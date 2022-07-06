package com.fakedetector.demo.api.controller;

import com.fakedetector.demo.api.dto.AnalyzingResponse;
import com.fakedetector.demo.service.MetadataService;
import com.fakedetector.demo.service.NeuralService;
import com.fakedetector.demo.service.ValidationService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
public class ImgController {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private NeuralService neuralService;

    @Autowired
    private ValidationService validationService;

    @SneakyThrows
    @PostMapping(value = "/analyzing", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity<AnalyzingResponse> analyzing(@RequestPart("file") MultipartFile file) {

        validationService.validateImage(file);

        return new ResponseEntity<>(
                AnalyzingResponse.builder()
                        .metadataResult(metadataService.analyze(file.getInputStream()))
                        .neuralResult(neuralService.analyze(file.getInputStream()))
                        .build(),
                HttpStatus.OK);
    }

}
