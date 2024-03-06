package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color; // Correct import for JavaFX Color, replacing java.awt.Color
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.File;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MainController {

    @FXML
    private ImageView imageView;

    @FXML
    private Slider resizeSlider;


    @FXML
    private RadioButton originalRadioButton, grayscaleRadioButton, bwRadioButton;

    private ToggleGroup imageModeToggleGroup = new ToggleGroup();

    private Image originalImage;
    private Image pickedImage;

    private Color selectedColor;

    private Image blackAndWhiteImage;

    public void setBlackAndWhiteImage(Image image) {
        this.blackAndWhiteImage = image;
    }

    @FXML
    private Slider thresholdSlider;

    @FXML
    private Label thresholdValueLabel;

    @FXML
    private void initialize() {
        setupSlider();
        setupImageModeToggleGroup();
        thresholdValueLabel.setText(String.format("Threshold Value: %.3f", thresholdSlider.getValue()));

        thresholdSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            thresholdValueLabel.setText(String.format("Threshold Value: %.3f", newValue.doubleValue()));
        });
    }

    private void setupSlider() {
        resizeSlider.setMin(0.5);
        resizeSlider.setMax(2.0);
        resizeSlider.setValue(1.0);
        imageView.fitWidthProperty().bind(resizeSlider.valueProperty().multiply(200));
        imageView.fitHeightProperty().bind(resizeSlider.valueProperty().multiply(150));
    }

    private void setupImageModeToggleGroup() {
        originalRadioButton.setToggleGroup(imageModeToggleGroup);
        grayscaleRadioButton.setToggleGroup(imageModeToggleGroup);
        bwRadioButton.setToggleGroup(imageModeToggleGroup);
        originalRadioButton.setSelected(true);
        imageModeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> handleImageModeChange());
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            originalImage = new Image(selectedFile.toURI().toString());
            imageView.setImage(originalImage);
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

        convertColors(clickedColor);
    }


    private void convertColors(Color targetColor) {
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
                imageView.setImage(updatedImage);
                if (colorSimilarity(targetColor, color, 0.05)) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                    countWhite++;
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                    countBlack++;
                }
            }
        }

        imageView.setImage(updatedImage);
        System.out.println("Image view should now display the updated image.");

        setBlackAndWhiteImage(updatedImage);

        System.out.println("White pixels: " + countWhite);
        System.out.println("Black pixels: " + countBlack);
    }

    private boolean colorSimilarity(Color c1, Color c2, double tolerance) {
        double rDiff = Math.abs(c1.getRed() - c2.getRed());
        double gDiff = Math.abs(c1.getGreen() - c2.getGreen());
        double bDiff = Math.abs(c1.getBlue() - c2.getBlue());
        return (rDiff + gDiff + bDiff) / 3 < tolerance;
    }

    private boolean isColorRed(Color color) {
        return color.getRed() > 0.8 && color.getGreen() < 0.2 && color.getBlue() < 0.2;
    }


    @FXML
    private void handleImageModeChange() {
        if (bwRadioButton.isSelected()) {
            try {
                blackAndWhite(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (grayscaleRadioButton.isSelected()) {
            // Handle grayscale mode
        } else if (originalRadioButton.isSelected()) {
            imageView.setImage(originalImage);
        }
    }

    public void blackAndWhite(ImageView imageView) throws IndexOutOfBoundsException {
        try {
            int width = (int) imageView.getImage().getWidth();
            int height = (int) imageView.getImage().getHeight();
            WritableImage bwImage = new WritableImage(width, height);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int p = imageView.getImage().getPixelReader().getArgb(x, y);

                    int a = (p >> 24) & 255;
                    int r = (p >> 16) & 255;
                    int g = (p >> 8) & 255;
                    int b = p & 255;

                    int avg = (r + g + b) / 3;

                    p = (a << 24) | (avg << 16) | (avg << 8) | avg;

                    bwImage.getPixelWriter().setArgb(x, y, p);
                }
            }
            imageView.setImage(bwImage);
            setBlackAndWhiteImage(bwImage);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    @FXML
    private void handleClose(ActionEvent event) {
        // Close the current stage (window)
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}


