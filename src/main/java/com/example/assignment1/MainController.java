package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;

import javafx.embed.swing.SwingFXUtils;




import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.WritableRaster;
import java.io.File;
import java.awt.image.BufferedImage;


public class MainController {

    @FXML
    private ImageView imageView;

    @FXML
    private Slider resizeSlider;

    @FXML
    private ToggleButton grayscaleToggleButton;
    @FXML
    private RadioButton originalRadioButton;
    @FXML
    private RadioButton grayscaleRadioButton;
    @FXML
    private RadioButton bwRadioButton;
    private Image originalImage;

    private ToggleGroup imageModeToggleGroup = new ToggleGroup();
    private final ColorAdjust grayscaleEffect = new ColorAdjust();
    private Color selectedColor;

    @FXML
    private void initialize() {
        // Slider setup
        resizeSlider.setMin(0.5);
        resizeSlider.setMax(2.0);
        resizeSlider.setValue(1.0);

        // Bind the image size to the slider value
        imageView.fitWidthProperty().bind(resizeSlider.valueProperty().multiply(200));
        imageView.fitHeightProperty().bind(resizeSlider.valueProperty().multiply(150));
        imageView.setOnMouseClicked(this::onImageClick);

        // Set up the toggle group for image modes
        originalRadioButton.setToggleGroup(imageModeToggleGroup);
        grayscaleRadioButton.setToggleGroup(imageModeToggleGroup);
        bwRadioButton.setToggleGroup(imageModeToggleGroup);

        // Set default image mode
        originalRadioButton.setSelected(true);

        // Apply grayscale effect based on toggle button
        grayscaleEffect.setSaturation(0); // Initially no grayscale
        imageView.setEffect(grayscaleEffect); // Apply effect to imageView

        // Listen for changes in the toggle group to adjust image accordingly
        imageModeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> handleImageModeChange());
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
    private void handleConvertToBlackAndWhite() {
        Image fxImage = imageView.getImage();
        if (fxImage != null) {
            BufferedImage bufferedImage = convertFxImageToBufferedImage(fxImage);

            // Assuming you have a way to choose targetColor and set threshold
            Color targetColor = Color.WHITE; // Example: dynamically set this based on user input
            double threshold = 50; // Example: dynamically set this based on user input

            BufferedImage bwImage = ImageProcessor.convertToBlackAndWhite(bufferedImage);
            Image newFxImage = convertToFxImage(bwImage);
            imageView.setImage(newFxImage);
        }
    }

    private BufferedImage convertFxImageToBufferedImage(Image fxImage) {
        BufferedImage bufferedImage = new BufferedImage((int) fxImage.getWidth(), (int) fxImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        javafx.embed.swing.SwingFXUtils.fromFXImage(fxImage, bufferedImage);
        g.dispose();
        return bufferedImage;
    }

    private Image convertToFxImage(BufferedImage bi) {
        return javafx.embed.swing.SwingFXUtils.toFXImage(bi, null);
    }

    @FXML
    private void handleImageModeChange() {
        if (imageModeToggleGroup.getSelectedToggle().equals(bwRadioButton)) {
            BufferedImage bufferedImage = convertFxImageToBufferedImage(imageView.getImage());
            BufferedImage bwImage = ImageProcessor.convertToBlackAndWhite(bufferedImage);
            Image newFxImage = convertToFxImage(bwImage);
            imageView.setImage(newFxImage);
        } else if (imageModeToggleGroup.getSelectedToggle().equals(grayscaleRadioButton)) {
            // Grayscale conversion logic...
        } else {
            // Reset to original image...
        }
    }

    @FXML
    private void onImageClick(javafx.scene.input.MouseEvent event) {
        System.out.println("Image clicked at");
        Image image = imageView.getImage();
        PixelReader pixelReader = image.getPixelReader();
        if (pixelReader == null) {
            return;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        Color sampledColor = pixelReader.getColor(x, y);

        this.selectedColor = sampledColor;
    }

    private double colorSimilarity(Color c1, Color c2){
        double redDiff = c1.getRed() - c2.getRed();
        double greenDiff = c1.getGreen() - c2.getGreen();
        double blueDiff = c1.getBlue() - c2.getBlue();
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    private Image convertToBlackAndWhite(Image originalImage,javafx.scene.paint.Color targetColor, double threshold) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        WritableImage bwImage = new WritableImage(width, height);
        PixelWriter pixelWriter = bwImage.getPixelWriter();
        PixelReader pixelReader = originalImage.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                double distance = colorSimilarity(color, targetColor);
                if (distance <= threshold) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                }
            }

        }
        return bwImage;
    }



    @FXML
    private void handleClose() {
        System.exit(0);
    }

    public class UnionFind{
        private int[] parent;
        private int[] rank;

        public UnionFind(int size){
            parent = new int[size];
            rank = new int[size];
            for(int i = 0; i <size; i++){
                parent[i] = i;
                rank[i] = 0;
            }
        }
        public int find(int i){
            if(parent[i] != i){
                parent[i] = find(parent[i]);
            }
                return parent[i];
            }

        public void union(int x,int y){
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY){
                if(rank[rootX] < rank[rootY]){
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]){
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;

                }
            }
        }
    }

    private boolean isWhite(int color){
        return (color & 0x00FFFFFF) == 0x00FFFFFF;
    }


    public void processImage(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        UnionFind uf = new UnionFind(width*height);

        for (int y =0; y < height; y++){
            for (int x = 0; x < width; x++){
                if(isWhite(image.getRGB(x,y))){
                    int id = x + y * width;
                    if(x + 1 < width && isWhite(image.getRGB(x+1,y))){
                        uf.union(id,id + 1);
                    }
                    if(y + 1 < height && isWhite(image.getRGB(x,y+1))){
                        uf.union(id,id + width);
                    }
                }
            }
        }
    }

}
