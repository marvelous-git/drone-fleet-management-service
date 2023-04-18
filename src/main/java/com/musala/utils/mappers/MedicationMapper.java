package com.musala.utils.mappers;

import com.musala.domain.Medication;
import com.musala.utils.dtos.MedicationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    Medication map(MedicationDto medicationDto);

    MedicationDto map(Medication medication);
}
