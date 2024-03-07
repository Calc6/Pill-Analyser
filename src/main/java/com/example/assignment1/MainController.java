package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.File;
import javafx.scene.input.MouseEvent;


public class MainController {

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView imageView2;

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
        setupImageModeToggleGroup();

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
            originalImage = new Image(selectedFile.toURI().toString(),imageView.getFitWidth(),imageView.getFitHeight(),false,true);
            imageView.setImage(originalImage);
            imageView2.setImage(originalImage);
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
        return Math.abs(c1.getHue() - c2.getHue()) < tolerance*180;
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
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}


