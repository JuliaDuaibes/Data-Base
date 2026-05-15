package application;

import java.sql.*;
import java.util.Properties;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CustomerInterface extends Application {

	private BorderPane root;
	private Connection connection;
	private ObservableList<Customer> customerData;
	private final String dbURL = "jdbc:mysql://127.0.0.1:3306/FinalPhase?verifyServerCertificate=false";
	private final String dbUsername = "root";
	private final String dbPassword = "root1234";

	@Override
	public void start(Stage primaryStage) throws ClassNotFoundException {
		root = new BorderPane();

		// Top Toolbar (Add customer button)
		HBox topToolbar = new HBox(10);
		topToolbar.setPadding(new Insets(10));
		topToolbar.setStyle("-fx-background-color: #283645;");

		// VBox to hold image and button
		VBox TopV = new VBox(15);
		TopV.setAlignment(Pos.CENTER); // To align items in the center

		// Create ImageView for the button
		ImageView buttonImage = new ImageView("file:new-account.png");
		buttonImage.setFitWidth(20); // Set width of image (adjust as needed)
		buttonImage.setFitHeight(20); // Set height of image (adjust as needed)
		buttonImage.setPreserveRatio(true); // Preserve the aspect ratio of the image

		// Create the button with image inside
		Button addCustomerButton = new Button("New Customer", buttonImage);
		addCustomerButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
		addCustomerButton.setOnAction(e -> openCustomerForm());

		// Center Content Area: Customer List View
		loadCustomerListView();

		// Bottom Toolbar (Back button)
		ImageView backIcon = new ImageView("file:left-arrow.png");
		backIcon.setFitWidth(20);
		backIcon.setFitHeight(20);

		HBox bottomToolbar = new HBox(10);
		bottomToolbar.setPadding(new Insets(10));
		bottomToolbar.setStyle("-fx-background-color: #283645;");
		Button backButton = new Button("Back", backIcon);
		backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
		backButton.setOnAction(e -> goBack(primaryStage));

		bottomToolbar.getChildren().add(backButton);
		root.setBottom(bottomToolbar);

		// Statistics Button
		ImageView statIcon = new ImageView("file:statistics.png");
		statIcon.setFitWidth(20);
		statIcon.setFitHeight(20);
		Button statsButton = new Button("Show Statistics", statIcon);
		statsButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
		statsButton.setOnAction(e -> showCustomerStatistics());

		// Add the image and button to VBox
		TopV.getChildren().addAll(addCustomerButton, statsButton);

		// Add VBox to top toolbar
		topToolbar.getChildren().add(TopV);
		root.setTop(topToolbar);
		// Scene setup
		Scene scene = new Scene(root, 1530, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Customer Management System");
		primaryStage.show();
	}

	private void goBack(Stage primaryStage) {
		RestaurantManagementSystem ResurantStage = new RestaurantManagementSystem();
		ResurantStage.start(primaryStage);
	}

	private void loadCustomerListView() {
		try {
			// Load customer data from the database
			loadCustomerFromDatabase();
		} catch (ClassNotFoundException | SQLException e) {
			// Print stack trace if there's an exception during database loading
			e.printStackTrace();
		}

		// Set the background color of the main layout
		root.setStyle("-fx-background-color: #283645");

		// Create a VBox to hold the customer table and buttons
		VBox customerListViewArea = new VBox(10);
		customerListViewArea.setPadding(new Insets(20));

		// Create a TableView for displaying customer information
		TableView<Customer> customerTableView = new TableView<>();
		customerTableView.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black;");

		// Column for Customer ID
		TableColumn<Customer, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		idColumn.setPrefWidth(150);
		idColumn.setCellFactory(column -> {
			return new TableCell<Customer, Integer>() {
				@Override
				protected void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						// Customize ID cell style and alignment
						setText(item.toString());
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		// Column for Customer Name
		TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setPrefWidth(150);
		nameColumn.setCellFactory(column -> {
			return new TableCell<Customer, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						// Customize Name cell style and alignment
						setText(item);
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		// Column for Phone Number
		TableColumn<Customer, String> phoneColumn = new TableColumn<>("Phone Number");
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		phoneColumn.setPrefWidth(150);
		phoneColumn.setCellFactory(column -> {
			return new TableCell<Customer, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						// Customize Phone Number cell style and alignment
						setText(item);
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		// Column for Payment Method
		TableColumn<Customer, String> paymentColumn = new TableColumn<>("Payment Method");
		paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
		paymentColumn.setPrefWidth(150);
		paymentColumn.setCellFactory(column -> {
			return new TableCell<Customer, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						// Customize Payment Method cell style and alignment
						setText(item);
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		// Column for Dining In (Yes/No)
		TableColumn<Customer, Boolean> diningInColumn = new TableColumn<>("Dining In");
		diningInColumn.setCellValueFactory(new PropertyValueFactory<>("diningIn"));
		diningInColumn.setPrefWidth(150);
		diningInColumn.setCellFactory(column -> {
			return new TableCell<Customer, Boolean>() {
				@Override
				protected void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setStyle("");
					} else {
						// Display "Yes" or "No" based on Boolean value
						setText(item ? "Yes" : "No");
						setStyle("-fx-alignment: CENTER; -fx-text-fill: #283645; -fx-font-weight: bold;");
					}
				}
			};
		});

		// Add all columns to the TableView
		customerTableView.getColumns().addAll(idColumn, nameColumn, phoneColumn, paymentColumn, diningInColumn);
		// Bind data to the TableView
		customerTableView.setItems(customerData);

		// Create a button area with update, delete, and search buttons
		HBox buttonArea = new HBox(10);

		// Update button with an icon
		ImageView buttonImage = new ImageView("file:regeneration.png");
		buttonImage.setFitWidth(20);
		buttonImage.setFitHeight(20);
		Button updateButton = new Button("Update Customer", buttonImage);
		updateButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
		updateButton
				.setOnAction(e -> openCustomerFormForUpdate(customerTableView.getSelectionModel().getSelectedItem()));

		// Delete button with an icon
		ImageView buttondImage = new ImageView("file:delete-user.png");
		buttondImage.setFitWidth(20);
		buttondImage.setFitHeight(20);
		Button deleteButton = new Button("Delete Customer", buttondImage);
		deleteButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
		deleteButton.setOnAction(e -> deleteCustomer(customerTableView.getSelectionModel().getSelectedItem()));

		// Search button with an icon
		ImageView searchImage = new ImageView("file:magnifying-glass-search.png");
		searchImage.setFitWidth(20);
		searchImage.setFitHeight(20);
		Button searchButton = new Button("Search Customer", searchImage);
		searchButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
		searchButton.setOnAction(e -> openSearchDialog());

		// Add buttons to the HBox
		buttonArea.getChildren().addAll(updateButton, deleteButton, searchButton);
		buttonArea.setAlignment(Pos.CENTER);

		// Add TableView and button area to the VBox layout
		customerListViewArea.getChildren().addAll(customerTableView, buttonArea);
		customerListViewArea.setAlignment(Pos.CENTER);

		// Configure TableView dimensions
		customerTableView.setMaxWidth(750);
		customerTableView.setPrefHeight(750);

		// Set the customer list layout at the center of the root layout
		root.setCenter(customerListViewArea);
	}

	private void loadCustomerFromDatabase() throws ClassNotFoundException, SQLException {
		// Establish a connection to the database
		connectDatabase();

		// Define the SQL query to retrieve all records from the "Customer" table
		String query = "SELECT * FROM Customer";

		// Create a statement object to execute the query
		Statement stmt = connection.createStatement();

		// Execute the query and store the result in a ResultSet
		ResultSet rs = stmt.executeQuery(query);

		// Initialize the observable list to hold Customer objects
		customerData = FXCollections.observableArrayList();

		// Iterate through the ResultSet to fetch each record
		while (rs.next()) {
			// Create a new Customer object using the data from the current record
			Customer customer = new Customer(rs.getInt("customerId"), // Retrieve the customer ID (integer)
					rs.getString("name"), // Retrieve the customer's name (string)
					rs.getString("phoneNumber"), // Retrieve the customer's phone number (string)
					rs.getString("paymentMethod"), // Retrieve the payment method (string)
					rs.getBoolean("isDiningIn") // Retrieve the "Dining In" status (boolean)
			);

			// Add the created Customer object to the observable list
			customerData.add(customer);
		}

		// Ensure resources like ResultSet and Statement are closed after use (Best
		// Practice)
		rs.close();
		stmt.close();
	}

	// Update Customer info
	private void openCustomerFormForUpdate(Customer customer) {
		if (customer != null) {
			Dialog<Customer> dialog = new Dialog<>();
			dialog.setTitle("Update Customer");

			// Style the dialog pane
			DialogPane dialogPane = dialog.getDialogPane();
			dialogPane.setStyle("-fx-background-color: #283645; -fx-border-color: #FFFFFF; -fx-border-width: 2px;");

			TextField nameField = new TextField(customer.getName());
			nameField.setPromptText("Customer Name");
			nameField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645; -fx-font-weight: bold;");

			TextField phoneField = new TextField(customer.getPhoneNumber());
			phoneField.setPromptText("Phone Number");
			phoneField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645; -fx-font-weight: bold;");

			// Limit phoneField to 10 digits and allow only numbers
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

			TextField paymentField = new TextField(customer.getPaymentMethod());
			paymentField.setPromptText("Payment Method");
			paymentField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645; -fx-font-weight: bold;");

			CheckBox diningInCheckBox = new CheckBox("Dining In");
			diningInCheckBox.setSelected(customer.isDiningIn());
			diningInCheckBox.setStyle("-fx-text-fill: white;");

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20));
			grid.setStyle("-fx-background-color: #283645;");

			Label nameLabel = new Label("Name:");
			nameLabel.setStyle("-fx-text-fill: white;");
			Label phoneLabel = new Label("Phone Number:");
			phoneLabel.setStyle("-fx-text-fill: white;");
			Label paymentLabel = new Label("Payment Method:");
			paymentLabel.setStyle("-fx-text-fill: white;");
			Label diningInLabel = new Label("Dining In:");
			diningInLabel.setStyle("-fx-text-fill: white;");

			grid.addRow(0, nameLabel, nameField);
			grid.addRow(1, phoneLabel, phoneField);
			grid.addRow(2, paymentLabel, paymentField);
			grid.addRow(3, diningInLabel, diningInCheckBox);

			dialog.getDialogPane().setContent(grid);
			ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

			// Validate phone number before updating
			dialog.getDialogPane().lookupButton(updateButtonType).addEventFilter(ActionEvent.ACTION, event -> {
				if (phoneField.getText().length() != 10) {
					showError("Validation Error", "Phone number must be exactly 10 digits.");
					event.consume(); // Prevent the dialog from closing
				}
			});

			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == updateButtonType) {
					customer.setName(nameField.getText());
					customer.setPhoneNumber(phoneField.getText());
					customer.setPaymentMethod(paymentField.getText());
					customer.setDiningIn(diningInCheckBox.isSelected());
					updateCustomerInDatabase(customer);
					loadCustomerListView();
					return customer;
				}
				return null;
			});

			dialog.showAndWait();
		}
	}

	// update on data base
	private void updateCustomerInDatabase(Customer customer) {
		try {
			connectDatabase();
			String query = "UPDATE Customer SET name = ?, phoneNumber = ?, paymentMethod = ?, isDiningIn = ? WHERE customerId = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, customer.getName());
			stmt.setString(2, customer.getPhoneNumber());
			stmt.setString(3, customer.getPaymentMethod());
			stmt.setBoolean(4, customer.isDiningIn());
			stmt.setInt(5, customer.getCustomerId());
			stmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Delete customer
	private void deleteCustomer(Customer customer) {
		if (customer != null) {
			deleteCustomerFromDatabase(customer);
			loadCustomerListView(); // Reload the customer list
		}
	}

	// delete from data base
	private void deleteCustomerFromDatabase(Customer customer) {
		try {
			connectDatabase();
			String query = "DELETE FROM Customer WHERE customerId = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, customer.getCustomerId());
			stmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// To enter customer info (add new )
	private void openCustomerForm() {
		Dialog<Customer> dialog = new Dialog<>();
		dialog.setTitle("Add New Customer");

		// Style the dialog pane
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.setStyle("-fx-background-color: #283645;");

		// TextField for Customer Name
		TextField nameField = new TextField();
		nameField.setPromptText("Customer Name");
		nameField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645; -fx-font-weight: bold;");

		// TextField for Phone Number
		TextField phoneField = new TextField();
		phoneField.setPromptText("Phone Number");
		phoneField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645; -fx-font-weight: bold;");

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

		// ComboBox for Payment Method
		ComboBox<String> paymentComboBox = new ComboBox<>();
		paymentComboBox.getItems().addAll("Cash", "Credit Card", "Debit Card", "Online Payment");
		paymentComboBox.setPromptText("Select Payment Method");
		paymentComboBox.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645; -fx-font-weight: bold;");

		// CheckBox for Dining In
		CheckBox diningInCheckBox = new CheckBox("Dining In");
		diningInCheckBox.setStyle("-fx-text-fill: white;");

		// GridPane Layout
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20));
		grid.setStyle("-fx-background-color: #283645;");

		Label nameLabel = new Label("Name:");
		nameLabel.setStyle("-fx-text-fill: white;");
		Label phoneLabel = new Label("Phone Number:");
		phoneLabel.setStyle("-fx-text-fill: white;");
		Label paymentLabel = new Label("Payment Method:");
		paymentLabel.setStyle("-fx-text-fill: white;");
		Label diningInLabel = new Label("Dining In:");
		diningInLabel.setStyle("-fx-text-fill: white;");

		grid.addRow(0, nameLabel, nameField);
		grid.addRow(1, phoneLabel, phoneField);
		grid.addRow(2, paymentLabel, paymentComboBox);
		grid.addRow(3, diningInLabel, diningInCheckBox);

		dialogPane.setContent(grid);

		// Buttons for Dialog
		ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		// Handle dialog result
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				return new Customer(0, nameField.getText(), phoneField.getText(), paymentComboBox.getValue(),
						diningInCheckBox.isSelected());
			}
			return null;
		});

		dialog.showAndWait().ifPresent(newCustomer -> {
			insertCustomerIntoDatabase(newCustomer);
			loadCustomerListView();
		});
	}

	// to insert new customer in data base
	public void insertCustomerIntoDatabase(Customer customer) {
		try {
			connectDatabase();
			String query = "INSERT INTO Customer (name, phoneNumber, paymentMethod, isDiningIn) VALUES (?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, customer.getName());
			stmt.setString(2, customer.getPhoneNumber());
			stmt.setString(3, customer.getPaymentMethod());
			stmt.setBoolean(4, customer.isDiningIn());
			stmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// search
	private void openSearchDialog() {
		Dialog<Customer> dialog = new Dialog<>();
		dialog.setTitle("Search Customer");

		// Style the dialog pane
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.setStyle("-fx-background-color: #283645;");

		// Search Fields
		TextField searchField = new TextField();
		searchField.setPromptText("Enter Name ");
		searchField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645; -fx-font-weight: bold;");

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setAlignment(Pos.CENTER);
		content.getChildren().add(searchField);

		dialogPane.setContent(content);

		// Buttons
		ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == searchButtonType) {
				String searchText = searchField.getText();
				searchCustomer(searchText);
			}
			return null;
		});

		dialog.showAndWait();
	}

	// Search from data base
	private void searchCustomer(String searchText) {
		try {
			connectDatabase();
			String query = "SELECT * FROM Customer WHERE name LIKE ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, "%" + searchText + "%");

			ResultSet rs = stmt.executeQuery();

			ObservableList<Customer> matchingCustomers = FXCollections.observableArrayList();

			while (rs.next()) {
				Customer customer = new Customer(rs.getInt("customerId"), rs.getString("name"),
						rs.getString("phoneNumber"), rs.getString("paymentMethod"), rs.getBoolean("isDiningIn"));
				matchingCustomers.add(customer);
			}

			if (matchingCustomers.isEmpty()) {
				showError("Not Found", "No customer matches the search criteria.");
			} else {

				showCustomerSelection(matchingCustomers);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void showCustomerDetails(Customer customer) {
		Stage customerStage = new Stage();
		customerStage.setTitle("Customer Details");

		VBox detailsLayout = new VBox(15);
		detailsLayout.setPadding(new Insets(25));
		detailsLayout.setStyle("-fx-background-color: #F7F9FC; -fx-border-color: #283645; -fx-border-width: 2px;");

		Label headerLabel = new Label("Customer Details");
		headerLabel
				.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #283645; -fx-padding: 0 0 15 0;");

		Label idLabel = new Label("ID: " + customer.getCustomerId());
		idLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #283645;");

		Label nameLabel = new Label("Name: " + customer.getName());
		nameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #283645;");

		Label phoneLabel = new Label("Phone Number: " + customer.getPhoneNumber());
		phoneLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #283645;");

		Label paymentLabel = new Label("Payment Method: " + customer.getPaymentMethod());
		paymentLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #283645;");

		Label diningLabel = new Label("Dining In: " + (customer.isDiningIn() ? "Yes" : "No"));
		diningLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #283645;");

		Button closeButton = new Button("Close");
		closeButton.setStyle(
				"-fx-background-color: #283645; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 5 20 5 20; -fx-border-radius: 5px; -fx-background-radius: 5px;");
		closeButton.setOnAction(e -> customerStage.close());

		detailsLayout.getChildren().addAll(headerLabel, idLabel, nameLabel, phoneLabel, paymentLabel, diningLabel,
				closeButton);
		detailsLayout.setAlignment(Pos.CENTER);

		Scene scene = new Scene(detailsLayout, 350, 400);
		customerStage.setScene(scene);
		customerStage.show();
	}

	private void showCustomerSelection(ObservableList<Customer> customers) {
		Stage selectionStage = new Stage();
		selectionStage.setTitle("Select a Customer");

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		layout.setStyle("-fx-background-color: #283645; -fx-border-color: #FFFFFF; -fx-border-width: 2px;");

		Label headerLabel = new Label("Matching Customers");
		headerLabel
				.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-padding: 0 0 15 0;");

		TableView<Customer> customerTable = new TableView<>();
		customerTable.setItems(customers);
		customerTable.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FFFFFF; -fx-text-fill: #283645;");

		TableColumn<Customer, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		idColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

		TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

		TableColumn<Customer, String> phoneColumn = new TableColumn<>("Phone");
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		phoneColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

		TableColumn<Customer, String> paymentColumn = new TableColumn<>("Payment Method");
		paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
		paymentColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

		TableColumn<Customer, Boolean> diningColumn = new TableColumn<>("Dining In");
		diningColumn.setCellValueFactory(new PropertyValueFactory<>("diningIn"));
		diningColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

		customerTable.getColumns().addAll(idColumn, nameColumn, phoneColumn, paymentColumn, diningColumn);

		Button selectButton = new Button("View Details");
		selectButton.setStyle(
				"-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 5px 15px;");
		selectButton.setOnAction(e -> {
			Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
			if (selectedCustomer != null) {
				showCustomerDetails(selectedCustomer);
				selectionStage.close();
			} else {
				showError("No Selection", "Please select a customer to view details.");
			}
		});

		Button closeButton = new Button("Close");
		closeButton.setStyle(
				"-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 5px 15px;");
		closeButton.setOnAction(e -> selectionStage.close());

		HBox buttonBox = new HBox(20, selectButton, closeButton);
		buttonBox.setAlignment(Pos.CENTER);

		layout.getChildren().addAll(headerLabel, customerTable, buttonBox);
		layout.setAlignment(Pos.CENTER);

		Scene scene = new Scene(layout, 700, 500);
		selectionStage.setScene(scene);
		selectionStage.show();
	}

	private void showCustomerStatistics() {
		Stage statsStage = new Stage();
		statsStage.setTitle("Customer Statistics");

		VBox layout = new VBox(20);
		layout.setPadding(new Insets(20));
		layout.setStyle("-fx-background-color: #283645; -fx-border-color: #FFFFFF; -fx-border-width: 2px;");

		Label headerLabel = new Label("Customer Statistics");
		headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

		try {
			connectDatabase();

			String totalQuery = "SELECT COUNT(*) AS total FROM Customer";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(totalQuery);
			int totalCustomers = rs.next() ? rs.getInt("total") : 0;

			String diningQuery = "SELECT isDiningIn, COUNT(*) AS count FROM Customer GROUP BY isDiningIn";
			rs = stmt.executeQuery(diningQuery);
			int diningInCount = 0, takeoutCount = 0;
			while (rs.next()) {
				if (rs.getBoolean("isDiningIn")) {
					diningInCount = rs.getInt("count");
				} else {
					takeoutCount = rs.getInt("count");
				}
			}

			String paymentQuery = "SELECT paymentMethod, COUNT(*) AS count FROM Customer GROUP BY paymentMethod";
			rs = stmt.executeQuery(paymentQuery);
			ObservableList<PieChart.Data> paymentData = FXCollections.observableArrayList();
			while (rs.next()) {
				paymentData.add(new PieChart.Data(rs.getString("paymentMethod"), rs.getInt("count")));
			}

			// Most order
//			String mostOrdersQuery = "SELECT C.name, SUM(O.quantity) AS totalOrders FROM Orders O JOIN Customer C ON O.customerId = C.customerId  GROUP BY O.customerId ORDER BY totalOrders DESC LIMIT 1";
			
			String mostOrdersQuery = "SELECT C.name, count(O.orderId) AS totalOrders"
					+ " FROM Orders O JOIN Customer C where O.customerId = C.customerId "
					+ " GROUP BY O.customerId ORDER BY" + " totalOrders desc limit 1;";
			rs = stmt.executeQuery(mostOrdersQuery);
			String topCustomerName = "";
			int totalOrders = 0;
			if (rs.next()) {
				topCustomerName = rs.getString("name");
				totalOrders = rs.getInt("totalOrders");
			}
			Label totalLabel = new Label("Total Customers: " + totalCustomers);
			totalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");

			Label diningLabel = new Label(String.format("Dining In: %d | Takeout: %d", diningInCount, takeoutCount));
			diningLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");

			// Add a custom title above the PieChart
			Label chartTitle = new Label("Payment Methods");
			chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

			PieChart paymentChart = new PieChart(paymentData);
			paymentChart.setLegendSide(Side.BOTTOM);

			// Style the legend and labels
			Platform.runLater(() -> {
				Region legendBackground = (Region) paymentChart.lookup(".chart-legend");
				if (legendBackground != null) {
					legendBackground.setStyle("-fx-background-color: #283645; -fx-padding: 10; -fx-border-radius: 5;");
				}

				paymentChart.lookupAll(".chart-legend-item").forEach(node -> {
					if (node instanceof Label) {
						((Label) node).setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px;");
					}
				});

				paymentChart.lookupAll(".chart-pie-label").forEach(node -> {
					if (node instanceof Text) {
						((Text) node).setFill(Color.WHITE);
					}
				});
			});

			Label topCustomerLabel = new Label("Top Customer: " + topCustomerName + " | Total Orders: " + totalOrders);
			topCustomerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");
			layout.getChildren().addAll(headerLabel, totalLabel, topCustomerLabel, diningLabel, chartTitle,
					paymentChart);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			Label errorLabel = new Label("Error retrieving statistics.");
			errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
			layout.getChildren().add(errorLabel);
		}

		Button closeButton = new Button("Close");
		closeButton.setStyle(
				"-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 15;");
		closeButton.setOnAction(e -> statsStage.close());

		layout.getChildren().add(closeButton);
		layout.setAlignment(Pos.CENTER);

		Scene scene = new Scene(layout, 600, 500);
		statsStage.setScene(scene);
		statsStage.show();
	}

	private void showError(String title, String message) {
		Dialog<Void> errorDialog = new Dialog<>();
		errorDialog.setTitle(title);

		// Style the dialog pane
		DialogPane dialogPane = errorDialog.getDialogPane();
		dialogPane.setStyle("-fx-background-color: #283645;");

		// Create the content of the dialog
		Label messageLabel = new Label(message);
		messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

		VBox content = new VBox(messageLabel);
		content.setAlignment(Pos.CENTER);
		content.setPadding(new Insets(20));

		dialogPane.setContent(content);

		// Add OK button
		ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
		dialogPane.getButtonTypes().add(okButtonType);

		// Show the dialog
		errorDialog.showAndWait();
	}

	private void connectDatabase() throws SQLException, ClassNotFoundException {
		Properties p = new Properties();
		p.setProperty("user", dbUsername);
		p.setProperty("password", dbPassword);
		p.setProperty("useSSL", "false");
		p.setProperty("autoReconnect", "true");

		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(dbURL, p);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
