package com.fakedetector.demo.service.impl;

import com.fakedetector.demo.service.ElaGenerator;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static ij.io.FileSaver.setJpegQuality;

@Log4j
@Service
public class ElaGeneratorImpl implements ElaGenerator {

    private final String DIR_GENERAL_PATH = "dataset/";
    private final String DIR_ELA_PATH = DIR_GENERAL_PATH + "ela/";
    private final String DIR_SOURCE_PATH = DIR_GENERAL_PATH + "source/";
    private final Dimension DIMENSION = new Dimension(500, 500); // was changed to 500
    private final int QUALITY = 95;
    private StringBuilder filenames = new StringBuilder();

    @Override
    public void generate() {
        File elaDir = new File(DIR_ELA_PATH);
        File sourceDir = new File(DIR_SOURCE_PATH);

        createFolderOnDemand(DIR_GENERAL_PATH);
        createFolderOnDemand(elaDir);
        createFolderOnDemand(sourceDir);

        log.info("Creating ela dataset was started");
        createElaImagesRecurrently(sourceDir);
        log.info("Creating ela dataset was finished");
    }

    private void createElaImagesRecurrently(File folder) {
        File[] images = folder.listFiles(File::isFile);
        File[] folders = folder.listFiles(File::isDirectory);

        String newElaPath = folder.getPath().replace("\\","/").replaceFirst(DIR_SOURCE_PATH, DIR_ELA_PATH);
        Arrays.stream(images).forEach(file -> {
            try {
                System.out.println(file.getName());
                filenames.append(file.getName()).append("\n");
                generateElaImage(file, newElaPath);
            } catch (IOException e) {
                log.info("Error in generateElaImage.method");
                e.printStackTrace();
                return;
            }
        });
        deleteRedundantFiles(newElaPath);

////        todo: delete
//        log.info("Creating file with filenames was started");
//        try(FileWriter writer = new FileWriter(DIR_GENERAL_PATH+folder.getName()+".txt", false))
//        {
//            writer.write(filenames.toString());
//            writer.flush();
//        }
//        catch(IOException ex){
//            System.out.println(ex.getMessage());
//        }
//        filenames = new StringBuilder();
//        log.info("Creating file with filenames was finished");
////        todo: -end

        Arrays.stream(folders).forEach(file -> {
            String newFolderPath = file.getPath().replace("\\","/").replaceFirst(DIR_SOURCE_PATH, DIR_ELA_PATH);
            createFolderOnDemand(newFolderPath);

            log.info("\nfolder: " + file.getName());
            createElaImagesRecurrently(file);
        });
    }

    private void generateElaImage(File sourceImg, String newPath) throws IOException{
        Image img = ImageIO.read(sourceImg);

        ImagePlus orig = new ImagePlus("Source Image", img);
        FileSaver fs = new FileSaver(orig);
        setJpegQuality(100);
        fs.saveAsJpeg(newPath+"/-original.jpg");

        setJpegQuality(QUALITY);
        fs.saveAsJpeg(newPath+"/-resaved.jpg");
        ImagePlus resaved = new ImagePlus(newPath+"/-resaved.jpg");

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
        fser.saveAsJpeg(newPath+"/"+sourceImg.getName());
    }

    private void createFolderOnDemand(String path){
        File folder = new File(path);
        createFolderOnDemand(folder);
    }

    private void createFolderOnDemand(File folder){
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    private void deleteRedundantFiles(String path){
        new File(path+"/-original.jpg").delete();
        new File(path+"/-resaved.jpg").delete();
    }
}