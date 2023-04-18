package com.musala.utils.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDroneStateRequest {
    @NotBlank(message = "is a required parameter")
    @Size(min = 1, max = 100, message = "size must be between 1 and 100 characters long")
    private String serialNumber;

    @NotBlank(message = "is a required parameter")
    private String action;
}
