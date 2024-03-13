module com.example.assignment1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires junit;


    opens com.example.assignment1 to javafx.fxml;
    exports com.example.assignment1;
}