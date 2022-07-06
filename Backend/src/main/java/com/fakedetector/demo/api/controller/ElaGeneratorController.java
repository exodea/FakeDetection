package com.fakedetector.demo.api.controller;

import com.fakedetector.demo.api.dto.MetadataResult;
import com.fakedetector.demo.service.ElaGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin
@RestController
public class ElaGeneratorController {

    @Autowired
    private ElaGenerator elaGenerator;

    @PostMapping(value = "/elaGenerator")
    @ResponseBody
    public MetadataResult metadataAnalyzing() throws IOException {
        elaGenerator.generate();
        return null;
    }

}