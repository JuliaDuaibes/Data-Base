package application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import application.Menu;

public class RestaurantMenu extends Application {
	private BorderPane root;
	private Connection connection;
	private ListView<String> categoryListView;
	private ArrayList<Menu> menuItems = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {
		try {
			connectDatabase();
			initializeUI(primaryStage);
			System.out.println("Global menu loaded for all branches.");

			// Show the global menu
			showMenuGrid("All Categories");
		} catch (Exception e) {
			e.printStackTrace();
			showError("An error occurred while initializing the menu: " + e.getMessage());
		}
	}

	// Helper method to show error dialog
	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// Updated method to initialize UI
	private void initializeUI(Stage primaryStage) {
		categoryListView = new ListView<>();
		categoryListView.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645; -fx-font-weight: bold;");
		categoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				showMenuGrid(newValue);
			}
		});

		root = new BorderPane();

		// Top Toolbar
		HBox topToolbar = new HBox(10);
		topToolbar.setPadding(new Insets(10));
		topToolbar.setStyle("-fx-background-color: #283645;");

		// Add New Item Button
		ImageView addImageIcon = new ImageView("file:icons8-add-64.png");
		addImageIcon.setFitWidth(20);
		addImageIcon.setFitHeight(20);
		Button addButton = new Button("New", addImageIcon);
		addButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
		addButton.setOnAction(e -> addNewItem());

		// Search Field
		TextField searchField = new TextField();
		searchField.setPromptText("Search by name or category...");
		searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

		// Search Button
		ImageView searchImageIcon = new ImageView("file:magnifying-glass-search.png");
		searchImageIcon.setFitWidth(20);
		searchImageIcon.setFitHeight(20);
		Button searchButton = new Button("Search", searchImageIcon);
		searchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
		searchButton.setOnAction(e -> {
			String searchText = searchField.getText().trim();
			if (!searchText.isEmpty()) {
				searchMenuItems(searchText);
			} else {
				showMenuGrid("All Categories");
			}
		});

		// Statistics Button
		ImageView statImage = new ImageView("file:statistics.png");
		statImage.setFitWidth(20);
		statImage.setFitHeight(20);
		Button statsButton = new Button("View Stats", statImage);
		statsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
		statsButton.setOnAction(e -> showStatsUI());

		// Add components to the toolbar
		topToolbar.getChildren().addAll(addButton, searchField, searchButton, statsButton);
		root.setTop(topToolbar);

		// Left Sidebar for Categories
		VBox categorySidebar = new VBox(10);
		categorySidebar.setPadding(new Insets(10));
		categorySidebar.setStyle("-fx-background-color: #283645;");
		// Back Button
		ImageView backIcon = new ImageView("file:left-arrow.png");
		backIcon.setFitWidth(20);
		backIcon.setFitHeight(20);
		Button backButton = new Button("Back", backIcon);
		backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
		backButton.setOnAction(e -> {
			RestaurantManagementSystem resurantStage = new RestaurantManagementSystem();
			resurantStage.start(primaryStage);
		});

		Label categoryLabel = new Label("Categories");
		categoryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

		categorySidebar.getChildren().addAll(categoryLabel, categoryListView, backButton);
		root.setLeft(categorySidebar);
		loadCategoriesFromDatabase();
		showMenuGrid("All Categories");

		Scene scene = new Scene(root, 1530, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Restaurant Management System");
		primaryStage.show();
	}

	private void showStatsUI() {
		VBox statsLayout = new VBox(20);
		statsLayout.setPadding(new Insets(20));
		statsLayout.setAlignment(Pos.TOP_CENTER);

		// Create TableViews
		TableView<Map<String, Object>> expensiveTable = createStatTable("Most Expensive Items");
		TableView<Map<String, Object>> cheapTable = createStatTable("Least Expensive Items");
		TableView<Map<String, Object>> salesTable = createSalesTable("Sold Items");

		// Load data into tables
		loadTableData(expensiveTable, cheapTable, salesTable);

		// Back button
		Button backButton = new Button("Back");
		backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
		backButton.setOnAction(e -> showMenuGrid("All Categories"));

		statsLayout.getChildren().addAll(expensiveTable, cheapTable, salesTable, backButton);
		root.setCenter(statsLayout);
	}

	private void loadTableData(TableView<Map<String, Object>> expensiveTable, TableView<Map<String, Object>> cheapTable,
			TableView<Map<String, Object>> salesTable) {
		try {
			connectDatabase();

			// Load most expensive items
			String maxPriceQuery = "SELECT category, MAX(itemPrice) AS value FROM Menu GROUP BY category";
			PreparedStatement stmtMax = connection.prepareStatement(maxPriceQuery);
			ResultSet rsMax = stmtMax.executeQuery();
			while (rsMax.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("category", rsMax.getString("category"));
				row.put("value", rsMax.getDouble("value"));
				expensiveTable.getItems().add(row);
			}

			// Load least expensive items
			String minPriceQuery = "SELECT category, MIN(itemPrice) AS value FROM Menu GROUP BY category";
			PreparedStatement stmtMin = connection.prepareStatement(minPriceQuery);
			ResultSet rsMin = stmtMin.executeQuery();
			while (rsMin.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("category", rsMin.getString("category"));
				row.put("value", rsMin.getDouble("value"));
				cheapTable.getItems().add(row);
			}

			// Load most sold items
			String mostSoldQuery = "SELECT m.category, m.itemName AS item, SUM(o.quantity) AS sales " + "FROM Orders o "
					+ "JOIN Menu m ON o.menuItemId = m.menuItemId " + "GROUP BY m.category, m.itemName "
					+ "ORDER BY sales DESC";

			PreparedStatement stmtSales = connection.prepareStatement(mostSoldQuery);
			ResultSet rsSales = stmtSales.executeQuery();
			while (rsSales.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("category", rsSales.getString("category"));
				row.put("item", rsSales.getString("item"));
				row.put("sales", rsSales.getInt("sales"));
				salesTable.getItems().add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadCategoriesFromDatabase() {
		try {
			connectDatabase();
			String query = "SELECT DISTINCT category FROM Menu";
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			categoryListView.getItems().clear();
			categoryListView.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645;-fx-font-weight: bold;");
			categoryListView.getItems().add("All Categories");

			while (rs.next()) {
				categoryListView.getItems().add(rs.getString("category"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showMenuGrid(String category) {
		GridPane menuGrid = new GridPane();
		menuGrid.setPadding(new Insets(20));
		menuGrid.setHgap(15);
		menuGrid.setVgap(15);
		menuGrid.setAlignment(Pos.CENTER);

		menuItems = loadMenuItemsFromDatabase(category);

		for (int i = 0; i < menuItems.size(); i++) {
			Menu item = menuItems.get(i);
			VBox menuItem = createMenuItem(item);
			menuGrid.add(menuItem, i % 5, i / 5);
		}

		ScrollPane scrollPane = new ScrollPane(menuGrid);
		scrollPane.setFitToWidth(true);
		root.setCenter(scrollPane);
	}

	public ArrayList<Menu> loadMenuItemsFromDatabase(String category) {
		ArrayList<Menu> items = new ArrayList<>();
		try {
			connectDatabase();
			String query = category.equals("All Categories") ? "SELECT * FROM Menu"
					: "SELECT * FROM Menu WHERE category = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			if (!category.equals("All Categories")) {
				stmt.setString(1, category);
			}
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				items.add(new Menu(rs.getInt("menuItemId"), rs.getString("itemName"), rs.getDouble("itemPrice"),
						rs.getString("category"), rs.getString("size"), rs.getBoolean("isAvailable"),
						rs.getString("imagePath")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	private VBox createMenuItem(Menu item) {
		VBox menuItem = new VBox(10);
		menuItem.setAlignment(Pos.CENTER);
		menuItem.setPadding(new Insets(10));
		menuItem.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

		// Display the product image
		ImageView itemImage = new ImageView(new Image("file:" + item.getImagePath()));
		itemImage.setFitWidth(150);
		itemImage.setFitHeight(150);
		itemImage.setPreserveRatio(true);

		// Display the product name
		Label itemName = new Label(item.getItemName());
		itemName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

		// Display the product price
		Label itemPrice = new Label("Price: " + item.getItemPrice() + " ₪");
		itemPrice.setStyle("-fx-font-size: 12px;");

		// Delete button
		// Icons for buttons
		ImageView deleteIcon = new ImageView("file:delete.png");
		deleteIcon.setFitWidth(20);
		deleteIcon.setFitHeight(20);
		Button deleteButton = new Button("Delete", deleteIcon);
		deleteButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
		deleteButton.setOnAction(e -> {
			deleteMenuItem(item);
		});

		// Update button
		ImageView UpadteIcon = new ImageView("file:refresh.png");
		UpadteIcon.setFitWidth(20);
		UpadteIcon.setFitHeight(20);
		Button updateButton = new Button("Update", UpadteIcon);
		updateButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");
		updateButton.setOnAction(e -> {
			updateMenuItem(item);
		});

		// Layout for the buttons
		HBox buttonBox = new HBox(10, deleteButton, updateButton);
		buttonBox.setAlignment(Pos.CENTER);

		// Add components to the menu item layout
		menuItem.getChildren().addAll(itemImage, itemName, itemPrice, buttonBox);
		menuItem.setOnMouseClicked(event -> showProductDetails(item));

		return menuItem;
	}

	private void deleteMenuItem(Menu item) {
		Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
		confirmDialog.setTitle("Delete Item");
		confirmDialog.setHeaderText("Are you sure you want to delete " + item.getItemName() + "?");
		confirmDialog.setContentText("This action cannot be undone.");

		Optional<ButtonType> result = confirmDialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				connectDatabase();
				String query = "DELETE FROM Menu WHERE menuItemId = ?";
				PreparedStatement stmt = connection.prepareStatement(query);
				stmt.setInt(1, item.getMenuItemId());
				stmt.executeUpdate();

				// Refresh the menu grid
				showMenuGrid("All Categories");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateMenuItem(Menu selectedMenuItem) {
		Dialog<Menu> dialog = new Dialog<>();
		dialog.setTitle("Update Menu Item");

		// Set background color for the dialog content area
		dialog.getDialogPane().setStyle("-fx-background-color: #283645;");

		// Create fields for input with the current values pre-filled
		TextField nameField = new TextField(selectedMenuItem.getItemName());
		nameField.setPromptText("Item Name");
		nameField.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");

		TextField priceField = new TextField(String.valueOf(selectedMenuItem.getItemPrice()));
		priceField.setPromptText("Price");
		priceField.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");

		TextField categoryField = new TextField(selectedMenuItem.getCategory());
		categoryField.setPromptText("Category");
		categoryField.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");

		ComboBox<String> sizeComboBox = new ComboBox<>();
		sizeComboBox.getItems().addAll("Small", "Medium", "Large");
		sizeComboBox.setValue(selectedMenuItem.getSize());
		sizeComboBox.setPromptText("Select Size");
		sizeComboBox.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");

		Label imagePathLabel = new Label(selectedMenuItem.getImagePath());
		imagePathLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

		// Icons for buttons
		ImageView AddImageIcon = new ImageView("file:Addimage.png");
		AddImageIcon.setFitWidth(20);
		AddImageIcon.setFitHeight(20);

		Button browseButton = new Button("Browse Image", AddImageIcon);
		browseButton.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");
		browseButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters()
					.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
			File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
			if (selectedFile != null) {
				imagePathLabel.setText(selectedFile.getAbsolutePath());
			}
		});

		CheckBox availableCheckBox = new CheckBox("Available");
		availableCheckBox.setSelected(selectedMenuItem.isAvailable());
		availableCheckBox.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");

		// Input form layout (GridPane)
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20));

		// Create and style labels
		Label nameLabel = new Label("Name:");
		nameLabel.setStyle("-fx-text-fill: white;");

		Label priceLabel = new Label("Price:");
		priceLabel.setStyle("-fx-text-fill: white;");

		Label categoryLabel = new Label("Category:");
		categoryLabel.setStyle("-fx-text-fill: white;");

		Label sizeLabel = new Label("Size:");
		sizeLabel.setStyle("-fx-text-fill: white;");

		Label imagePathLabelPrompt = new Label("Image Path:");
		imagePathLabelPrompt.setStyle("-fx-text-fill: white;");
		Label availablePrompt = new Label("available:");
		availablePrompt.setStyle("-fx-text-fill: white;");
		// Add the components to the grid
		grid.addRow(0, nameLabel, nameField);
		grid.addRow(1, priceLabel, priceField);
		grid.addRow(2, categoryLabel, categoryField);
		grid.addRow(3, sizeLabel, sizeComboBox);
		grid.addRow(4, imagePathLabelPrompt, imagePathLabel, browseButton);
		grid.addRow(5, availablePrompt, availableCheckBox);

		grid.setStyle("-fx-background-color: #283645;"); // Dark background for contrast

		dialog.getDialogPane().setContent(grid);

		// Add "Update" and "Cancel" buttons to the dialog
		ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == updateButtonType) {
				return new Menu(selectedMenuItem.getMenuItemId(), nameField.getText(),
						Double.parseDouble(priceField.getText()), categoryField.getText(), sizeComboBox.getValue(),
						availableCheckBox.isSelected(), imagePathLabel.getText());
			}
			return null;
		});

		dialog.showAndWait().ifPresent(updatedMenuItem -> {
			updateMenuInDatabase(updatedMenuItem); // Method to update the menu item in your database
			showMenuGrid("All Categories"); // Refresh the menu grid
		});
	}

	private void updateMenuInDatabase(Menu menu) {
		try {
			connectDatabase();
			String query = "UPDATE Menu SET itemName = ?, itemPrice = ?, category = ?, size = ?, isAvailable = ?, imagePath = ? WHERE menuItemId = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, menu.getItemName());
			stmt.setDouble(2, menu.getItemPrice());
			stmt.setString(3, menu.getCategory());
			stmt.setString(4, menu.getSize());
			stmt.setBoolean(5, menu.isAvailable());
			stmt.setString(6, menu.getImagePath());
			stmt.setInt(7, menu.getMenuItemId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addNewItem() {
		// Create a new stage for adding a new menu item
		Stage stage = new Stage();
		stage.setTitle("Add New Menu Item");

		// Input fields
		TextField nameField = new TextField();
		nameField.setPromptText("Item Name");
		nameField.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");
		TextField priceField = new TextField();
		priceField.setPromptText("Price");
		priceField.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");

		TextField categoryField = new TextField();
		categoryField.setPromptText("Category");
		categoryField.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");

		// ComboBox for size selection
		ComboBox<String> sizeComboBox = new ComboBox<>();
		sizeComboBox.getItems().addAll("Small", "Medium", "Large", "Regular"); // Add Regular explicitly
		sizeComboBox.setValue("Regular"); // Default value
		sizeComboBox.setPromptText("Select Size");
		sizeComboBox.setStyle("-fx-text-fill: #283645; -fx-font-weight: bold;");

		// Icons for buttons
		ImageView AddImageIcon = new ImageView("file:Addimage.png");
		AddImageIcon.setFitWidth(20);
		AddImageIcon.setFitHeight(20);

		Label imagePathLabel = new Label("No image selected");
		imagePathLabel.setStyle("-fx-text-fill: white;");
		Button browseButton = new Button("Browse Image", AddImageIcon);
		browseButton.setStyle("-fx-text-fill: #283645; -fx-background-color: #E91E63; -fx-font-weight: bold;");

		// FileChooser for selecting an image
		browseButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters()
					.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
			File selectedFile = fileChooser.showOpenDialog(stage);
			if (selectedFile != null) {
				imagePathLabel.setText(selectedFile.getAbsolutePath());
			}
		});

		CheckBox availableCheckBox = new CheckBox("Available");
		availableCheckBox.setStyle("-fx-text-fill: white;");

		// Grid for input layout
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20));

		// Directly set the style for each label when creating them
		Label nameLabel = new Label("Name:");
		nameLabel.setStyle("-fx-text-fill: white;");
		grid.addRow(0, nameLabel, nameField);

		Label priceLabel = new Label("Price:");
		priceLabel.setStyle("-fx-text-fill: white;");
		grid.addRow(1, priceLabel, priceField);

		Label categoryLabel = new Label("Category:");
		categoryLabel.setStyle("-fx-text-fill: white;");
		grid.addRow(2, categoryLabel, categoryField);

		Label sizeLabel = new Label("Size:");
		sizeLabel.setStyle("-fx-text-fill: white;");
		grid.addRow(3, sizeLabel, sizeComboBox);

		Label imageLabel = new Label("Image Path:");
		imageLabel.setStyle("-fx-text-fill: white;");
		grid.addRow(4, imageLabel, imagePathLabel, browseButton);

		Label availableLabel = new Label("Available:");
		availableLabel.setStyle("-fx-text-fill: white;");
		grid.addRow(5, availableLabel, availableCheckBox);

		// Buttons for Add and Cancel
		ImageView AddIcon = new ImageView("file:add.png");
		AddIcon.setFitWidth(20);
		AddIcon.setFitHeight(20);
		Button addButton = new Button(" Add", AddIcon);
		addButton.setStyle("-fx-text-fill: #283645; -fx-background-color: #E91E63; -fx-font-weight: bold;");

		ImageView cancelIcon = new ImageView("file:cancel.png");
		cancelIcon.setFitWidth(20);
		cancelIcon.setFitHeight(20);
		Button cancelButton = new Button("Cancel", cancelIcon);
		cancelButton.setStyle("-fx-text-fill: #283645; -fx-background-color: #E91E63; -fx-font-weight: bold;");

		// Add action to the Add button
		addButton.setOnAction(e -> {
			if (validateInputs(nameField, priceField, categoryField, sizeComboBox)) {
				String newCategory = categoryField.getText().trim();

				// Add new category to the ListView if it doesn't exist
				if (!categoryListView.getItems().contains(newCategory)) {
					categoryListView.getItems().add(newCategory);
				}

				// Create a new menu item
				Menu newMenuItem = new Menu(0, nameField.getText(), Double.parseDouble(priceField.getText()),
						newCategory, sizeComboBox.getValue(), availableCheckBox.isSelected(), imagePathLabel.getText());

				// Insert the new item into the database
				insertMenuIntoDatabase(newMenuItem);

				// Reload categories and refresh the grid
				loadCategoriesFromDatabase();
				showMenuGrid("All Categories");

				// Close the stage
				stage.close();
			}
		});

		// Add action to the Cancel button
		cancelButton.setOnAction(e -> {
			stage.close();
		});

		// Create a horizontal box for buttons
		HBox buttonBox = new HBox(10, addButton, cancelButton);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(10));

		// Create a vertical box for the layout (grid + buttons)
		VBox layout = new VBox(10, grid, buttonBox);
		layout.setPadding(new Insets(10));
		layout.setStyle("-fx-background-color: #283645;");

		// Create and set the scene
		Scene scene = new Scene(layout, 500, 350);
		stage.setScene(scene);

		// Show the stage
		stage.show();
	}

	private void insertMenuIntoDatabase(Menu menu) {
		try {
			connectDatabase();
			String query = "INSERT INTO Menu (itemName, itemPrice, category, size, isAvailable, imagePath) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, menu.getItemName());
			stmt.setDouble(2, menu.getItemPrice());
			stmt.setString(3, menu.getCategory());
			stmt.setString(4, menu.getSize());
			stmt.setBoolean(5, menu.isAvailable());
			stmt.setString(6, menu.getImagePath());

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// to check all item info entered
	private boolean validateInputs(TextField nameField, TextField priceField, TextField categoryField,
			ComboBox<String> sizeComboBox) {
		StringBuilder errorMessage = new StringBuilder();

		if (nameField.getText().trim().isEmpty()) {
			errorMessage.append("- Name is required.\n");
		}
		if (priceField.getText().trim().isEmpty() || !isValidDouble(priceField.getText().trim())) {
			errorMessage.append("- Valid Price is required.\n");
		}
		if (categoryField.getText().trim().isEmpty()) {
			errorMessage.append("- Category is required.\n");
		}
		if (sizeComboBox.getValue() == null) {
			errorMessage.append("- Size must be selected.\n");
		}

		if (errorMessage.length() > 0) {
			showAlert("Input Validation Error", errorMessage.toString());
			return false;
		}
		return true;
	}

	private void showProductDetails(Menu item) {
		Dialog<Void> detailsDialog = new Dialog<>();
		detailsDialog.setTitle("Product Details");

		// Set background color for the dialog content area
		detailsDialog.getDialogPane().setStyle("-fx-background-color: #283645;");

		// Create layout for the product details
		GridPane detailsGrid = new GridPane();
		detailsGrid.setHgap(10);
		detailsGrid.setVgap(10);
		detailsGrid.setPadding(new Insets(20));
		detailsGrid.setStyle("-fx-background-color: #283645;");

		// Display product details with white font color
		Label itemNameLabel = new Label("Item Name:");
		itemNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
		Label name = new Label(item.getItemName());
		name.setStyle("-fx-text-fill: white;");
		detailsGrid.addRow(0, itemNameLabel, name);

		Label categoryLabel = new Label("Category:");
		categoryLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
		Label category = new Label(item.getCategory());
		category.setStyle("-fx-text-fill: white;");
		detailsGrid.addRow(1, categoryLabel, category);

		Label sizeLabel = new Label("Size:");
		sizeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
		Label size = new Label(item.getSize());
		size.setStyle("-fx-text-fill: white;");
		detailsGrid.addRow(2, sizeLabel, size);

		Label priceLabel = new Label("Price:");
		priceLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
		Label price = new Label(item.getItemPrice() + " ₪");
		price.setStyle("-fx-text-fill: white;");
		detailsGrid.addRow(3, priceLabel, price);

		Label availabilityLabel = new Label("Availability:");
		availabilityLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
		Label available = new Label(item.isAvailable() ? "Available" : "Out of Stock");
		available.setStyle("-fx-text-fill: white;");
		detailsGrid.addRow(4, availabilityLabel, available);

		ImageView itemImage = new ImageView(new Image("file:" + item.getImagePath()));
		itemImage.setFitWidth(200);
		itemImage.setFitHeight(200);
		itemImage.setPreserveRatio(true);
		detailsGrid.addRow(5, new Label("Image:"), itemImage);

		detailsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		detailsDialog.getDialogPane().setContent(detailsGrid);
		detailsDialog.showAndWait();
	}

	// Utility method to check if a string is a valid double
	private boolean isValidDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// search item by itemName OR category
	private void searchMenuItems(String searchText) {
		ArrayList<Menu> filteredItems = new ArrayList<>();
		try {
			connectDatabase();
			String query = "SELECT * FROM Menu WHERE itemName LIKE ? OR category LIKE ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, "%" + searchText + "%");
			stmt.setString(2, "%" + searchText + "%");

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				filteredItems.add(new Menu(rs.getInt("menuItemId"), rs.getString("itemName"), rs.getDouble("itemPrice"),
						rs.getString("category"), rs.getString("size"), rs.getBoolean("isAvailable"),
						rs.getString("imagePath")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		showFilteredMenuGrid(filteredItems);
	}

	private void showFilteredMenuGrid(ArrayList<Menu> filteredItems) {
		GridPane menuGrid = new GridPane();
		menuGrid.setPadding(new Insets(20));
		menuGrid.setHgap(15);
		menuGrid.setVgap(15);
		menuGrid.setAlignment(Pos.CENTER);

		for (int i = 0; i < filteredItems.size(); i++) {
			Menu item = filteredItems.get(i);
			VBox menuItem = createMenuItem(item);
			menuGrid.add(menuItem, i % 5, i / 5);
		}

		ScrollPane scrollPane = new ScrollPane(menuGrid);
		scrollPane.setFitToWidth(true);
		root.setCenter(scrollPane);
	}

	private TableView<Map<String, Object>> createStatTable(String title) {
		TableView<Map<String, Object>> table = new TableView<>();
		table.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #dddddd; -fx-border-width: 1;");
		table.setPlaceholder(new Label("No data to display"));

		TableColumn<Map<String, Object>, String> categoryCol = new TableColumn<>("Category");
		categoryCol.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("category")));
		categoryCol.setCellFactory(column -> {
			return new TableCell<>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						setText(item);
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		TableColumn<Map<String, Object>, String> valueCol = new TableColumn<>(title);
		valueCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("value").toString()));
		valueCol.setCellFactory(column -> {
			return new TableCell<>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						setText(item);
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		table.getColumns().addAll(categoryCol, valueCol);
		table.setPrefHeight(200);
		return table;
	}

	private TableView<Map<String, Object>> createSalesTable(String title) {
		TableView<Map<String, Object>> table = new TableView<>();
		table.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #dddddd; -fx-border-width: 1;");
		table.setPlaceholder(new Label("No data to display"));

		TableColumn<Map<String, Object>, String> categoryCol = new TableColumn<>("Category");
		categoryCol.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("category")));
		categoryCol.setCellFactory(column -> {
			return new TableCell<>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						setText(item);
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		TableColumn<Map<String, Object>, String> itemCol = new TableColumn<>("Item");
		itemCol.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("item")));
		itemCol.setCellFactory(column -> {
			return new TableCell<>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						setText(item);
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		TableColumn<Map<String, Object>, String> salesCol = new TableColumn<>(title);
		salesCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("sales").toString()));
		salesCol.setCellFactory(column -> {
			return new TableCell<>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						setText(item);
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		table.getColumns().addAll(categoryCol, itemCol, salesCol);
		table.setPrefHeight(200);
		return table;
	}

	private void connectDatabase() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/FinalPhase?useSSL=false";
			String username = "root";
			String password = "root1234";
			connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("MySQL Connector/J was not found at runtime. Expected: lib/mysql-connector-j.jar", e);
		} catch (SQLException e) {
			throw new IllegalStateException("Database connection failed for jdbc:mysql://127.0.0.1:3306/FinalPhase using user 'root': "
					+ e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
