package com.fakedetector.demo.api.controller;

import com.fakedetector.demo.api.dto.AnalyzingResponse;
import com.fakedetector.demo.api.dto.MetadataResult;
import com.fakedetector.demo.service.MetadataService;
import com.fakedetector.demo.service.NeuralService;
import com.fakedetector.demo.weka.filters.unsupervised.instance.imagefilter.BinaryPatternsPyramidFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;

import java.io.IOException;

@CrossOrigin
@RestController
public class ImgController {

    @Autowired
    MetadataService metadataService;

    @Autowired
    NeuralService neuralService;

    @PostMapping(value = "/analyzing", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public AnalyzingResponse analyzing(@RequestPart("file") MultipartFile file) throws IOException { // todo delete throws
        return AnalyzingResponse.builder()
                .metadataResult(metadataService.analyze(file.getInputStream()))
                .neuralResult(neuralService.analyze(file.getInputStream()))
                .build();
    }

    @PostMapping(value = "/test")
    @ResponseBody
    public MetadataResult test() throws Exception {
        J48 j48 = (J48) SerializationHelper.read("demo/nnet/neuro.model");

        ConverterUtils.DataSource source = new ConverterUtils.DataSource("C:\\Users\\Пользователь\\Desktop\\dataset for learning\\atable test.arff");
        Instances testSet = source.getDataSet();
        testSet.setClassIndex(testSet.numAttributes() - 1);

        BinaryPatternsPyramidFilter filter = new BinaryPatternsPyramidFilter();
        filter.setInputFormat(testSet);
        Instances filteredSet = Filter.useFilter(testSet, filter);
        filteredSet.deleteAttributeAt(0);
        filteredSet.setClassIndex(filteredSet.numAttributes() - 1);

//        j48.buildClassifier(filteredSet);

        int correct = 0;
        int incorrect = 0;
        System.out.println("start checking");
        for (int i = 0; i < filteredSet.numInstances(); i++) {
            double pred = j48.classifyInstance(filteredSet.instance(i));

            String actual = filteredSet.classAttribute().value((int) filteredSet.instance(i).classValue());
            String predicted = filteredSet.classAttribute().value((int) pred);
            if (actual.equals(predicted)) {
                correct++;
            } else {
                incorrect++;
            }
//            System.out.print("ID: " + i);
//            System.out.print(", actual: " + filteredSet.classAttribute().value((int) filteredSet.instance(i).classValue()));
//            System.out.println(", predicted: " + filteredSet.classAttribute().value((int) pred));
        }
        System.out.println("stop checking");
        System.out.println("correct:"+correct);
        System.out.println("incorrect:"+incorrect);


        return null;
    }

//    @PostMapping(value = "/ganAnalyzing", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public @ResponseBody byte[] ganAnalyzing(@RequestPart("file") MultipartFile file) throws IOException {
//        System.out.println("ganAnalyzing: successfully received");
//
//        MBFImage image = ImageUtilities.readMBF(file.getInputStream());
//        MBFImage clone = image.clone();
//
//        // face detection
//        FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(40);
//        List<DetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(clone));
//        for( DetectedFace face : faces ) {
//            clone.drawShape(face.getBounds(), RGBColour.RED);
//        }
//
//        return MBFItoBytes(clone);
//    }
//
//
//
//    private byte[] MBFItoBytes(MBFImage image){
//        byte[] bytes = null;
//        try {
//            BufferedImage bimg = ImageUtilities.createBufferedImageForDisplay(image);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(bimg, "jpg", baos);
//            bytes = baos.toByteArray();
//        }catch(IOException e){
//            System.out.println();
//            e.printStackTrace();
//        }
//        return bytes;
//    }

}
