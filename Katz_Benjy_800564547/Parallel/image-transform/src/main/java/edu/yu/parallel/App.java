package edu.yu.parallel;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.parallel.impl.ParallelTransformer;
import edu.yu.parallel.impl.ParallelTransformerCache;
import edu.yu.parallel.impl.ParallelTransformerRegions;
import edu.yu.parallel.impl.ParallelTransformerSameTime;
import edu.yu.parallel.impl.SerialTransformer;
import edu.yu.parallel.impl.StreamTransformer;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    
    public static void main(String[] args) {

        logger.info("Application starting");
        BufferedImage originalImage;
        BufferedImage resizedImg = null;
        try{
            originalImage = Imaging.getBufferedImage(new File("earth.png"));
            var transformer = new StreamTransformer(originalImage);
            //var transformer = new SerialTransformer(originalImage);
            resizedImg = transformer.resizeAndAdjustBrightness(10, 0.5);
            
        }
        catch(ImageReadException | IOException e){
            e.printStackTrace();
        }
        String outputPath = "resizedImage.png";
        String formatName = "png";

        // Save the BufferedImage
        saveImage(resizedImg, outputPath, formatName);
        


        logger.info("Application exiting");
    }
    public static void saveImage(BufferedImage image, String outputPath, String formatName) {
        try {
            File output = new File(outputPath);
            ImageIO.write(image, formatName, output);
            System.out.println("Image saved successfully to: " + output.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
