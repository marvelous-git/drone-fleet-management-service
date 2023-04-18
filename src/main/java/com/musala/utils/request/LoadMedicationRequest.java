package com.musala.utils.request;

import com.musala.utils.dtos.MedicationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoadMedicationRequest {
    @Valid
    private List<MedicationDto> medicationDtos;
}
