package com.fakedetector.demo.api.controller;

import com.fakedetector.demo.api.dto.MetadataResult;
import com.fakedetector.demo.service.ElaGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
public class ElaGeneratorController {

    @Autowired
    ElaGenerator elaGenerator;

    @PostMapping(value = "/elaGenerator")
    @ResponseBody
    public MetadataResult metadataAnalyzing() throws IOException {
        elaGenerator.generate();
        return null;
    }

}