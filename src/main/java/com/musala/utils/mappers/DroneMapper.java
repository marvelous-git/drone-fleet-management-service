package com.musala.utils.mappers;

import com.musala.domain.Drone;
import com.musala.utils.dtos.DroneDto;
import com.musala.utils.request.DroneRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MedicationMapper.class)
public interface DroneMapper {
    Drone map(DroneRegistrationRequest droneRegistrationRequest);

    @Mapping(target = "medicationDtos", source = "medications")
    DroneDto map(Drone drone);
}
