package com.fakedetector.demo.validation;

import com.fakedetector.demo.exception.ErrorCode;

public interface Validation <T> {

    ErrorCode validate(T obj);

}
