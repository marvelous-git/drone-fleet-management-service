package com.musala.utils.request;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DroneRegistrationRequest {

    @NotBlank(message = "is a required parameter")
    @Size(min = 1, max = 100, message = "size must be between 1 and 100 characters long")
    private String serialNumber;

    @NotBlank(message = "is a required parameter")
    private String model;

    @NotNull(message = "is a required parameter")
    @Min(0)
    @Max(500)
    private Integer weightLimit;
}
