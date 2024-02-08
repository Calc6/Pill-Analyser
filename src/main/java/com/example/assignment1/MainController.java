package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    @FXML
    private ImageView imageView;

    @FXML
    private Slider resizeSlider;

    @FXML
    private ToggleButton grayscaleToggleButton;

    private final ColorAdjust grayscaleEffect = new ColorAdjust();

    @FXML
    private void initialize() {
        // Set up the slider properties
        resizeSlider.setMin(0.5);
        resizeSlider.setMax(2.0);
        resizeSlider.setValue(1.0);

        // Bind the image size to the slider value
        imageView.fitWidthProperty().bind(resizeSlider.valueProperty().multiply(200));
        imageView.fitHeightProperty().bind(resizeSlider.valueProperty().multiply(150));

        grayscaleEffect.setSaturation(0);
        grayscaleToggleButton.setSelected(false);
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    private void handleToggleGrayscale(){
        if (grayscaleToggleButton.isSelected()){
            grayscaleEffect.setSaturation(-1);
        }else {
            grayscaleEffect.setSaturation(0);
        }
        imageView.setEffect(grayscaleEffect);
    }

    @FXML
    private void handleClose() {
        System.exit(0);
    }

}
