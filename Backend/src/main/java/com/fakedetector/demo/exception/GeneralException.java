package com.fakedetector.demo.exception;

import java.util.Map;

public class GeneralException extends RuntimeException {

    public ErrorCode errorCode;

    public GeneralException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public GeneralException(ErrorCode errorCode, Map<String, Object> extra){
        errorCode.setExtra(extra);
        this.errorCode = errorCode;
    }

}
