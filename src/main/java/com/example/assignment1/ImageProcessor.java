package com.example.assignment1;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {


    public static BufferedImage convertToBlackAndWhite(BufferedImage originalImage, Color targetColor, double threshold) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage bwImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(originalImage.getRGB(x, y));
                if (colorSimilarity(pixelColor, targetColor) < threshold) {
                    bwImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    bwImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return bwImage;
    }

}


