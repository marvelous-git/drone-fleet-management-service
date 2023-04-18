package com.musala.service.api;

import com.musala.utils.request.ChangeDroneStateRequest;
import com.musala.utils.request.DroneRegistrationRequest;
import com.musala.utils.request.LoadMedicationRequest;
import com.musala.utils.responses.BatteryLevelResponse;
import com.musala.utils.responses.DroneResponse;

import java.util.Locale;

public interface DroneService {
    DroneResponse registerDrone(DroneRegistrationRequest droneRegistrationRequest, Locale locale);
    DroneResponse getAvailableDronesForLoading(Locale locale);
    DroneResponse loadMedications(String serialNumber, LoadMedicationRequest medicationRequest, Locale locale);
    DroneResponse getLoadedMedication(String serialNumber, Locale locale);
    BatteryLevelResponse getDroneBatteryLevel(String serialNumber, Locale locale);

    DroneResponse changeDroneState(ChangeDroneStateRequest request, Locale forLanguageTag);
}