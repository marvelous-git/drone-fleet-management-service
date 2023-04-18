package com.musala.utils.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.musala.utils.dtos.ErrorDetail;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class CommonResponse {
    private int statusCode;
    private boolean success;
    private String message;

    private List<ErrorDetail> errors;

    @Override
    public String toString() {
        return "statusCode=" + statusCode +
                ", success=" + success +
                ", message='" + message + '\'';
    }
}