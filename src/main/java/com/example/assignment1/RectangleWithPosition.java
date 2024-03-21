package com.example.assignment1;

import javafx.scene.shape.Rectangle;

public class RectangleWithPosition {
    private Rectangle rectangle;
    private int minX;
    private int minY;

    public RectangleWithPosition(Rectangle rectangle, int minX, int minY) {
        this.rectangle = rectangle;
        this.minX = minX;
        this.minY = minY;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }
}
