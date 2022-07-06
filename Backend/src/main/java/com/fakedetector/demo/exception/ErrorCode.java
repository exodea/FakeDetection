package com.fakedetector.demo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    FD0000("FD0000", "The system throw an exception", "An unexpected error occurred. Please try later", HttpStatus.INTERNAL_SERVER_ERROR),
    FD1001("FD1001", "The system throw an validation exception", "Wrong extension of file. Supports only JPG", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String reason;
    private final String message;
    private final HttpStatus status;

    @Setter
    protected Map<String, Object> extra;

    ErrorCode(String code, String reason, String message, HttpStatus status){
        this.code = code;
        this.reason = reason;
        this.message = message;
        this.status = status;
    }

}
