module dataBaseProj {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    // Open the `application` package to allow JavaFX and reflection-based libraries to access it
    opens application to javafx.graphics, javafx.fxml, javafx.base;

    // Export the `application` package so other modules or classes can use it
    exports application;
}

