//package application;
//
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Text;
//import javafx.scene.text.TextAlignment;
//import javafx.scene.text.TextFlow;
//import javafx.stage.Screen;
//import javafx.stage.Stage;
//
//public class MangEmp extends Application {
//
//	OrdinaryEmployeeCollector ordinary= new  OrdinaryEmployeeCollector();
//	ManagerCollector manger= new  ManagerCollector();
//    @Override
//    public void start(Stage primaryStage) {
//        // Main layout
//        StackPane root = new StackPane();
//        root.setStyle("-fx-background-color: #283645;"); // Set background color
//
//        // Buttons with images
//        Button ordinaryButton = createCustomButton("OrdinaryEmployee", "employees.png");
//        Button managerButton = createCustomButton("Manager", "manager.png");
//
//        // Button actions
//        ordinaryButton.setOnAction(e -> showOrdSection( primaryStage ));
//        managerButton.setOnAction(e ->  showMangSection( primaryStage));
//        
//        
//        
//
//        // HBox to hold buttons
//        HBox buttonBox = new HBox(30, ordinaryButton, managerButton);
//        buttonBox.setAlignment(Pos.CENTER);
//
//        // Add buttons to root
//        root.getChildren().add(buttonBox);
//        double screenWidth = Screen.getPrimary().getBounds().getWidth();
//        double screenHeight = Screen.getPrimary().getBounds().getHeight();
//
//        // Scene setup
//        Scene scene = new Scene(root, screenWidth, screenHeight); // Adjusted window size
//
//        // Scene setup
//      //  Scene scene = new Scene(root, 600, 400);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("Employee Interface");
//        primaryStage.show();
//    }
//
//    // Create a custom button with text and image
//    private Button createCustomButton(String text, String imagePath) {
//        // Load the image
//        ImageView imageView = createImageView(imagePath, 80, 80);
//
//        // Text for the button
//        Text buttonText = new Text(text);
//        buttonText.setFill(Color.WHITE);
//        buttonText.setStyle("-fx-font-size: 14px; -fx-font-family: Arial;");
//
//        // Combine text and image in VBox
//        VBox content = new VBox(10, buttonText, imageView);
//        content.setAlignment(Pos.CENTER);
//
//        // Button with custom VBox
//        Button button = new Button();
//        button.setGraphic(content);
//        button.setStyle(
//                "-fx-background-color: #405060; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; "
//                        + "-fx-background-radius: 10; -fx-border-radius: 10;");
//
//        button.setOnMouseEntered(e -> button.setStyle(
//                "-fx-background-color: #506070; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; "
//                        + "-fx-background-radius: 10; -fx-border-radius: 10;"));
//        button.setOnMouseExited(e -> button.setStyle(
//                "-fx-background-color: #405060; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; "
//                        + "-fx-background-radius: 10; -fx-border-radius: 10;"));
//
//        return button;
//    }
//
//    // Helper method to create an ImageView
//    private ImageView createImageView(String imagePath, double width, double height) {
//        try {
//            Image image = new Image(getClass().getResourceAsStream(imagePath));
//            ImageView imageView = new ImageView(image);
//            imageView.setFitWidth(width);
//            imageView.setFitHeight(height);
//            imageView.setPreserveRatio(true);
//            return imageView;
//        } catch (Exception e) {
//            System.out.println("Error loading image: " + imagePath);
//            return new ImageView(); // Return empty ImageView if image fails to load
//        }
//    }
//
//    
//    private void showOrdSection(Stage primaryStage) {
//    	ordinary.start(primaryStage);
//	}
//    private void showMangSection(Stage primaryStage) {
//    	manger.start(primaryStage);
//	}
//    
//
//   
//    public static void main(String[] args) {
//        launch(args);
//    }
//}