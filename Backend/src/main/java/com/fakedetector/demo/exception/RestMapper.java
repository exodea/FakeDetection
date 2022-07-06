package com.fakedetector.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestMapper extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected ResponseEntity<ErrorCode> handle(RuntimeException ex) {
        ErrorCode errorCode;

        if(ex instanceof GeneralException){
            errorCode = ((GeneralException) ex).errorCode;
        }else{
            errorCode = ErrorCode.FD0000;
        }

        log.error(errorCode.toString(), ex);

        return new ResponseEntity<>(errorCode, errorCode.getStatus());
    }

}
