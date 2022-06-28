package com.fakedetector.demo.service;

import com.fakedetector.demo.api.dto.ErrorMessageDto;
import com.fakedetector.demo.api.dto.NeuralResult;

import java.io.InputStream;

public interface NeuralService {

    NeuralResult analyze(InputStream imageFile) throws ErrorMessageDto;

}