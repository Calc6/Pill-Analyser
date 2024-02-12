package com.example.assignment1;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {

    public static double colorSimilarity(Color c1, Color c2){
        double distance = Math.sqrt(Math.pow(c1.getRed() - c2.getRed(), 2) +
                Math.pow(c1.getGreen() - c2.getGreen(), 2) +
                Math.pow(c1.getBlue() - c2.getBlue(), 2));
        return distance;
    }


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


