package edu.yu.parallel.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import edu.yu.parallel.ImageTransformer;

public class StreamTransformer implements ImageTransformer {
    private final BufferedImage originalImage;

    public StreamTransformer(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    @Override
    public BufferedImage resizeAndAdjustBrightness(double scaleFactor, double brightnessFactor) {
        // Determine the new width and height based on the scale factor
        int newWidth = (int) (originalImage.getWidth() * scaleFactor);
        int newHeight = (int) (originalImage.getHeight() * scaleFactor);

        // Create an empty image of the desired size
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // Resize row by row using streams
        IntStream.range(0, newHeight).parallel().forEach(y -> {
            Graphics2D graphics = resizedImage.createGraphics();
            try {
                graphics.drawImage(originalImage, 0, y, newWidth, y + (int)scaleFactor, 0, (int) (y / scaleFactor), originalImage.getWidth(), (int) ((y + (int)scaleFactor) / scaleFactor), null);
            } finally {
                graphics.dispose();
            }
        });
        
        // Adjust brightness pixel by pixel using streams
        IntStream.range(0, newHeight).parallel().forEach(y ->
                IntStream.range(0, newWidth).forEach(x -> {
                    // Get the original color of the pixel
                    Color originalColor = new Color(resizedImage.getRGB(x, y), true);

                    // Adjust the brightness of the pixel
                    int red = (int) Math.min(255, Math.max(0, originalColor.getRed() * brightnessFactor));
                    int green = (int) Math.min(255, Math.max(0, originalColor.getGreen() * brightnessFactor));
                    int blue = (int) Math.min(255, Math.max(0, originalColor.getBlue() * brightnessFactor));

                    // Set the new color of the pixel
                    Color newColor = new Color(red, green, blue, originalColor.getAlpha());
                    resizedImage.setRGB(x, y, newColor.getRGB());
                })
        );

        // Return the resized image
        return resizedImage;
    }
}