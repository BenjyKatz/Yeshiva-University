package edu.yu.parallel.impl;
//337.595 score for 10x the sample image
import java.awt.image.BufferedImage;
import java.awt.*;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

import edu.yu.parallel.ImageTransformer;

public class ParallelTransformer implements ImageTransformer {

    private BufferedImage originalImage;

    public ParallelTransformer(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }
    
    /*
    @Override
    public BufferedImage resizeAndAdjustBrightness(double scaleFactor, double brightnessFactor) {
        throw new UnsupportedOperationException("Unimplemented method 'resizeAndAdjustBrightness'");
    }
    */

    @Override
    public BufferedImage resizeAndAdjustBrightness(double scaleFactor, double brightnessFactor) {
        int newWidth = (int) (originalImage.getWidth() * scaleFactor);
        int newHeight = (int) (originalImage.getHeight() * scaleFactor);
        
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // Resize the image in parallel
        resizeImageParallel(resizedImage, scaleFactor);

        // Adjust brightness in parallel

        adjustBrightnessParallel(resizedImage, brightnessFactor);

        return resizedImage;
    }

    private void resizeImageParallel(BufferedImage resizedImage, double scaleFactor) {
        int newWidth = resizedImage.getWidth();
        int newHeight = resizedImage.getHeight();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < newHeight; i++) {
                final int rowStart = i;
                final int rowEnd = i + (int)scaleFactor;
                futures.add(executor.submit(() -> {
                    Graphics2D graphics = resizedImage.createGraphics();
                    try {
                        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                        // Draw the image for the current row, scaling it to fit the new size
                        graphics.drawImage(originalImage,
                                0, rowStart, newWidth, rowEnd,
                                0, (int) (rowStart / scaleFactor), originalImage.getWidth(), (int) (rowEnd / scaleFactor),
                                null);
                    } finally {
                        graphics.dispose();
                    }
                }));
            }

            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                future.get();
            }
        }catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private void adjustBrightnessParallel(BufferedImage resizedImage, double brightnessFactor) {
        
        int newWidth = resizedImage.getWidth();
        int newHeight = resizedImage.getHeight();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int y = 0; y < newHeight; y++) {
                final int currentY = y;
                futures.add(executor.submit(() -> {
                    for (int x = 0; x < newWidth; x++) {
                        Color originalColor = new Color(resizedImage.getRGB(x, currentY), true);

                        int red = (int) Math.min(255, Math.max(0, originalColor.getRed() * brightnessFactor));
                        int green = (int) Math.min(255, Math.max(0, originalColor.getGreen() * brightnessFactor));
                        int blue = (int) Math.min(255, Math.max(0, originalColor.getBlue() * brightnessFactor));

                        Color newColor = new Color(red, green, blue, originalColor.getAlpha());
                        resizedImage.setRGB(x, currentY, newColor.getRGB());
                    }
                }));
            }

            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
    }
    
}
