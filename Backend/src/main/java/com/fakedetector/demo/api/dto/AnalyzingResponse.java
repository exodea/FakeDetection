package com.fakedetector.demo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AnalyzingResponse {

    MetadataResult metadataResult;

    NeuralResult neuralResult;

}