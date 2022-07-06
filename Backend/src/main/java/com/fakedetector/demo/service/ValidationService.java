package com.fakedetector.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface ValidationService {

    void validateImage(MultipartFile file);

}
