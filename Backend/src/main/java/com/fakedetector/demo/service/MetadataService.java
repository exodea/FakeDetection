package com.fakedetector.demo.service;

import com.fakedetector.demo.api.dto.MetadataResult;

import java.io.InputStream;

public interface MetadataService {

    MetadataResult analyze(InputStream imageFile);

}