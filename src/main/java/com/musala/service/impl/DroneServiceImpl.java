package com.musala.service.impl;

import com.musala.domain.Drone;
import com.musala.domain.DroneStateTransitionMachine;
import com.musala.domain.Medication;
import com.musala.repository.DroneRepository;
import com.musala.repository.MedicationRepository;
import com.musala.service.api.DroneService;
import com.musala.utils.dtos.DroneDto;
import com.musala.utils.dtos.ErrorDetail;
import com.musala.utils.dtos.MedicationDto;
import com.musala.utils.enums.*;
import com.musala.utils.exceptions.DroneStateTransitionException;
import com.musala.utils.i18.api.MessageService;
import com.musala.utils.mappers.DroneMapper;
import com.musala.utils.mappers.MedicationMapper;
import com.musala.utils.request.ChangeDroneStateRequest;
import com.musala.utils.request.DroneRegistrationRequest;
import com.musala.utils.request.LoadMedicationRequest;
import com.musala.utils.responses.BatteryLevelResponse;
import com.musala.utils.responses.DroneResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.musala.utils.dtos.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {

    private final DroneRepository droneRepository;

    private MedicationRepository medicationRepository;

    private final MessageService messageService;

    private final DroneMapper droneMapper;

    private final MedicationMapper medicationMapper;

    @Value("${limit.drone.fleet}")
    private long droneFleet;
    @Value("${limit.drone.battery}")
    private int batteryLimit;

    @Override
    public DroneResponse registerDrone(DroneRegistrationRequest droneRegistrationRequest, Locale locale) {
        String message;
        if (DroneModel.getEnumFromString(droneRegistrationRequest.getModel()) == null) {

            String errorMsg = messageService.getMessage(I18Code.MESSAGE_DRONE_INVALID_DRONE_MODEL_SUPPLIED.getCode(),
                    new String[]{Arrays.toString(DroneModel.values())}, locale);

            ErrorDetail errorDetail = ErrorDetail.builder()
                    .code(INVALID_PARAM)
                    .message(errorMsg).build();

            message = messageService.getMessage(I18Code.MESSAGE_INVALID_REQUEST_SUPPLIED.getCode(),
                    new String[]{}, locale);

            return createErrorResponse(FAILURE_INT_VALUE, message, errorDetail);
        }

        Boolean droneAlreadyExists = droneRepository.existsBySerialNumberAndEntityStatusIsNot(droneRegistrationRequest.getSerialNumber(), EntityStatus.DELETED);

        if (droneAlreadyExists) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_ALREADY_EXISTS.getCode(),
                    new String[]{}, locale);
            ErrorDetail errorDetail = ErrorDetail.builder()
                    .code(DUPLICATE_ENTRY)
                    .message(message).build();

            return createErrorResponse(FAILURE_INT_VALUE, message, errorDetail);
        }

        long droneCount = droneRepository.countByEntityStatusIsNot(EntityStatus.DELETED);

        if (droneCount >= droneFleet) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_FLEET_SIZE_VIOLATION.getCode(),
                    new String[]{}, locale);

            ErrorDetail errorDetail = ErrorDetail.builder()
                    .code(DRONE_FLEET_SIZE_VIOLATION)
                    .message(messageService.getMessage(I18Code.MESSAGE_DRONE_REGISTERED_EXCEEDED_LIMIT.getCode(),
                            new String[]{Long.toString(droneFleet)}, locale)).build();

            return createErrorResponse(FAILURE_INT_VALUE, message, errorDetail);
        }

        Drone drone = droneRepository.save(droneMapper.map(droneRegistrationRequest));

        DroneDto droneDto = droneMapper.map(drone);

        message = messageService.getMessage(I18Code.MESSAGE_DRONE_REGISTRATION_SUCCESS.getCode(),
                new String[]{}, locale);

        return DroneResponse.builder()
                .success(Boolean.TRUE)
                .statusCode(CREATE_SUCCESS_INT_VALUE)
                .message(message)
                .droneDto(droneDto)
                .build();
    }


    @Override
    public DroneResponse getAvailableDronesForLoading(Locale locale) {
        String message;
        List<Drone> availableDrones = droneRepository
                .findByStateAndEntityStatusIsNotAndBatteryCapacityGreaterThan(DroneState.IDLE, EntityStatus.DELETED, 25);
        if (availableDrones.isEmpty()) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_NOT_AVAILABLE_FOR_LOADING.getCode(),
                    new String[]{}, locale);

            return createErrorResponse(NOT_FOUND_INT_VALUE, message, null);
        }
        List<DroneDto> droneDtoList = availableDrones.stream()
                .map(droneMapper::map).collect(Collectors.toList());

        message = messageService.getMessage(I18Code.MESSAGE_DRONE_RETRIEVED_SUCCESSFULLY.getCode(),
                new String[]{}, locale);

        return DroneResponse.builder()
                .success(Boolean.TRUE)
                .statusCode(SUCCESS_INT_VALUE)
                .message(message)
                .droneDtos(droneDtoList)
                .build();
    }

    @Override
    public DroneResponse loadMedications(String serialNumber, LoadMedicationRequest medicationRequest, Locale locale) {
        String message;
        Optional<Drone> drone = droneRepository.findBySerialNumberAndEntityStatusIsNot(serialNumber, EntityStatus.DELETED);
        if (!drone.isPresent()) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_NOT_FOUND.getCode(),
                    new String[]{}, locale);

            return createErrorResponse(NOT_FOUND_INT_VALUE, message, null);
        }

        Integer totalWeight = medicationRequest.getMedicationDtos().stream().mapToInt(MedicationDto::getWeight).sum();

        if (totalWeight > drone.get().getWeightLimit()) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_WEIGHT_LIMIT_VIOLATION.getCode(),
                    new String[]{}, locale);

            return createErrorResponse(FAILURE_INT_VALUE, message, null);
        }

        if (drone.get().getBatteryCapacity() < batteryLimit) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_BATTERY_LOW.getCode(),
                    new String[]{}, locale);

            return createErrorResponse(FAILURE_INT_VALUE, message, null);
        }

        List<Medication> medicationList = medicationRequest.getMedicationDtos().stream().map(medicationDto -> {
            Medication medication = medicationMapper.map(medicationDto);
            medication.setDrone(drone.get());
            return medication;
        }).collect(Collectors.toList());

        drone.get().setMedications(medicationList);

        try {
            DroneStateTransitionMachine.get().changeState(drone.get(), DroneStateAction.LOAD);

            DroneStateTransitionMachine.get().changeState(drone.get(), DroneStateAction.INSPECT_LOAD);
        } catch (DroneStateTransitionException exception) {
            return createErrorResponse(FAILURE_INT_VALUE, exception.getMessage(), null);
        }

        Drone loadedDrone = droneRepository.save(drone.get());

        DroneDto loadedDroneDto = droneMapper.map(loadedDrone);

        message = messageService.getMessage(I18Code.MESSAGE_DRONE_LOADED_SUCCESSFULLY.getCode(),
                new String[]{}, locale);

        return DroneResponse.builder()
                .success(Boolean.TRUE)
                .statusCode(SUCCESS_INT_VALUE)
                .message(message)
                .droneDto(loadedDroneDto)
                .build();
    }

    @Override
    public DroneResponse getLoadedMedication(String serialNumber, Locale locale) {
        String message;
        Optional<Drone> drone = droneRepository.findBySerialNumberAndEntityStatusIsNot(serialNumber, EntityStatus.DELETED);
        if (drone.isPresent()) {
            DroneDto droneDto = droneMapper.map(drone.get());
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_RETRIEVED_SUCCESSFULLY.getCode(),
                    new String[]{}, locale);
            return DroneResponse.builder()
                    .success(Boolean.TRUE)
                    .statusCode(SUCCESS_INT_VALUE)
                    .message(message)
                    .droneDto(droneDto)
                    .build();
        }
        message = messageService.getMessage(I18Code.MESSAGE_DRONE_NOT_FOUND.getCode(),
                new String[]{}, locale);

        return createErrorResponse(NOT_FOUND_INT_VALUE, message, null);
    }

    @Override
    public BatteryLevelResponse getDroneBatteryLevel(String serialNumber, Locale locale) {
        String message;
        Optional<Drone> drone = droneRepository.findBySerialNumberAndEntityStatusIsNot(serialNumber, EntityStatus.DELETED);
        if (drone.isPresent()) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_BATTERY_LEVEL_RETRIEVED_SUCCESSFULLY.getCode(),
                    new String[]{}, locale);
            return BatteryLevelResponse.builder()
                    .success(Boolean.TRUE)
                    .statusCode(SUCCESS_INT_VALUE)
                    .message(message)
                    .batteryLevel(drone.get().getBatteryCapacity())
                    .build();
        }
        message = messageService.getMessage(I18Code.MESSAGE_DRONE_NOT_FOUND.getCode(),
                new String[]{}, locale);

        return BatteryLevelResponse.builder()
                .message(message)
                .statusCode(NOT_FOUND_INT_VALUE)
                .success(Boolean.FALSE).build();
    }

    // Only For Testing purposes
    @Override
    public DroneResponse changeDroneState(ChangeDroneStateRequest request, Locale locale) {
        String message;

        DroneStateAction action;

        if (DroneStateAction.getEnumFromString(request.getAction()) == null) {

            String errorMsg = messageService.getMessage(I18Code.MESSAGE_DRONE_INVALID_DRONE_ACTION_SUPPLIED.getCode(),
                    new String[]{Arrays.toString(DroneStateAction.values())}, locale);

            ErrorDetail errorDetail = ErrorDetail.builder()
                    .code(INVALID_PARAM)
                    .message(errorMsg).build();

            message = messageService.getMessage(I18Code.MESSAGE_INVALID_REQUEST_SUPPLIED.getCode(),
                    new String[]{}, locale);

            return createErrorResponse(FAILURE_INT_VALUE, message, errorDetail);
        }

        action = DroneStateAction.getEnumFromString(request.getAction());

        Optional<Drone> drone = droneRepository.findBySerialNumberAndEntityStatusIsNot(request.getSerialNumber(), EntityStatus.DELETED);

        if (!drone.isPresent()) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_NOT_FOUND.getCode(),
                    new String[]{}, locale);
            return createErrorResponse(NOT_FOUND_INT_VALUE, message, null);
        }

        if (action.equals(DroneStateAction.LOAD) || action.equals(DroneStateAction.REGISTER)) {
            message = messageService.getMessage(I18Code.MESSAGE_DRONE_WRONG_METHOD.getCode(),
                    new String[]{}, locale);
            return createErrorResponse(FAILURE_INT_VALUE, message, null);
        }

        try {
            DroneStateTransitionMachine.get().changeState(drone.get(), action);
            if (action.equals(DroneStateAction.ARRIVE)) {
                removeMedications(drone.get());
            }
        } catch (DroneStateTransitionException exception) {
            return createErrorResponse(FAILURE_INT_VALUE, exception.getMessage(), null);
        }
        Drone updatedDrone = droneRepository.save(drone.get());

        DroneDto updatedDroneDto = droneMapper.map(updatedDrone);

        message = messageService.getMessage(I18Code.MESSAGE_DRONE_LOADED_SUCCESSFULLY.getCode(),
                new String[]{}, locale);

        return DroneResponse.builder()
                .success(Boolean.TRUE)
                .statusCode(SUCCESS_INT_VALUE)
                .message(message)
                .droneDto(updatedDroneDto)
                .build();
    }

    private static DroneResponse createErrorResponse(int statusCode, String message, ErrorDetail errorDetail) {
        List<ErrorDetail> errorDetails = null;
        if (errorDetail != null) {
            errorDetails = Collections.singletonList(errorDetail);
        }
        return DroneResponse.builder()
                .success(Boolean.FALSE)
                .statusCode(statusCode)
                .message(message)
                .errors(errorDetails)
                .build();
    }

    private void removeMedications(Drone drone) {
        List<Medication> medications = drone.getMedications();
        medications.forEach(medication -> medication.setEntityStatus(EntityStatus.DELETED));
        drone.setMedications(medications);
    }

}
