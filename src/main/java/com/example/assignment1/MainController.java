package com.example.assignment1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
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
import org.w3c.dom.Text;

public class MainController {
    UnionFind uf;
    public Pane origPane;
    private Image originalImage;
    private UnionFind unionFind;
    @FXML
    private ImageView bAndWImageView;
    @FXML
    private ImageView normalImageView;

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

        getRectPositions(uf.parent);
    }



 // Handles the black and white conversion
    private void bAndw(Color targetColor){

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
                if (colorSimilarity(targetColor, color, 0.05)) {
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
        System.out.println("Image view should now display the updated image.");

        System.out.println("White pixels: " + countWhite);
        System.out.println("Black pixels: " + countBlack);
    }

    @FXML
    // Resets the image view to the original image
    private void ResetToOriginal() {
        if (originalImage != null) {
            bAndWImageView.setImage(originalImage);
            System.out.println("Image view reset to the original image.");
        }
    }

    // Compares two colors and returns true if they are similar, false if not within tolerance
    public static boolean colorSimilarity(Color c1, Color c2, double tolerance) {
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

    // Counts distinct connected components in the image.
    public int countComponents(UnionFind uf, int width, int height) {
        HashSet<Integer> uniqueComponents = new HashSet<>();
        for (int i = 0; i < width * height; i++) {
            int parent = uf.find(i);
            uniqueComponents.add(parent);
        }
        return uniqueComponents.size();
    }


    public void getRectPositions(int[] imageArray) {
        Platform.runLater(() -> {
            // Clears rectangles and text from pane
            origPane.getChildren().removeIf(node -> node instanceof Rectangle || node instanceof Text);

            // Find root values indicating distinct components
            HashSet<Integer> rootValues = new HashSet<>();
            for (int i = 0; i < imageArray.length; i++) {
                if (imageArray[i] != -1) {
                    int root = findRoot(imageArray, i);
                    rootValues.add(root);
                }
            }

            // For each unique component determine its bounding box
            for (int rootValue : rootValues) {
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxX = 0;
                int maxY = 0;

                for (int i = 0; i < imageArray.length; i++) {
                    if (findRoot(imageArray, i) == rootValue) {
                        int width = (int) bAndWImageView.getImage().getWidth();
                        int x = i % width; // Calculate x position.
                        int y = i / width; // Calculate y position.

                        // Update bounding box corners for the component.
                        if (x < minX) minX = x;
                        if (y < minY) minY = y;
                        if (x > maxX) maxX = x;
                        if (y > maxY) maxY = y;
                    }
                }

                // Draw a rectangle around the component if it's larger than a single pixel.
                if (maxX - minX > 0 && maxY - minY > 0) {
                    Rectangle rect = new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
                    rect.setStroke(Color.GREEN);
                    rect.setFill(Color.TRANSPARENT);
                    origPane.getChildren().add(rect); // Add rectangle to the pane.
                }
            }
        });
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


    @FXML
    private void handleClose(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}