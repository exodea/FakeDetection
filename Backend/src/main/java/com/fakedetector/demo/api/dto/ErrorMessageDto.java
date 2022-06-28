package com.fakedetector.demo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ErrorMessageDto extends Throwable{
    String message;
}