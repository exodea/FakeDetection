package com.fakedetector.demo.validation;

import com.fakedetector.demo.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Component
public class ExtentionValidation implements Validation<MultipartFile> {

    @Override
    public ErrorCode validate(MultipartFile file) {
        String filename = file.getOriginalFilename();
        Optional<String> extension = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
        if (extension.isEmpty()){
            return ErrorCode.FD1001;
        }
        if (!extension.get().equals("jpg") && !extension.get().equals("jpeg")){
            return ErrorCode.FD1001;
        }
        return null;
    }

}
