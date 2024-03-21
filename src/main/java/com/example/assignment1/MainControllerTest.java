package com.example.assignment1;

import javafx.scene.paint.Color;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MainControllerTest {


    @Test
    public void testColorSimilarity() {

        Color color1 = Color.RED;
        Color color2 = Color.rgb(255, 0, 0);
        assertTrue(MainController.colorSim(color1, color2, 0.05));

        Color color3 = Color.BLUE;
        assertFalse(MainController.colorSim(color1, color3, 0.05));

        Color color4 = Color.rgb(128, 128, 128);
        Color color5 = Color.rgb(130, 130, 130);
        assertTrue(MainController.colorSim(color4, color5, 0.05));

        Color color6 = Color.rgb(0, 0, 0);
        Color color7 = Color.rgb(255, 255, 255);
        assertFalse(MainController.colorSim(color6, color7, 0.05));
    }

    @Test
    public void testCountComponents() {
        //UnionFind for a 3x3 grid
        UnionFind uf = new UnionFind(9);


        uf.union(0, 1); // Connect 0 and 1
        uf.union(3, 4); // Connect 3 and 4
        uf.union(4, 5); // Connect 4 and 5

        MainController controller = new MainController();


        int expectedComponents = 6;
        int actualComponents = controller.countComponents(uf, 3, 3);

        // There should be 6 components: 0, 1, 2, 3, 4, 5
        assertEquals(expectedComponents, actualComponents);
    }

    @Test
    public void testFindRoot() {
        UnionFind uf = new UnionFind(10);
        uf.union(1, 2); // Union some elements
        uf.union(2, 3);
        assertEquals(uf.find(3), uf.find(1)); // Verify that element 3's root is the same as element 1's root
    }



}


