package com.example.assignment1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

    // Counts distinct components in the image.
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
            origPane.getChildren().removeIf(node -> node instanceof Rectangle || node instanceof Text);

            // Find root values indicating distinct components
            HashSet<Integer> rootValues = new HashSet<>();
            for (int i = 0; i < imageArray.length; i++) {
                if (imageArray[i] != -1) {
                    int root = findRoot(imageArray, i);
                    rootValues.add(root);
                }
            }

            int totalPills = 0;

            for (int rootValue : rootValues) {
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxX = 0;
                int maxY = 0;
                int pillCount = 0;

                for (int i = 0; i < imageArray.length; i++) {
                    if (findRoot(imageArray, i) == rootValue) {
                        int width = (int) bAndWImageView.getImage().getWidth();
                        int x = i % width; // Calculate x position.
                        int y = i / width; // Calculate y position.


                        if (x < minX) minX = x;
                        if (y < minY) minY = y;
                        if (x > maxX) maxX = x;
                        if (y > maxY) maxY = y;


                        if (imageArray[i] != -1) {
                            pillCount++;
                        }
                    }
                }

                // Draw a rectangle around the component if it's larger than 10 pixels
                if ((maxX - minX + 1) * (maxY - minY + 1) > 10) {
                    Rectangle rect = new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
                    rect.setStroke(Color.GREEN);
                    rect.setFill(Color.TRANSPARENT);
                    origPane.getChildren().add(rect);


                    Text text = new Text((minX + maxX) / 2, (minY + maxY) / 2, "Pills: " + pillCount + ", Size: " + ((maxX - minX + 1) * (maxY - minY + 1)));
                    text.setFont(Font.font("Verdana", FontWeight.NORMAL, 7));
                    origPane.getChildren().add(text);


                    totalPills += pillCount;
                }
            }

            Text totalText = new Text("Total Pills: " + totalPills);
            totalText.setX(10); // Set X position of the total count text
            totalText.setY(20); // Set Y position of the total count text
            totalText.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
            origPane.getChildren().add(totalText);
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