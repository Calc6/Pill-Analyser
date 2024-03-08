package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;


import javafx.scene.input.MouseEvent;



public class MainController {

    @FXML
    private ImageView bAndWImageView;

    @FXML
    private ImageView normalImageView;


    private Image originalImage;



    // Image file selection and display = 5%
    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            originalImage = new Image(selectedFile.toURI().toString(),bAndWImageView.getFitWidth(),bAndWImageView.getFitHeight(),false,true);
            bAndWImageView.setImage(originalImage);
            normalImageView.setImage(originalImage);
        }
    }



    @FXML
    private void onImageClick(MouseEvent event) {
        if (originalImage == null) return;

        int x = (int) event.getX();
        int y = (int) event.getY();

        PixelReader pixelReader = originalImage.getPixelReader();
        Color clickedColor = pixelReader.getColor(x, y);

        System.out.println("Clicked coordinates: (" + x + ", " + y + ")");
        System.out.println("Clicked color: " + clickedColor.toString());

        bAndw(clickedColor);

    }

    private void bAndw(Color targetColor) {
        if (originalImage == null) {
            System.out.println("No image loaded.");
            return;
        }

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        WritableImage updatedImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = updatedImage.getPixelWriter();


        int countWhite = 0;
        int countBlack = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                bAndWImageView.setImage(updatedImage);
                if (colorSimilarity(targetColor, color, 0.05)) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                    countWhite++;
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                    countBlack++;
                }
            }
        }

        bAndWImageView.setImage(updatedImage);
        System.out.println("Image view should now display the updated image.");

        System.out.println("White pixels: " + countWhite);
        System.out.println("Black pixels: " + countBlack);
    }

    @FXML
    private void handleResetToOriginal() {
        if (originalImage != null) {
            bAndWImageView.setImage(originalImage);
            System.out.println("Image view reset to the original image.");
        }
    }


    private boolean colorSimilarity(Color c1, Color c2, double tolerance) {
        double hueDifference = Math.abs(c1.getHue() - c2.getHue()) / 360.0; // Normalize hue difference
        double satDifference = Math.abs(c1.getSaturation() - c2.getSaturation());
        double brightDifference = Math.abs(c1.getBrightness() - c2.getBrightness());

        double weightedDiff = (hueDifference * 0.4) + (satDifference * 0.3) + (brightDifference * 0.3);
        return weightedDiff < tolerance;
    }







    @FXML
    private void handleClose(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}


