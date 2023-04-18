package com.musala.utils.exceptions;

import com.musala.utils.dtos.ErrorDetail;
import com.musala.utils.enums.I18Code;
import com.musala.utils.i18.api.MessageService;
import com.musala.utils.responses.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static com.musala.utils.dtos.Constants.*;

@ControllerAdvice
public class ApiExceptionHandler {
    @Autowired
    private MessageService messageService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> handleApiValidations(MethodArgumentNotValidException ex, WebRequest request){
        List<ErrorDetail> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String msg = String.format("%s : %s",fieldError.getField(),fieldError.getDefaultMessage());
            errors.add(ErrorDetail.builder().code(INVALID_PARAM).message(msg).build());
        });
        String message = messageService.getMessage(I18Code.MESSAGE_INVALID_REQUEST_SUPPLIED.getCode(),
                new String[]{}, request.getLocale());

        CommonResponse response = CommonResponse.builder()
                .errors(errors)
                .success(Boolean.FALSE)
                .message(message)
                .statusCode(FAILURE_INT_VALUE).build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
