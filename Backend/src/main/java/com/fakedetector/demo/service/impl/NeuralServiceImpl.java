package com.fakedetector.demo.service.impl;

import com.fakedetector.demo.api.dto.NeuralResult;
import com.fakedetector.demo.exception.ErrorCode;
import com.fakedetector.demo.exception.GeneralException;
import com.fakedetector.demo.service.NeuralService;
import com.fakedetector.demo.weka.filters.unsupervised.instance.imagefilter.BinaryPatternsPyramidFilter;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ij.io.FileSaver.setJpegQuality;

@Slf4j
@Service
public class NeuralServiceImpl implements NeuralService {

    private final int QUALITY = 95;
    private final Dimension DIMENSION = new Dimension(500, 500); // todo: check resize, not cutting
    private final String BASE_PATH = "tmp/";

    @Override
    public NeuralResult analyze(InputStream imageFile) {

        Image img;

        try {
            img = ImageIO.read(imageFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            Map<String, Object> extra = new HashMap<>();
            extra.put("debug", "Unable to create image from InputStream");
            throw new GeneralException(ErrorCode.FD0000, extra);
        }

        File file = new File(BASE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }

        String origPath = BASE_PATH + "-original.jpg";
        String resavedPath = BASE_PATH + "-resaved.jpg";
        String elaPath = BASE_PATH + "-ELA.jpg";

        ImagePlus orig = new ImagePlus("Source Image", img);
        FileSaver fs = new FileSaver(orig);
        setJpegQuality(100);
        fs.saveAsJpeg(origPath);

        setJpegQuality(QUALITY);
        fs.saveAsJpeg(resavedPath);
        ImagePlus resaved = new ImagePlus(resavedPath);

        ImagePlus diff = new ImageCalculator().run("create difference", orig, resaved);
        ImageProcessor ip = diff.getProcessor();

        new ContrastEnhancer().stretchHistogram(diff, 0.05);

        ImageProcessor imp; //todo: here is cropping of image
        if (ip.getWidth() > ip.getHeight()) {
            Rectangle rec = new Rectangle(0, 0, ip.getHeight(), ip.getHeight());
            ip.setRoi(rec);
            imp = ip.crop();
        } else {
            Rectangle rec = new Rectangle(0, 0, ip.getWidth(), ip.getWidth());
            ip.setRoi(rec);
            imp = ip.crop();
        }

        imp = imp.resize((int) DIMENSION.getWidth(), (int) DIMENSION.getHeight());

        ImagePlus er = new ImagePlus("Source Image", imp);
        FileSaver fser = new FileSaver(er);
        setJpegQuality(100);
        fser.saveAsJpeg(elaPath);

        J48 j48 = null;
        String result = null;
        try {
            j48 = (J48) SerializationHelper.read("nnet/FCTHFilter.model");

            ConverterUtils.DataSource source = new ConverterUtils.DataSource("tmp/table.arff");
            Instances testSet = source.getDataSet();
            testSet.setClassIndex(testSet.numAttributes() - 1);

            BinaryPatternsPyramidFilter filter = new BinaryPatternsPyramidFilter();
            filter.setInputFormat(testSet);
            Instances filteredSet = Filter.useFilter(testSet, filter);
            filteredSet.deleteAttributeAt(0);
//            filteredSet.setClassIndex(filteredSet.numAttributes() - 1); //TODO: is it need?

    //        j48.buildClassifier(filteredSet);

            for (int i = 0; i < filteredSet.numInstances(); i++) {
                double pred = j48.classifyInstance(filteredSet.instance(i));
                System.out.print("ID: " + i);
                System.out.print(", actual: " + filteredSet.classAttribute().value((int) filteredSet.instance(i).classValue()));
                System.out.println(", predicted: " + filteredSet.classAttribute().value((int) pred));

                result = filteredSet.classAttribute().value((int) pred);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return NeuralResult.builder()
                .originalProbability(result)
                .build();
    }

}