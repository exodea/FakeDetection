package com.fakedetector.demo.service.impl;

import com.fakedetector.demo.exception.ErrorCode;
import com.fakedetector.demo.exception.GeneralException;
import com.fakedetector.demo.service.ValidationService;
import com.fakedetector.demo.validation.ExtentionValidation;
import com.fakedetector.demo.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ValidationServiceImpl implements ValidationService {

    private final ExtentionValidation extentionValidation;

    @Override
    public void validateImage(MultipartFile file) {
        validate(file, extentionValidation);
    }

    private void validate(Object obj, Validation validation){
        ErrorCode errorCode = validation.validate(obj);
        if (errorCode != null){
            throw new GeneralException(errorCode);
        }
    }

}
