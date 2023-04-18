package com.musala.utils.responses;

import com.musala.utils.dtos.DroneDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DroneResponse extends CommonResponse {
    private DroneDto droneDto;
    private List<DroneDto> droneDtos;
}
