package application;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class OrderApp extends Application {

	private BorderPane root;
	private Connection connection;
	private ListView<String> orderListView;
	private Label totalPriceLabel;
	private double totalPrice = 0.0;
	private Button completeOrderButton;

	RestaurantMenu menu = new RestaurantMenu();
	private TableView<Order> orderTableView;
	private ObservableList<Order> ordersList;
	private int branchId; // Branch ID for filtering

	@Override
	public void start(Stage primaryStage) {
		// Initialize the database connection
		connectDatabase();
		// Create a custom dialog for branch selection
		Dialog<Integer> branchDialog = new Dialog<>();
		branchDialog.setTitle("Branch Selection");
		branchDialog.setHeaderText("Welcome to the Restaurant Management System\nPlease select your branch");

		// Set custom styles
		branchDialog.getDialogPane().setStyle("-fx-background-color: #283645; -fx-text-fill: white;");
		branchDialog.setHeaderText(null);
		Label headerLabel = new Label("Welcome to the Restaurant Management System\nPlease select your branch:");
		headerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-family: Arial;");
		branchDialog.getDialogPane().setContent(headerLabel);

		// Create a ComboBox for branch selection
		ComboBox<String> branchComboBox = new ComboBox<>();
		branchComboBox.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: white;");

		// Fetch branch names from the database
		List<String> branchNames = fetchBranchNamesFromDatabase(connection);

		// Populate the ComboBox with the branch names
		branchComboBox.getItems().addAll(branchNames);

		// Create a GridPane for the dialog content
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setStyle("-fx-background-color: #283645;");

		Label branchLabel = new Label("Select Branch:");
		branchLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-family: Arial;");

		gridPane.add(branchLabel, 0, 0);
		gridPane.add(branchComboBox, 1, 0);

		branchDialog.getDialogPane().setContent(gridPane);

		// Add OK and Cancel buttons
		ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
		branchDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

		// Handle button action
		branchDialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButton) {
				String selectedBranchName = branchComboBox.getValue();
				if (selectedBranchName != null) {
					// get branch id according to selection
					int selectedBranchId = getBranchIdByName(selectedBranchName, connection);
					return selectedBranchId;
				} else {
					showError("Please select a valid branch.");

				}
			}
			return null;
		});

		// Show dialog and process the result
		branchDialog.showAndWait().ifPresent(input -> {
			try {
				branchId = input;
				initializeUI(primaryStage);
				System.out.println("Selected Branch ID: " + branchId);

				// Show the menu for the selected branch
				showMenuGrid("All Categories");
			} catch (Exception e) {
				showError("An error occurred while processing the branch ID.");
			}
		});
	}

	// Fetch branch names from the database
	private List<String> fetchBranchNamesFromDatabase(Connection connection) {
		List<String> branchNames = new ArrayList<>();
		try {
			// Connect to the database and retrieve the branch names
			String query = "SELECT BranchName FROM Branches";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				branchNames.add(rs.getString("BranchName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return branchNames;
	}

	// show menu
	private void showMenuGrid(String category) {
		if (root != null) {
			GridPane menuGrid = new GridPane();
			menuGrid.setPadding(new Insets(20));
			menuGrid.setHgap(15);
			menuGrid.setVgap(15);
			menuGrid.setStyle("-fx-background-color: #FFFFFF;");
			menuGrid.setAlignment(Pos.CENTER);

			ArrayList<Menu> menuItems = menu.loadMenuItemsFromDatabase(category);

			for (int i = 0; i < menuItems.size(); i++) {
				Menu item = menuItems.get(i);
				VBox menuItem = createMenuItem(item);
				menuGrid.add(menuItem, i % 3, i / 3); // Add to grid (3 items per row)
			}

			// Wrap the menu grid in a scroll pane
			ScrollPane scrollPane = new ScrollPane();
			scrollPane.setContent(menuGrid);
			scrollPane.setFitToWidth(true);
			scrollPane.setStyle("-fx-background-color: #FFFFFF;");

			root.setCenter(scrollPane); // Update the center content with the scroll pane
		}
	}

	private VBox createMenuItem(Menu item) {
		VBox menuItem = new VBox(10);
		menuItem.setAlignment(Pos.CENTER);
		menuItem.setPadding(new Insets(10));
		menuItem.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

		// Item image
		ImageView itemImage = new ImageView(new Image("file:" + item.getImagePath()));
		itemImage.setFitWidth(150);
		itemImage.setFitHeight(150);
		itemImage.setPreserveRatio(true);

		// Item label
		Label itemName = new Label(item.getItemName());
		itemName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

		Label itemPrice = new Label("Price: ₪" + item.getItemPrice());
		itemPrice.setStyle("-fx-font-size: 12px;");

		ImageView ADDto = new ImageView(new Image("file:addToOrder.png"));
		ADDto.setFitWidth(20);
		ADDto.setFitHeight(20);
		Button addToOrderButton = new Button("Add to Order", ADDto);
		addToOrderButton.setOnAction(e -> addToOrder(item, branchId));
		addToOrderButton.setStyle("-fx-background-color: #283645; -fx-text-fill: white; -fx-font-size: 14px;");

		menuItem.getChildren().addAll(itemImage, itemName, itemPrice, addToOrderButton);
		return menuItem;
	}

//	private ArrayList<Menu> loadMenuItemsFromDatabase(String category) {
//		ArrayList<Menu> menuItems = new ArrayList<>();
//		try {
//			String query = category.equals("All Categories") ? "SELECT * FROM Menu"
//					: "SELECT * FROM Menu WHERE category = ?";
//			PreparedStatement stmt = connection.prepareStatement(query);
//			if (!category.equals("All Categories")) {
//				stmt.setString(1, category);
//			}
//			ResultSet rs = stmt.executeQuery();
//
//			while (rs.next()) {
//				int menuItemId = rs.getInt("menuItemId");
//				String itemName = rs.getString("itemName");
//				double price = rs.getDouble("itemPrice");
//				String menuCategory = rs.getString("category");
//				String size = rs.getString("size");
//				boolean isAvailable = rs.getBoolean("isAvailable");
//				String imagePath = rs.getString("imagePath");
//
//				Menu menuItem = new Menu(menuItemId, itemName, price, menuCategory, size, isAvailable, imagePath);
//				menuItems.add(menuItem);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return menuItems;
//	}
//
	private void loadCategoriesFromDatabase(ListView<String> categoryListView) {
		try {
			String query = "SELECT DISTINCT category FROM Menu"; // Removed branch_id condition
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			categoryListView.getItems().add("All Categories"); // Default category
			while (rs.next()) {
				categoryListView.getItems().add(rs.getString("category"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Get Branch ID by branch name
	private int getBranchIdByName(String branchName, Connection connection) {
		try {
			// Query to get the branch ID based on the selected branch name
			String query = "SELECT BranchID FROM Branches WHERE BranchName = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, branchName);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("BranchID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // Return -1 if no matching branch found
	}

	private void initializeUI(Stage primaryStage) {
		root = new BorderPane();

		// Initialize orders list
		ordersList = FXCollections.observableArrayList();

		// Show menu grid
		showMenuGrid("All Categories");

		// Left Sidebar for Categories (Filtered by Branch)
		VBox categorySidebar = createCategorySidebar();

		// Other UI elements (center and right sections)
		HBox topToolbar = createTopToolbar();
		VBox orderSummaryPanel = createOrderSummaryPanel();

		// Create Back Button
		// Back button
		ImageView backIcon = new ImageView("file:left-arrow.png");
		backIcon.setFitWidth(20);
		backIcon.setFitHeight(20);
		Button backButton = new Button(" Back", backIcon);
		backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
		backButton.setOnAction(e -> {
			RestaurantManagementSystem ResurantStage = new RestaurantManagementSystem();
			ResurantStage.start(primaryStage);
		}); // Action for back button

		categorySidebar.getChildren().addAll(backButton);
		root.setLeft(categorySidebar);

		root.setLeft(categorySidebar);
		root.setCenter(new GridPane()); // Placeholder for menu grid
		root.setRight(orderSummaryPanel);
		root.setTop(topToolbar);

		Scene scene = new Scene(root, 1530, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Restaurant Management System - Branch #" + branchId);
		primaryStage.show();
	}

	// create right side
	private VBox createOrderSummaryPanel() {
		VBox orderSummaryPanel = new VBox(10);
		orderSummaryPanel.setPadding(new Insets(20));
		orderSummaryPanel.setStyle("-fx-background-color: #283645;");

		// Label for the order summary
		Label orderLabel = new Label("Order Summary");
		orderLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

		// ListView to display items in the order
		orderListView = new ListView<>();
		orderListView.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645;-fx-font-weight: bold;");
		orderSummaryPanel.getChildren().addAll(orderLabel, orderListView);

		// Label to show the total price of the order
		totalPriceLabel = new Label("Total Price: ₪0.00");
		totalPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");
		orderSummaryPanel.getChildren().add(totalPriceLabel);

		// Button to complete the order
		ImageView completeIcon = new ImageView("file:order.png");
		completeIcon.setFitWidth(20);
		completeIcon.setFitHeight(20);

		completeOrderButton = new Button("Complete Order", completeIcon);
		completeOrderButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		completeOrderButton.setDisable(true); // Initially disabled

		completeOrderButton.setOnAction(e -> {
			if (orderListView.getItems().isEmpty()) {
				showError("Please add items to your order before completing.");
				return;
			}
			showCustomerInputStage();
		});

		ImageView deleteIcon = new ImageView("file:deleteFromorder.png");
		deleteIcon.setFitWidth(20);
		deleteIcon.setFitHeight(20);
		Button deleteButton = new Button("Delete", deleteIcon);
		deleteButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");

		deleteButton.setOnAction(e -> {
			String selectedOrder = orderListView.getSelectionModel().getSelectedItem();

			if (selectedOrder != null) {
				int existingQuantity = extractQuantityFromOrder(selectedOrder);
				double itemPrice = extractPriceFromOrder(selectedOrder);

				if (existingQuantity > 1) {

					int newQuantity = existingQuantity - 1;
					// update quantity after delete
					String updatedOrder = updateOrderString(selectedOrder, newQuantity);
					orderListView.getItems().set(orderListView.getSelectionModel().getSelectedIndex(), updatedOrder);
					// update price after delete
					totalPrice -= itemPrice;
					totalPriceLabel.setText("Total Price: ₪" + totalPrice);
				} else {
					// remove from list
					orderListView.getItems().remove(selectedOrder);

					totalPrice -= itemPrice;
					totalPriceLabel.setText("Total Price: ₪" + totalPrice);

					if (orderListView.getItems().isEmpty()) {
						completeOrderButton.setDisable(true);
					}
				}
			} else {
				showError("Please select an item to delete.");
			}
		});

		orderSummaryPanel.getChildren().add(deleteButton);
		orderSummaryPanel.getChildren().add(completeOrderButton);

		return orderSummaryPanel;
	}

	// update Quantity
	private String updateOrderString(String order, int newQuantity) {
		String[] parts = order.split(" x ");
		return parts[0] + " x " + newQuantity;
	}

	// create left side
	private VBox createCategorySidebar() {
		VBox categorySidebar = new VBox(10);
		categorySidebar.setPadding(new Insets(10));
		categorySidebar.setStyle("-fx-background-color: #283645;");

		Label categoryLabel = new Label("Categories");
		categoryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

		ListView<String> categoryListView = new ListView<>();
		categoryListView.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645;-fx-font-weight: bold;");

		// Load categories for the branch
		loadCategoriesFromDatabase(categoryListView);

		categoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				showMenuGrid(newValue); // Load menu items based on the selected category
			}
		});

		categorySidebar.getChildren().addAll(categoryLabel, categoryListView);
		return categorySidebar;
	}

	// Create top UI
	private HBox createTopToolbar() {
		HBox topToolbar = new HBox(10);
		topToolbar.setPadding(new Insets(10));
		topToolbar.setStyle("-fx-background-color: #283645;");
		ImageView buttondImage = new ImageView("file:operation.png");
		buttondImage.setFitWidth(20); // Set width of image (adjust as needed)
		buttondImage.setFitHeight(20); // Set height of image (adjust as needed)
		buttondImage.setPreserveRatio(true);
		ImageView statImage = new ImageView("file:statistics.png");
		statImage.setFitWidth(20); // Set width of image (adjust as needed)
		statImage.setFitHeight(20); // Set height of image (adjust as needed)
		statImage.setPreserveRatio(true);

		Button operateButton = new Button("Operate", buttondImage);
		operateButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
		operateButton.setOnAction(e -> showOperationStage(branchId));

		Button StatButton = new Button("Statistics", statImage);
		StatButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
		StatButton.setOnAction(e -> showStatisticsView());
		topToolbar.getChildren().addAll(operateButton, StatButton);
		return topToolbar;
	}

	// Helper method to show an error message (you can modify this as needed)
	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// to enter customer info
	private void showCustomerInputStage() {
		Stage customerStage = new Stage();
		customerStage.setTitle("Customer Information");

		// Input fields for customer details
		TextField nameField = new TextField();
		nameField.setPromptText("Customer Name");
		nameField.setStyle(
				"-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-border-color: #FFFFFF; -fx-border-radius: 5px;");

		TextField phoneField = new TextField();
		phoneField.setPromptText("Phone Number");
		phoneField.setStyle(
				"-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-border-color: #FFFFFF; -fx-border-radius: 5px;");
		// Limit phoneField to 10 digits
		phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
			// Remove non-numeric characters
			if (!newValue.matches("\\d*")) {
				phoneField.setText(newValue.replaceAll("[^\\d]", ""));
			}
			// Limit to 10 digits
			if (phoneField.getText().length() > 10) {
				phoneField.setText(phoneField.getText().substring(0, 10));
			}
		});

		ComboBox<String> paymentMethodComboBox = new ComboBox<>();
		paymentMethodComboBox.getItems().addAll("Cash", "Credit Card", "Debit Card", "online Payment");
		paymentMethodComboBox.setPromptText("Payment Method");
		paymentMethodComboBox.setStyle(
				"-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-border-color: #FFFFFF; -fx-border-radius: 5px;");

		CheckBox diningInCheckBox = new CheckBox("Dining In");
		diningInCheckBox.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

		// Save button
		Button saveButton = new Button("Save");
		saveButton.setStyle(
				"-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 5px;");
		saveButton.setOnAction(e -> {
			String name = nameField.getText().trim();
			String phone = phoneField.getText().trim();
			String paymentMethod = paymentMethodComboBox.getValue();
			boolean isDiningIn = diningInCheckBox.isSelected();

			if (!name.isEmpty() && paymentMethod != null) {
				int customerId = saveCustomerToDatabase(name, phone, paymentMethod, isDiningIn);
				if (customerId != -1) {
					saveOrderToDatabase(customerId);
					customerStage.close();
					resetOrder();
					showSuccess("Order completed successfully!");
				} else {
					showError("Failed to save customer information.");
				}
			} else {
				showError("Please fill in all required fields.");
			}
		});

		// Layout
		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(20));
		gridPane.add(createStyledLabel("Name:"), 0, 0);
		gridPane.add(nameField, 1, 0);
		gridPane.add(createStyledLabel("Phone:"), 0, 1);
		gridPane.add(phoneField, 1, 1);
		gridPane.add(createStyledLabel("Payment Method:"), 0, 2);
		gridPane.add(paymentMethodComboBox, 1, 2);
		gridPane.add(createStyledLabel("Dining In:"), 0, 3);
		gridPane.add(diningInCheckBox, 1, 3);

		VBox layout = new VBox(20, gridPane, saveButton);
		layout.setStyle(
				"-fx-background-color: #283645; -fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-border-radius: 10px;");
		layout.setPadding(new Insets(20));
		Scene scene = new Scene(layout, 400, 300);
		customerStage.setScene(scene);
		customerStage.show();
	}

	private Label createStyledLabel(String text) {
		Label label = new Label(text);
		label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
		return label;
	}

	// after finsh order
	private void resetOrder() {
		orderListView.getItems().clear();
		totalPrice = 0.0;
		totalPriceLabel.setText("Total Price: ₪0.00");
		completeOrderButton.setDisable(true);
	}

	// insert order
	private void saveOrderToDatabase(int customerId) {
		String orderQuery = "INSERT INTO Orders (customerId, menuItemId, branchId, quantity, orderDate) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(orderQuery)) {
			for (String order : orderListView.getItems()) {
				String itemName = order.split(" - ")[0].trim();
				int quantity = extractQuantityFromOrder(order);

				// Get menuItemId from the Menu table
				int menuItemId = getMenuItemIdByName(itemName);

				// Insert order into Orders table
				stmt.setInt(1, customerId);
				stmt.setInt(2, menuItemId);
				stmt.setInt(3, branchId); // Include branchId
				stmt.setInt(4, quantity);
				stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// get id of item
	private int getMenuItemIdByName(String itemName) throws SQLException {
		String query = "SELECT menuItemId FROM Menu WHERE itemName = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, itemName);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return rs.getInt("menuItemId");
		} else {
			throw new SQLException("Menu item not found: " + itemName);
		}
	}

	// save customer info
	private int saveCustomerToDatabase(String name, String phone, String paymentMethod, boolean isDiningIn) {
		int customerId = -1;
		try {
			connectDatabase();
			// Check if the customer already exists
			String checkQuery = "SELECT customerId FROM Customer WHERE name = ? AND phoneNumber = ?";
			try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
				checkStmt.setString(1, name);
				checkStmt.setString(2, phone);
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next()) {
					// Customer exists, return their ID
					customerId = rs.getInt("customerId");
					return customerId;
				}
			}
			// If customer does not exist, insert them into the database
			String insertQuery = "INSERT INTO Customer (name, phoneNumber, paymentMethod, isDiningIn) VALUES (?, ?, ?, ?)";
			try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery,
					Statement.RETURN_GENERATED_KEYS)) {
				insertStmt.setString(1, name);
				insertStmt.setString(2, phone);
				insertStmt.setString(3, paymentMethod);
				insertStmt.setBoolean(4, isDiningIn);
				insertStmt.executeUpdate();

				ResultSet rs = insertStmt.getGeneratedKeys();
				if (rs.next()) {
					customerId = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return customerId;
	}

	// Create order
	private void addToOrder(Menu item, int branchId) {
		boolean itemExists = false;

		// Iterate through existing orders to check if the item already exists
		for (int i = 0; i < orderListView.getItems().size(); i++) {
			String existingOrder = orderListView.getItems().get(i);
			String existingItemName = extractItemNameFromOrder(existingOrder);

			if (existingItemName.equals(item.getItemName())) {
				// Extract the existing quantity and price from the order
				int existingQuantity = extractQuantityFromOrder(existingOrder);
				double itemPrice = extractPriceFromOrder(existingOrder);

				// Update the quantity
				int newQuantity = existingQuantity + 1;
				String updatedOrder = item.getItemName() + " - ₪" + itemPrice + " x " + newQuantity;

				// Update the item in the order list
				orderListView.getItems().set(i, updatedOrder);

				// Update the total price
				totalPrice += itemPrice;
				totalPriceLabel.setText("Total Price: ₪" + String.format("%.2f", totalPrice));

				itemExists = true;
				break;
			}
		}

		// If the item does not already exist in the order list, add it as a new order
		if (!itemExists) {
			int quantity = 1;
			String newOrder = item.getItemName() + " - ₪" + item.getItemPrice() + " x " + quantity;

			// Add the new item to the order list
			orderListView.getItems().add(newOrder);

			// Update the total price
			totalPrice += item.getItemPrice();
			totalPriceLabel.setText("Total Price: ₪" + String.format("%.2f", totalPrice));

			// Enable the Complete Order button if it was disabled
			completeOrderButton.setDisable(false);
		}
	}

	private String extractItemNameFromOrder(String order) {
		if (order == null || order.isEmpty()) {
			return ""; // Return an empty string if the order is null or empty
		}
		return order.split(" - ")[0].trim(); // Extract the item name before " - "
	}

	private double extractPriceFromOrder(String order) {
		String[] parts = order.split(" - ")[1].split("x")[0].trim().split("₪");
		return Double.parseDouble(parts[1].trim()); // Extracts the price between "₪" and "x"
	}

	private int extractQuantityFromOrder(String order) {
		String[] parts = order.split("x");
		return Integer.parseInt(parts[1].trim()); // Extracts the quantity after "x"
	}

	//
	public void showOperationStage(int branchId) {
		Stage stage = new Stage();
		stage.setTitle("Order Operations - Branch ID: " + branchId);

		// Create the TableView to show all orders
		orderTableView = new TableView<>();
		ordersList = FXCollections.observableArrayList();
		orderTableView.setItems(ordersList);

		// Fetch orders for the specific branch from the database
		fetchOrdersFromDatabase(branchId);

		// Define the columns for the TableView
		TableColumn<Order, Integer> orderIdColumn = new TableColumn<>("Order ID");
		orderIdColumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());
		orderIdColumn
				.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #283645;");
		TableColumn<Order, Integer> customerIdColumn = new TableColumn<>("Customer ID");
		customerIdColumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
		customerIdColumn
				.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #283645;");

		TableColumn<Order, Integer> menuItemIdColumn = new TableColumn<>("Menu Item ID");
		menuItemIdColumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getMenuItemId()).asObject());
		menuItemIdColumn
				.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #283645;");

		TableColumn<Order, Integer> branchIdColumn = new TableColumn<>("Branch ID");
		branchIdColumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getBranchId()).asObject());
		branchIdColumn
				.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #283645;");

		TableColumn<Order, Integer> quantityColumn = new TableColumn<>("Quantity");
		quantityColumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
		quantityColumn
				.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #283645;");

		TableColumn<Order, String> orderDateColumn = new TableColumn<>("Order Date");
		orderDateColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getOrderDate().toString()));
		orderDateColumn
				.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #283645;");

		// Add the columns to the TableView
		orderTableView.getColumns().addAll(orderIdColumn, customerIdColumn, menuItemIdColumn, branchIdColumn,
				quantityColumn, orderDateColumn);

		// Style for TableView
		orderTableView.setStyle(
				"-fx-background-color: #2e3b4e; -fx-text-fill: #fff; -fx-font-size: 14px; -fx-font-weight: normal;");
		orderTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Create the "Delete" button
		Button deleteButton = new Button("Delete Order");
		deleteButton.setStyle(
				"-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 15;");
		deleteButton.setOnAction(e -> {
			Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
			if (selectedOrder != null) {
				handleDeleteOrder(selectedOrder);
				ordersList.remove(selectedOrder);
			} else {
				showError("Please select an order to delete.");
			}
		});

		// Create the "Update" button
		Button updateButton = new Button("Update Order");
		updateButton.setStyle(
				"-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 15;");
		updateButton.setOnAction(e -> {
			Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
			if (selectedOrder != null) {
				showUpdateOrderDialog(selectedOrder, branchId);
			} else {
				showError("Please select an order to update.");
			}
		});

		orderTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				int selectedOrderId = newSelection.getOrderId();
				showOrderDetailsFromDatabase(selectedOrderId);
			}
		});

		// Layout for the buttons
		HBox buttonBox = new HBox(10, deleteButton, updateButton);
		buttonBox.setPadding(new Insets(15));
		buttonBox.setAlignment(Pos.CENTER);

		// Header Label for the stage
		Label headerLabel = new Label("Order Operations");
		headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fff; -fx-padding: 10 0;");

		// Layout for the TableView and buttons
		VBox layout = new VBox(10, headerLabel, orderTableView, buttonBox);
		layout.setPadding(new Insets(15));
		layout.setStyle("-fx-background-color: #283645; -fx-border-color: white; -fx-border-width: 2px;");

		// Set up the scene and show the stage
		Scene scene = new Scene(layout, 800, 600);
		stage.setScene(scene);
		stage.show();
	}

	private void handleDeleteOrder(Order order) {
		String sql = "DELETE FROM Orders WHERE orderId = ?";
		try (PreparedStatement pst = connection.prepareStatement(sql)) {
			pst.setInt(1, order.getOrderId());
			int rowsAffected = pst.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Order with ID " + order.getOrderId() + " was deleted successfully.");
				ordersList.remove(order);
			} else {
				showError("No order found with ID " + order.getOrderId() + ".");
			}
		} catch (SQLException e) {
			showError("Failed to delete order: " + e.getMessage());
		}
	}

	private void fetchOrdersFromDatabase(int branchId) {
		ordersList.clear(); // Clear the existing orders list
		try {
//			String query = "SELECT o.orderId, o.customerId, o.menuItemId, o.branchId, o.quantity, o.orderDate "
//					+ "FROM Orders o " + "JOIN Menu m ON o.menuItemId = m.menuItemId " + "WHERE o.branchId = ?";
			String query = "SELECT o.orderId, o.customerId, o.menuItemId, o.branchId, o.quantity, o.orderDate "
					+ "FROM Orders o  " + "WHERE o.branchId = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, branchId); // Set the branchId dynamically
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ordersList.add(new Order(rs.getInt("orderId"), rs.getInt("customerId"), rs.getInt("menuItemId"),
						rs.getInt("branchId"), rs.getInt("quantity"), rs.getTimestamp("orderDate")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showOrderDetailsFromDatabase(int orderId) {
		String query = "SELECT c.customerId, c.name AS CustomerName, c.phoneNumber AS PhoneNumber, "
				+ "c.paymentMethod AS PaymentMethod, c.isDiningIn AS IsDiningIn, "
				+ "o.orderDate, o.quantity, m.itemName AS MenuItem, m.itemPrice AS ItemPrice, "
				+ "m.category AS Category " + "FROM Customer c " + "JOIN Orders o ON c.customerId = o.customerId "
				+ "JOIN Menu m ON o.menuItemId = m.menuItemId " + "WHERE o.orderId = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, orderId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				// Create a dialog to display details
				Dialog<ButtonType> detailsDialog = new Dialog<>();
				detailsDialog.setTitle("Order Details");

				// Set the header text with custom styling
				Label headerLabel = new Label("Details for Order ID: " + orderId);
				headerLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 16px;");
				detailsDialog.setHeaderText(null); // Clear default header text
				detailsDialog.getDialogPane().setHeader(headerLabel);

				// Style the dialog pane
				detailsDialog.getDialogPane().setStyle("-fx-background-color: #2B3E50; -fx-padding: 20px;");

				// Layout for order details
				GridPane grid = new GridPane();
				grid.setHgap(15);
				grid.setVgap(10);
				grid.setPadding(new Insets(20));
				grid.setStyle("-fx-background-color: #2B3E50;");

				// Style for labels
				String labelStyle = "-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px;";
				String valueStyle = "-fx-text-fill: #FF4081; -fx-font-size: 14px;";

				// Add details to the grid
				addDetailToGrid(grid, "Customer Name:", rs.getString("CustomerName"), 0, labelStyle, valueStyle);
				addDetailToGrid(grid, "Phone Number:", rs.getString("PhoneNumber"), 1, labelStyle, valueStyle);
				addDetailToGrid(grid, "Payment Method:", rs.getString("PaymentMethod"), 2, labelStyle, valueStyle);
				addDetailToGrid(grid, "Dining Status:", rs.getBoolean("IsDiningIn") ? "Dining In" : "Takeaway", 3,
						labelStyle, valueStyle);
				addDetailToGrid(grid, "Menu Item:", rs.getString("MenuItem"), 4, labelStyle, valueStyle);
				addDetailToGrid(grid, "Quantity:", String.valueOf(rs.getInt("quantity")), 5, labelStyle, valueStyle);
				addDetailToGrid(grid, "Price:", String.format("%.2f", rs.getDouble("ItemPrice")), 6, labelStyle,
						valueStyle);
				addDetailToGrid(grid, "Category:", rs.getString("Category"), 7, labelStyle, valueStyle);
				addDetailToGrid(grid, "Order Date:", rs.getTimestamp("orderDate").toString(), 8, labelStyle,
						valueStyle);

				// Set grid content to the dialog pane
				detailsDialog.getDialogPane().setContent(grid);

				// Add Close button with styling
				ButtonType closeButtonType = ButtonType.CLOSE;
				detailsDialog.getDialogPane().getButtonTypes().add(closeButtonType);
				Button closeButton = (Button) detailsDialog.getDialogPane().lookupButton(closeButtonType);
				closeButton.setStyle(
						"-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10;");

				// Show dialog
				detailsDialog.showAndWait();
			} else {
				showError("Error", "No details found for the selected order.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addDetailToGrid(GridPane grid, String labelText, String value, int row, String labelStyle,
			String valueStyle) {
		if (value == null) {
			value = "N/A"; // Default for null values
		}
		grid.add(createStyledLabel(labelText, labelStyle), 0, row);
		grid.add(createStyledLabel(value, valueStyle), 1, row);
	}

	private Label createStyledLabel(String text, String style) {
		Label label = new Label(text);
		label.setStyle(style);
		return label;
	}

	private void showUpdateOrderDialog(Order order, int branchId) {
		// Create a dialog for updating order details
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Update Order");
		dialog.setHeaderText("Update details for Order ID: " + order.getOrderId());

		// Style the dialog pane
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.setStyle(
				"-fx-background-color: #283645; -fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: white;");

		// Create input fields for updating order
		TextField quantityField = new TextField(String.valueOf(order.getQuantity()));
		quantityField.setPromptText("Enter Quantity");
		quantityField.setStyle(
				"-fx-background-color: #2F4F4F; -fx-text-fill: white; -fx-border-color: #FFFFFF; -fx-border-width: 1px;");

		ComboBox<String> menuItemComboBox = new ComboBox<>();
		menuItemComboBox.getItems().addAll(fetchMenuItems()); // Populate with all menu items
		menuItemComboBox.setValue(fetchMenuItemName(order.getMenuItemId())); // Set current menu item
		menuItemComboBox.setStyle(
				"-fx-background-color: #2F4F4F; -fx-text-fill: white; -fx-border-color: #FFFFFF; -fx-border-width: 1px;");

		// Style labels
		Label quantityLabel = new Label("Quantity:");
		quantityLabel.setStyle("-fx-text-fill: white;");

		Label menuItemLabel = new Label("Menu Item:");
		menuItemLabel.setStyle("-fx-text-fill: white;");

		// Layout for dialog
		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(20));
		gridPane.setStyle("-fx-background-color: #283645;");

		gridPane.add(quantityLabel, 0, 0);
		gridPane.add(quantityField, 1, 0);
		gridPane.add(menuItemLabel, 0, 1);
		gridPane.add(menuItemComboBox, 1, 1);

		dialog.getDialogPane().setContent(gridPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Show the dialog and handle input
		dialog.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try {
					// Validate and parse input
					int newQuantity = Integer.parseInt(quantityField.getText().trim());
					String newMenuItemName = menuItemComboBox.getValue();

					if (newQuantity > 0 && newMenuItemName != null) {
						int newMenuItemId = getMenuItemIdByName(newMenuItemName);

						// Update the order in the database
						updateOrderInDatabase(order.getOrderId(), newQuantity, newMenuItemId);

						// Update the UI
						order.setQuantity(newQuantity);
						order.setMenuItemId(newMenuItemId);
						orderTableView.refresh(); // Refresh TableView
					} else {
						showError("Invalid input. Please provide valid details.");
					}
				} catch (NumberFormatException e) {
					showError("Quantity must be a valid number.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void updateOrderInDatabase(int orderId, int newQuantity, int newMenuItemId) {
		String query = "UPDATE Orders SET quantity = ?, menuItemId = ? WHERE orderId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, newQuantity);
			stmt.setInt(2, newMenuItemId);
			stmt.setInt(3, orderId);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Order updated successfully!");
			} else {
				showError("Failed to update the order.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String fetchMenuItemName(int menuItemId) {
		String query = "SELECT itemName FROM Menu WHERE menuItemId = ?"; // Query to fetch the name of the menu item by
																			// ID
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, menuItemId); // Bind the menuItemId parameter
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("itemName"); // Return the menu item name
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // Return null if no matching menuItemId is found
	}

	// Fetch menu items (assuming all items are globally available)
	private List<String> fetchMenuItems() {
		List<String> menuItems = new ArrayList<>();
		String query = "SELECT itemName FROM Menu"; // Fetch all menu items
		try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				menuItems.add(rs.getString("itemName")); // Add each item name to the list
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return menuItems;
	}

	private void showStatisticsView() {
		TabPane tabPane = new TabPane();

		// Customer Statistics Tab
		Tab customerStatsTab = new Tab("Customer Statistics");
		customerStatsTab.setClosable(false);
		customerStatsTab.setContent(createCustomerStatisticsView(branchId));

		// Order Statistics Tab
		Tab orderStatsTab = new Tab("Order Statistics");
		orderStatsTab.setClosable(false);
		orderStatsTab.setContent(createOrderStatisticsView(branchId));

		// Menu Tab
		Tab menuTab = new Tab("Menu");
		menuTab.setClosable(false);
		menuTab.setContent(createMenuStatisticsView(branchId));

		// Add tabs to the TabPane
		tabPane.getTabs().addAll(customerStatsTab, orderStatsTab, menuTab);

		// Set TabPane in the root layout
		root.setCenter(tabPane);
	}

	private VBox createCustomerStatisticsView(int branchId) {
		VBox statsViewArea = new VBox(20);
		statsViewArea.setPadding(new Insets(20));
		statsViewArea.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f0f4f7);");

		// Pie Chart: Customers by Payment Method
		PieChart paymentMethodChart = new PieChart();
		paymentMethodChart.setTitle("Customers by Payment Method");

		try {
			String query = """
					    SELECT c.paymentMethod, COUNT(*) AS customerCount
					    FROM Customer c
					    JOIN Orders o ON c.customerId = o.customerId
					    WHERE o.branchId = ?
					    GROUP BY c.paymentMethod
					""";

			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, branchId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String paymentMethod = rs.getString("paymentMethod");
				int customerCount = rs.getInt("customerCount");
				paymentMethodChart.getData().add(new PieChart.Data(paymentMethod, customerCount));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Label paymentMethodLabel = new Label("Distribution of customers by payment method.");
		paymentMethodLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");

		// Bar Chart: Dining Preferences
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Dining Preference");

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Number of Customers");

		BarChart<String, Number> diningChart = new BarChart<>(xAxis, yAxis);
		diningChart.setTitle("Dining Preferences");
		try {
			String query = """
					    SELECT c.isDiningIn, COUNT(*) AS Count
					    FROM Customer c
					    JOIN Orders o ON c.customerId = o.customerId
					    WHERE o.branchId = ?
					    GROUP BY c.isDiningIn
					""";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, branchId);
			ResultSet rs = stmt.executeQuery();

			XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
			dataSeries.setName("Number of Customers");

			while (rs.next()) {
				boolean isDiningIn = rs.getBoolean("isDiningIn");
				int customerCount = rs.getInt("Count");
				String diningPreference = isDiningIn ? "Dining In" : "Takeaway";
				dataSeries.getData().add(new XYChart.Data<>(diningPreference, customerCount));
			}

			diningChart.getData().add(dataSeries);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		VBox paymentMethodArea = new VBox(10, paymentMethodChart, paymentMethodLabel);
		paymentMethodArea.setAlignment(Pos.CENTER);

		VBox diningChartArea = new VBox(10, diningChart);
		diningChartArea.setAlignment(Pos.CENTER);

		HBox chartsArea = new HBox(30, paymentMethodArea, diningChartArea);
		chartsArea.setAlignment(Pos.CENTER);
		statsViewArea.getChildren().add(chartsArea);
		// Back Button
		Button backButton = new Button("Back to Main View");
		backButton.setStyle(
				"-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px;");
		backButton.setOnAction(e -> root.setCenter(null));

		VBox buttonBox = new VBox(backButton);
		buttonBox.setAlignment(Pos.CENTER);

		statsViewArea.getChildren().add(buttonBox);

		return statsViewArea;
	}

	private VBox createMenuStatisticsView(int branchId) {
		VBox statsViewArea = new VBox(20);
		statsViewArea.setPadding(new Insets(20));
		statsViewArea.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f0f4f7);");

		// Pie Chart: Items by Category
		PieChart categoryPieChart = new PieChart();
		categoryPieChart.setTitle("Menu Items by Category");
		categoryPieChart.setPrefWidth(400);
		categoryPieChart.setPrefHeight(300);

		try {
			String query = "SELECT category, COUNT(*) AS itemCount FROM Menu GROUP BY category";
			PreparedStatement stmt = connection.prepareStatement(query); // No branch filtering for this chart
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String category = rs.getString("category");
				int itemCount = rs.getInt("itemCount");
				categoryPieChart.getData().add(new PieChart.Data(category, itemCount));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Label categoryPieChartLabel = new Label("Distribution of menu items by category.");
		categoryPieChartLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");

		// Bar Chart: Availability of Items
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Availability Status");
		xAxis.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Number of Items");
		yAxis.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");

		BarChart<String, Number> availabilityChart = new BarChart<>(xAxis, yAxis);
		availabilityChart.setTitle("Item Availability");
		availabilityChart.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		availabilityChart.setPrefWidth(500);
		availabilityChart.setPrefHeight(400);

		try {
			String query = "SELECT isAvailable, COUNT(*) AS itemCount FROM Menu GROUP BY isAvailable";
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
			dataSeries.setName("Number of Items");

			while (rs.next()) {
				boolean isAvailable = rs.getBoolean("isAvailable");
				String status = isAvailable ? "Available" : "Not Available";
				int itemCount = rs.getInt("itemCount");
				dataSeries.getData().add(new XYChart.Data<>(status, itemCount));
			}

			availabilityChart.getData().add(dataSeries);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Label availabilityChartLabel = new Label("Availability of items (Available vs Not Available).");
		availabilityChartLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");

		// Combine charts and labels in a layout
		VBox categoryPieChartArea = new VBox(10, categoryPieChart, categoryPieChartLabel);
		categoryPieChartArea.setAlignment(Pos.CENTER);

		VBox availabilityChartArea = new VBox(10, availabilityChart, availabilityChartLabel);
		availabilityChartArea.setAlignment(Pos.CENTER);

		HBox chartsArea = new HBox(30, categoryPieChartArea, availabilityChartArea);
		chartsArea.setAlignment(Pos.CENTER);

		statsViewArea.getChildren().add(chartsArea);

		// Back Button
		Button backButton = new Button("Back to Main View");
		backButton.setStyle(
				"-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px;");
		backButton.setOnAction(e -> root.setCenter(null));

		VBox buttonBox = new VBox(backButton);
		buttonBox.setAlignment(Pos.CENTER);

		statsViewArea.getChildren().add(buttonBox);

		return statsViewArea;
	}

	private VBox createOrderStatisticsView(int branchId) {
		VBox statsViewArea = new VBox(20);
		statsViewArea.setPadding(new Insets(20));
		statsViewArea.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f0f4f7);");

		// Pie Chart: Orders by Category
		PieChart categoryPieChart = new PieChart();
		categoryPieChart.setTitle("Orders by Category");
		categoryPieChart.setPrefWidth(400);
		categoryPieChart.setPrefHeight(300);

		try {
			String query = "SELECT m.category, COUNT(o.orderId) AS orderCount " + "FROM Orders o "
					+ "JOIN Menu m ON o.menuItemId = m.menuItemId " + "WHERE o.branchId = ? " + "GROUP BY m.category";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, branchId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String category = rs.getString("category");
				int orderCount = rs.getInt("orderCount");
				categoryPieChart.getData().add(new PieChart.Data(category, orderCount));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Label categoryPieChartLabel = new Label("Orders distribution by menu category.");
		categoryPieChartLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");

		// Bar Chart: Orders by Menu Items
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Menu Items");
		xAxis.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");
		xAxis.setTickLabelRotation(45);

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Quantity Ordered");
		yAxis.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");

		BarChart<String, Number> itemsBarChart = new BarChart<>(xAxis, yAxis);
		itemsBarChart.setTitle("Orders by Menu Items");
		itemsBarChart.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		itemsBarChart.setPrefWidth(500);
		itemsBarChart.setPrefHeight(400);

		try {
			String query = "SELECT m.itemName, SUM(o.quantity) AS totalQuantity " + "FROM Orders o "
					+ "JOIN Menu m ON o.menuItemId = m.menuItemId " + "WHERE o.branchId = ? " + "GROUP BY m.itemName";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, branchId);
			ResultSet rs = stmt.executeQuery();

			XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
			dataSeries.setName("Quantity Ordered");

			while (rs.next()) {
				String itemName = rs.getString("itemName");
				int totalQuantity = rs.getInt("totalQuantity");
				dataSeries.getData().add(new XYChart.Data<>(itemName, totalQuantity));
			}

			itemsBarChart.getData().add(dataSeries);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Label itemsBarChartLabel = new Label("Total quantities ordered for each menu item.");
		itemsBarChartLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #283645;");

		// Layout for Charts
		VBox categoryPieChartArea = new VBox(10, categoryPieChart, categoryPieChartLabel);
		categoryPieChartArea.setAlignment(Pos.CENTER);

		VBox itemsBarChartArea = new VBox(10, itemsBarChart, itemsBarChartLabel);
		itemsBarChartArea.setAlignment(Pos.CENTER);

		HBox chartsArea = new HBox(30, categoryPieChartArea, itemsBarChartArea);
		chartsArea.setAlignment(Pos.CENTER);

		// Back Button
		Button backButton = new Button("Back to Main View");
		backButton.setStyle(
				"-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px;");
		backButton.setOnAction(e -> root.setCenter(null));

		VBox buttonBox = new VBox(backButton);
		buttonBox.setAlignment(Pos.CENTER);

		statsViewArea.getChildren().addAll(chartsArea, buttonBox);

		return statsViewArea;
	}

	private void showSuccess(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void connectDatabase() {
		try {
			String url = "jdbc:mysql://127.0.0.1:3306/FinalPhase?useSSL=false";
			String username = "root";
			String password = "root1234";
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Database connected successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
