package com.example.assignment1;

import com.example.assignment1.MainController;
import javafx.scene.paint.Color;
import org.junit.Test;
import javafx.scene.paint.Color;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MainControllerTest {


    @Test
    public void testColorSimilarity() {

        Color color1 = Color.RED;
        Color color2 = Color.rgb(255, 0, 0);
        assertTrue(MainController.colorSimilarity(color1, color2, 0.05));

        Color color3 = Color.BLUE;
        assertFalse(MainController.colorSimilarity(color1, color3, 0.05));

        Color color4 = Color.rgb(128, 128, 128);
        Color color5 = Color.rgb(130, 130, 130);
        assertTrue(MainController.colorSimilarity(color4, color5, 0.05));

        Color color6 = Color.rgb(0, 0, 0);
        Color color7 = Color.rgb(255, 255, 255);
        assertFalse(MainController.colorSimilarity(color6, color7, 0.05));
    }

    @Test
    public void testCountComponents() {
        //UnionFind for a 3x3 grid
        UnionFind uf = new UnionFind(9);


        uf.union(0, 1);
        uf.union(3, 4);
        uf.union(4, 5);

        MainController controller = new MainController();


        int expectedComponents = 9;
        int actualComponents = controller.countComponents(uf, 3, 3);

        assertEquals(expectedComponents, actualComponents);
    }
}


