package com.example.assignment1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;



import javafx.scene.input.MouseEvent;



public class MainController {
    UnionFind uf;
    public Pane origPane;
    @FXML
    private ImageView bAndWImageView;

    @FXML
    private ImageView normalImageView;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane drawingArea;



    private Image originalImage;
    private UnionFind unionFind;

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

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        this.unionFind = new UnionFind(width * height);

        processDSArrayWithUnionFind(originalImage);

        int componentCount = countComponents(this.unionFind, width, height);

        System.out.println("Component count: " + componentCount);

        drawComponentRectangles(this.unionFind, width, height);
    }




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
    private void handleResetToOriginal() {
        if (originalImage != null) {
            bAndWImageView.setImage(originalImage);
            System.out.println("Image view reset to the original image.");
        }
    }


    public static boolean colorSimilarity(Color c1, Color c2, double tolerance) {
        double hueDifference = Math.abs(c1.getHue() - c2.getHue()) / 360.0;
        double satDifference = Math.abs(c1.getSaturation() - c2.getSaturation());
        double brightDifference = Math.abs(c1.getBrightness() - c2.getBrightness());

        double weightedDiff = (hueDifference * 0.4) + (satDifference * 0.3) + (brightDifference * 0.3);
        return weightedDiff < tolerance;
    }


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

    public int countComponents(UnionFind uf, int width, int height) {
        HashSet<Integer> uniqueComponents = new HashSet<>();
        for (int i = 0; i < width * height; i++) {
            int parent = uf.find(i);
            uniqueComponents.add(parent);
        }
        return uniqueComponents.size();
    }


    private void drawComponentRectangles(UnionFind unionFind, int width, int height) {
        Map<Integer, int[]> boundsMap = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelIndex = y * width + x;
                int componentId = unionFind.find(pixelIndex);

                boundsMap.putIfAbsent(componentId, new int[]{x, y, x, y});

                int[] bounds = boundsMap.get(componentId);
                if (x < bounds[0]) bounds[0] = x;
                if (y < bounds[1]) bounds[1] = y;
                if (x > bounds[2]) bounds[2] = x;
                if (y > bounds[3]) bounds[3] = y;
            }
        }

        Platform.runLater(() -> {
            origPane.getChildren().clear();
            for (int[] bounds : boundsMap.values()) {
                Rectangle rect = new Rectangle(bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
                rect.setStroke(Color.GREEN);
                rect.setFill(Color.TRANSPARENT);
                origPane.getChildren().add(rect);
            }
        });
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}


