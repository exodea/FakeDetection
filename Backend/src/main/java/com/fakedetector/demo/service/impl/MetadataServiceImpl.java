package com.fakedetector.demo.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.fakedetector.demo.api.dto.MetadataResult;
import com.fakedetector.demo.exception.ErrorCode;
import com.fakedetector.demo.exception.GeneralException;
import com.fakedetector.demo.service.MetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MetadataServiceImpl implements MetadataService {

    @Override
    public MetadataResult analyze(InputStream imageFile) {
        StringBuilder extractedMetadata = new StringBuilder();
        Metadata metadata;

        try {
            metadata = ImageMetadataReader.readMetadata(imageFile);
        } catch (ImageProcessingException | IOException e) {
            log.error(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            Map<String, Object> extra = new HashMap<>();
            extra.put("debug", "Unable to read metadata");
            throw new GeneralException(ErrorCode.FD0000, extra);
        }

        metadata.getDirectories().forEach(directory -> {
            extractedMetadata.append(directory.getName());
            directory.getTags().forEach(tag ->
                extractedMetadata.append(tag).append("\n")
            );
        });

        int real = 1;
        StringBuilder fakeReason = new StringBuilder();

        String[] splitMetadata = extractedMetadata.toString().split("\n");
        for (String string : splitMetadata) { // TODO: change on method
            String fakeReasonString = fakeReason.toString();
            if (string.toUpperCase().contains("ADOBE")) {
                real = 0;
                if (!fakeReasonString.contains("Detected Adobe Tag")) {
                    fakeReason.append("Detected Adobe Tag").append("\n");
                }
            }
            if (string.toUpperCase().contains("PHOTOSHOP")) {
                real = 0;
                if (!fakeReasonString.contains("Detected Photoshop Tag")) {
                    fakeReason.append("Detected Photoshop Tag").append("\n");
                }
            }
            if (string.toUpperCase().contains("GIMP")) {
                real = 0;
                if (!fakeReasonString.contains("Detected Gimp Tag")) {
                    fakeReason.append("Detected Gimp Tag").append("\n");
                }
            }
            if (string.toUpperCase().contains("COREL")) {
                real = 0;
                if (!fakeReasonString.contains("Detected Corel Tag")) {
                    fakeReason.append("Detected Corel Tag").append("\n");
                }
            }
            if (string.toUpperCase().contains("PAINT")) {
                real = 0;
                if (!fakeReasonString.contains("Detected Paint Tag")) {
                    fakeReason.append("Detected Paint Tag").append("\n");
                }
            }
            if (string.toUpperCase().contains("PIXLR")) {
                real = 0;
                if (!fakeReasonString.contains("Detected Pixlr Tag")) {
                    fakeReason.append("Detected Pixlr Tag").append("\n");
                }
            }
            if (string.contains("Software - Google")) {
                real = 0;
                if (!fakeReasonString.contains("Google's Signature Found: Possibly Google+")) {
                    fakeReason.append("Google's Signature Found: Possibly Google+").append("\n");
                }
            }
        }

        int ctr = splitMetadata.length;
        if (ctr < 15) {
            fakeReason.append("Very Low Metadata Content. Edited").append("\n");
        } else if (ctr < 21) {
            fakeReason.append("Low Metadata Content. Edited").append("\n");
        } else if (ctr < 30) {
            fakeReason.append("Average Metadata Content. Edited").append("\n");
        }

        log.info("Fakeness reasons = " + fakeReason);
        log.info("Number of metadata fields = " + ctr);

        return MetadataResult.builder()
                .originalProbability(real)
                .fakeReasons(fakeReason.toString())
                .metadata(extractedMetadata.toString())
                .build();
    }

}