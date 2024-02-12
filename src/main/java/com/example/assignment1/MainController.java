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
import java.awt.image.BufferedImage;


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
