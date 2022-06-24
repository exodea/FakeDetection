package com.fakedetector.demo.api.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
public class MetadataResult {

    private double originalProbability;

    private String fakeReasons;

    private String metadata;

}