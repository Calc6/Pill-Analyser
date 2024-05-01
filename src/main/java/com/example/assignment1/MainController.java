package com.example.assignment1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.*;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;

import javafx.scene.input.MouseEvent;


public class MainController {
    UnionFind uf;
    public Pane origPane;
    private Image originalImage;
    private UnionFind unionFind;
    private int noiseThreshold = 100;
    @FXML
    private ImageView bAndWImageView;
    @FXML
    private ImageView normalImageView;
    @FXML
    private Slider thresholdSlider;
    @FXML
    Label thresholdLabel;

    @FXML
    private TextArea rectInfoDisplay;



    public void initialize() {
        // Example values, adjust as needed
        thresholdSlider.setMin(0);
        thresholdSlider.setMax(300);
        thresholdSlider.setValue(noiseThreshold);
        thresholdLabel.setText("Threshold: " + noiseThreshold);

        thresholdSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            noiseThreshold = newValue.intValue();
            thresholdLabel.setText("Threshold: " + noiseThreshold);
             updateImageViewWithReducedNoise(uf.parent);
        });
    }



    //Opens a file chooser dialog to select an image file
    @FXML
    private void openFile() {
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
    // Handles a mouse click on the ImageView, turns black and white, and marks rectangles
    private void onImageClick(MouseEvent event) {
        if (originalImage == null) return;

        int x = (int) event.getX();
        int y = (int) event.getY();
        PixelReader pixelReader = originalImage.getPixelReader();
        Color clickedColor = pixelReader.getColor(x, y);

        System.out.println("Clicked coordinates: (" + x + ", " + y + ")");
        System.out.println("Clicked color: " + clickedColor.toString());

        bAndw(clickedColor);

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        this.unionFind = new UnionFind(width * height);

        processDSArrayWithUnionFind(originalImage);

        int componentCount = countComponents(this.unionFind, width, height);

        System.out.println("Component count: " + componentCount);


        noiseReduction(uf.parent, noiseThreshold);

        getRectPositions(uf.parent);
    }



    // Handles the black and white conversion
    public void bAndw(Color targetColor){

        if (originalImage == null) {
            System.out.println("No image loaded.");
            return;
        }

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        uf = new UnionFind(width * height);

        WritableImage updatedImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = updatedImage.getPixelWriter();


        int countWhite = 0;
        int countBlack = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                bAndWImageView.setImage(updatedImage);
                if (colorSim(targetColor, color, 0.05)) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                    countWhite++;
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                    uf.parent[y * width + x] = -1;
                    countBlack++;
                }
            }
        }

        bAndWImageView.setImage(updatedImage);
        System.out.println("Image view now black and white");

        System.out.println("White pixels: " + countWhite);
        System.out.println("Black pixels: " + countBlack);
    }

    @FXML
    // Resets the image view to the original image
    private void ResetToOriginal() {
        if (originalImage != null) {
            bAndWImageView.setImage(originalImage);
            System.out.println("Image reset");
        }
    }

    // Compares two colors and returns true if they are similar, false if not within tolerance
    public static boolean colorSim(Color c1, Color c2, double tolerance) {
        double hueDifference = Math.abs(c1.getHue() - c2.getHue()) / 360.0;
        double satDifference = Math.abs(c1.getSaturation() - c2.getSaturation());
        double brightDifference = Math.abs(c1.getBrightness() - c2.getBrightness());

        double weightedDiff = (hueDifference * 0.4) + (satDifference * 0.3) + (brightDifference * 0.3);
        return weightedDiff < tolerance;
    }


    // Identifies connected components in an image with Union-Find.
    public void processDSArrayWithUnionFind(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                if (uf.parent[index] != -1){

                    if (x < width - 1 && uf.parent[index + 1] != -1) {
                        uf.union(index, index + 1);
                    }


                    if (y < height - 1 && uf.parent[index + width] != -1){
                        uf.union(index, index + width);
                    }
                }
            }
        }
        uf.displayDSAsText(width);
    }

    // Counts distinct components in the image.
    public int countComponents(UnionFind uf, int width, int height) {
        HashSet<Integer> uniqueComponents = new HashSet<>();
        for (int i = 0; i < width * height; i++) {
            int parent = uf.find(i);
            uniqueComponents.add(parent);
        }
        return uniqueComponents.size();
    }


    // Draws rectangles around the components and displays the total count of pills.
    public void getRectPositions(int[] imageArray) {
        Platform.runLater(() -> {
            origPane.getChildren().removeIf(node -> node instanceof Rectangle || node instanceof Text);

            List<RectangleWithPosition> rectanglesWithPositions = new ArrayList<>();

            HashSet<Integer> rootValues = new HashSet<>();
            for (int i = 0; i < imageArray.length; i++) {
                if (imageArray[i] != -1) {
                    int root = findRoot(imageArray, i);
                    rootValues.add(root);
                }
            }

            for (int rootValue : rootValues) {
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxX = 0;
                int maxY = 0;

                for (int i = 0; i < imageArray.length; i++) {
                    if (findRoot(imageArray, i) == rootValue) {
                        int width = (int) bAndWImageView.getImage().getWidth();
                        int x = i % width;
                        int y = i / width;

                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                    }
                }

                int rectWidth = maxX - minX + 1;
                int rectHeight = maxY - minY + 1;
                if (rectWidth > 2 && rectHeight > 2) {
                    Rectangle rect = new Rectangle(minX, minY, rectWidth, rectHeight);
                    rectanglesWithPositions.add(new RectangleWithPosition(rect, minX, minY));
                }
            }

            rectanglesWithPositions.sort(Comparator.comparingInt(RectangleWithPosition::getMinY)
                    .thenComparingInt(RectangleWithPosition::getMinX));

            StringBuilder infoText = new StringBuilder();
            infoText.append("Total Pill: ").append(rectanglesWithPositions.size()).append("\n");

            int label = 1;
            for (RectangleWithPosition rwp : rectanglesWithPositions) {
                Rectangle rect = rwp.getRectangle();
                rect.setStroke(Color.GREEN);
                rect.setFill(Color.TRANSPARENT);
                origPane.getChildren().add(rect);

                Text labelText = new Text(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2, "#" + label);
                labelText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                origPane.getChildren().add(labelText);

                double size = rect.getWidth() * rect.getHeight();
                infoText.append("Pill ").append(label).append(" Size: ").append(String.format("%.2f", size)).append("\n");
                label++;
            }

            // Update the information display area with rectangle details
            updateInfoDisplay(infoText.toString());
        });
    }

    // updates the display area with the given text.
    public void updateInfoDisplay(String infoText) {
        displayInfoTextOnUI(infoText);
    }

    // Displays the given text on the UI.
    private void displayInfoTextOnUI(String text) {
        rectInfoDisplay.setText(text);
    }




    //Utility method to find the root of a component in the Union-Find structure.
    private int findRoot(int[] imageArray, int i) {
        if (i == -1) {

            return -1;
        }
        while (i != imageArray[i]) {
            if (imageArray[i] == -1) {
                return -1;
            }
            i = imageArray[i];
        }
        return i;
    }

    // Applies noise reduction to the image.
    public void noiseReduction(int[] imageArray, int threshold) {
        HashMap<Integer, Integer> componentSizes = new HashMap<>();

        for (int i = 0; i < imageArray.length; i++) {
            int root = findRoot(imageArray, i);
            if (root != -1) {
                componentSizes.put(root, componentSizes.getOrDefault(root, 0) + 1);
            }
        }

        // Remove components from the map that don't meet the size threshold
        componentSizes.keySet().removeIf(key -> componentSizes.get(key) < threshold);

        for (int i = 0; i < imageArray.length; i++) {
            int root = findRoot(imageArray, i);
            if (!componentSizes.containsKey(root)) {
                imageArray[i] = -1;
            }
        }
    }

    // Updates the image view with the reduced noise.
    private void updateImageViewWithReducedNoise(int[] imageArray) {
        noiseReduction(imageArray, noiseThreshold);

        Image updatedImage = convertArrayToImage(imageArray);

        Platform.runLater(() -> {
            bAndWImageView.setImage(updatedImage);
        });
    }

    private Image convertArrayToImage(int[] imageArray) {
        return null;
    }

    public void colorDisjointSets() {
        int width = (int) bAndWImageView.getImage().getWidth();
        int height = (int) bAndWImageView.getImage().getHeight();
        WritableImage coloredImage = new WritableImage(width, height);
        PixelWriter writer = coloredImage.getPixelWriter();

        // Generate a color for each set
        Map<Integer, Color> setColorMap = new HashMap<>();
        int index;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                index = y * width + x;
                int root = uf.find(index);
                setColorMap.putIfAbsent(root, generateRandomColor());

                writer.setColor(x, y, setColorMap.get(root));
            }
        }

        bAndWImageView.setImage(coloredImage);
    }

    //generates random color
    public Color generateRandomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }


    // Closes the application
    @FXML
    private void handleClose(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}