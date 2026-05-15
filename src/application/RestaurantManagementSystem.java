package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class RestaurantManagementSystem extends Application {

	private BorderPane root; // Main layout
	RestaurantMenu menu = new RestaurantMenu();
	CustomerInterface Customer = new CustomerInterface();
	OrderApp orders = new OrderApp();
	RestaurantMonthlySalary Salary = new RestaurantMonthlySalary();

	@Override
	public void start(Stage primaryStage) {
		// Initialize root layout
		root = new BorderPane();

		// Sidebar (VBox)
		VBox sidebar = new VBox(20);
		sidebar.setPadding(new Insets(20));
		sidebar.setStyle("-fx-background-color: #283645;"); // Dark blue background
		

		// Sidebar buttons
		Button customerButton = createSidebarButton("Customer", "file:customer.png");
		Button orderButton = createSidebarButton("Order", "file:sent.png");
		Button menuButton = createSidebarButton("Menu", "file:menu-burger.png");
		Button homeButton = createSidebarButton("Home", "file:home.png"); // Add Home button
		Button branchButton = createSidebarButton("Branch", "file:global.png"); // Add Home button
		Button salaryButton = createSidebarButton("Salary", "file:income.png"); // Add Home button
		Button employeeButton = createSidebarButton("Employee", "file:employees.png"); // Add Home button
		Button managerButton = createSidebarButton("Manager", "file:manager.png"); // Add Home button
		Button exitButton = createExitButton("Exit");

		// Add buttons to the sidebar
		sidebar.getChildren().addAll(homeButton, customerButton, orderButton, menuButton, branchButton, salaryButton,
				employeeButton, managerButton, exitButton);


		// Default Center Content (Welcome Image)
		VBox centerContent = new VBox();
		centerContent.setAlignment(Pos.CENTER); // Center the image vertically and horizontally
		centerContent.setPadding(new Insets(0)); // Remove any padding
		centerContent.setStyle("-fx-background-color: #283645;"); // Unified dark background color

		// Load the image
//
//		// Resize the image and maintain aspect ratio
//		welcomeImage.setFitWidth(1500); // Adjust width
//		welcomeImage.setFitHeight(700);
//		welcomeImage.setPreserveRatio(true); // Keep aspect ratio
		ImageView welcomeImage = new ImageView(new Image("file:HomePage.jpg"));

		// Resize the image and maintain aspect ratio
		welcomeImage.setFitWidth(1400); // Adjust width

		welcomeImage.setFitHeight(700);
		welcomeImage.setPreserveRatio(true);

		// Center the content
		centerContent.setAlignment(Pos.CENTER); // Center the image vertically and horizontally
//		centerContent.setPadding(new Insets(10)); // Add padding
		centerContent.getChildren().add(welcomeImage); // Add the image to the center content

		// Set the layout
		root.setLeft(sidebar); // Sidebar on the left
		root.setCenter(centerContent); // Center content with the image

		// Set default layout
		root.setLeft(sidebar);
		root.setCenter(centerContent);

		// Button Actions
		customerButton.setOnAction(e -> {
			try {
				showCustomerSection(primaryStage);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		orderButton.setOnAction(e -> showOrderSection(primaryStage));
		menuButton.setOnAction(e -> showMenuSection(primaryStage));
		homeButton.setOnAction(e -> showHomeSection(centerContent, welcomeImage)); // Show home content when pressed
		exitButton.setOnAction(e -> System.exit(0));
		salaryButton .setOnAction(e ->{
			try {
				showSalarySection(primaryStage);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		homeButton.setOnAction(e -> {
		    // 
		    centerContent.getChildren().clear(); 
		    centerContent.getChildren().add(welcomeImage); 
		    root.setCenter(centerContent); 
		});

		// Scene setup
		Scene scene = new Scene(root, 1530, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Restaurant Management System");
		primaryStage.show();
	}

	// Home Section: Show welcome image
	private void showHomeSection(VBox centerContent, ImageView welcomeImage) {
		centerContent.getChildren().clear(); // Clear current content
		centerContent.getChildren().add(welcomeImage); // Show welcome image again
		root.setCenter(centerContent);
	}

	// Sidebar Button Factory
	private Button createSidebarButton(String text, String PATH) {
		ImageView image = new ImageView(PATH);
		image.setFitWidth(20);
		image.setFitHeight(20);
		Button button = new Button(text, image);
		button.setStyle(
				"-fx-background-color: #283645; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: Arial; "
						+ "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;");
		button.setOnMouseEntered(e -> button.setStyle(
				"-fx-background-color: #405060; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: Arial; "
						+ "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;"));
		button.setOnMouseExited(e -> button.setStyle(
				"-fx-background-color: #283645; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: Arial; "
						+ "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;"));
		ImageView backIcon = new ImageView(PATH);
		backIcon.setFitWidth(20);
		backIcon.setFitHeight(20);
		return button;
	}

	// Exit Button
	private Button createExitButton(String text) {
		Button button = new Button(text);
		button.setStyle(
				"-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: Arial; "
						+ "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;");
		button.setOnMouseEntered(e -> button.setStyle(
				"-fx-background-color: #D81B60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: Arial; "
						+ "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;"));
		button.setOnMouseExited(e -> button.setStyle(
				"-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: Arial; "
						+ "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;"));
		return button;
	}

	// Customer Section Placeholder
	private void showCustomerSection(Stage primaryStage) throws ClassNotFoundException {
		Customer.start(primaryStage);
	}

	// Order Section Placeholder
	private void showOrderSection(Stage primaryStage) {
		orders.start(primaryStage);
	}

	// Menu Section
	private void showMenuSection(Stage primaryStage) {
		menu.start(primaryStage);
	}

	// Customer Section Placeholder
	private void showSalarySection(Stage primaryStage) throws ClassNotFoundException {
		Salary.start(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}

 
