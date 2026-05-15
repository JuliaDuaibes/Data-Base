//package application;
//
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.stage.Stage;
//
//import java.sql.*;
//import java.util.ArrayList;
//
//public class RestaurantMonthlySalary extends Application {
//
//	private BorderPane root;
//	private Connection connection;
//	private ListView<MonthlySalary> salaryListView;
//	private ArrayList<MonthlySalary> salaries = new ArrayList<>();
//
//	@Override
//	public void start(Stage primaryStage) {
//		salaryListView = new ListView<>();
//		salaryListView.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black;");
//		salaryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue != null) {
//				showSalaryDetails(newValue);
//			}
//		});
//
//		root = new BorderPane();
//
//		// Top toolbar
//		HBox topToolbar = new HBox(10);
//		topToolbar.setPadding(new Insets(10));
//		topToolbar.setStyle("-fx-background-color: #283645;");
//
//		Button addButton = new Button("+ New");
//		addButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
//		addButton.setOnAction(e -> addNewSalary());
//
//		Button deleteButton = new Button("Delete");
//		deleteButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-font-size: 14px;");
//		deleteButton.setOnAction(e -> {
//			MonthlySalary selectedSalary = salaryListView.getSelectionModel().getSelectedItem();
//			if (selectedSalary != null) {
//				deleteSalary(selectedSalary);
//			} else {
//				showAlert("No Selection", "Please select a salary record to delete.");
//			}
//		});
//
//		Button editButton = new Button("Edit");
//		editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
//		editButton.setOnAction(e -> {
//			MonthlySalary selectedSalary = salaryListView.getSelectionModel().getSelectedItem();
//			if (selectedSalary != null) {
//				editSalary(selectedSalary);
//			} else {
//				showAlert("No Selection", "Please select a salary record to edit.");
//			}
//		});
//
//		topToolbar.getChildren().addAll(addButton, editButton, deleteButton);
//		root.setTop(topToolbar);
//
//		// Left sidebar
//		VBox salarySidebar = new VBox(10);
//		salarySidebar.setPadding(new Insets(10));
//		salarySidebar.setStyle("-fx-background-color: #283645;");
//		Label salaryLabel = new Label("Salaries");
//		salaryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
//		salarySidebar.getChildren().addAll(salaryLabel, salaryListView);
//		root.setLeft(salarySidebar);
//
//		loadSalariesFromDatabase();
//		showSalaryDetails(null);
//
//		Scene scene = new Scene(root, 1530, 800);
//		primaryStage.setScene(scene);
//		primaryStage.setTitle("Monthly Salaries Management");
//		primaryStage.show();
//	}
//
//	private void showSalaryDetails(MonthlySalary salary) {
//		VBox detailsBox = new VBox(10);
//		detailsBox.setPadding(new Insets(20));
//		detailsBox.setStyle("-fx-background-color: #FFFFFF;");
//		detailsBox.setAlignment(Pos.CENTER);
//
//		if (salary != null) {
//			Label branchLabel = new Label("Branch ID: " + salary.getBranchID());
//			Label monthLabel = new Label("Month: " + salary.getMonth());
//			Label totalSalaryLabel = new Label("Total Salary: " + salary.getTotalSalary());
//			Label paymentDateLabel = new Label("Payment Date: " + salary.getPaymentDate());
//			Label notesLabel = new Label("Notes: " + salary.getNotes());
//
//			detailsBox.getChildren().addAll(branchLabel, monthLabel, totalSalaryLabel, paymentDateLabel, notesLabel);
//		} else {
//			detailsBox.getChildren().add(new Label("Select a salary record to view details."));
//		}
//
//		root.setCenter(detailsBox);
//	}
//
//	private void addNewSalary() {
//		Dialog<MonthlySalary> dialog = new Dialog<>();
//		dialog.setTitle("Add New Salary");
//
//		TextField branchIdField = new TextField();
//		branchIdField.setPromptText("Branch ID");
//
//		// ComboBox for months
//		ComboBox<String> monthComboBox = new ComboBox<>();
//		monthComboBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August",
//				"September", "October", "November", "December");
//		monthComboBox.setPromptText("Select Month");
//
//		TextField totalSalaryField = new TextField();
//		totalSalaryField.setPromptText("Total Salary");
//
//		DatePicker paymentDatePicker = new DatePicker();
//
//		TextField notesField = new TextField();
//		notesField.setPromptText("Notes");
//
//		GridPane grid = new GridPane();
//		grid.setHgap(10);
//		grid.setVgap(10);
//		grid.setPadding(new Insets(20));
//		grid.addRow(0, new Label("Branch ID:"), branchIdField);
//		grid.addRow(1, new Label("Month:"), monthComboBox);
//		grid.addRow(2, new Label("Total Salary:"), totalSalaryField);
//		grid.addRow(3, new Label("Payment Date:"), paymentDatePicker);
//		grid.addRow(4, new Label("Notes:"), notesField);
//
//		dialog.getDialogPane().setContent(grid);
//		ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
//		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
//
//		dialog.setResultConverter(dialogButton -> {
//			if (dialogButton == addButtonType) {
//				try {
//					int branchId = Integer.parseInt(branchIdField.getText().trim());
//					String month = monthComboBox.getValue();
//					double totalSalary = Double.parseDouble(totalSalaryField.getText().trim());
//					String paymentDate = (paymentDatePicker.getValue() != null)
//							? paymentDatePicker.getValue().toString()
//							: null;
//
//					if (month == null || paymentDate == null) {
//						showAlert("Validation Error", "All fields are required.");
//						return null;
//					}
//
//					return new MonthlySalary(branchId, month, totalSalary, paymentDate, notesField.getText().trim());
//				} catch (NumberFormatException e) {
//					showAlert("Validation Error", "Branch ID and Total Salary must be numeric.");
//				}
//			}
//			return null;
//		});
//
//		dialog.showAndWait().ifPresent(newSalary -> {
//			insertSalaryIntoDatabase(newSalary);
//			loadSalariesFromDatabase();
//		});
//	}
//
////    private void insertSalaryIntoDatabase(MonthlySalary salary) {
////        try {
////            connectDatabase();
////            String query = "INSERT INTO MonthlySalary (BranchID, Month, SalaryAmount, PaymentDate, Notes) VALUES (?, ?, ?, ?, ?)";
////            PreparedStatement stmt = connection.prepareStatement(query);
////            stmt.setInt(1, salary.getBranchID());
////            stmt.setString(2, salary.getMonth());
////            stmt.setDouble(3, salary.getTotalSalary());
////            stmt.setString(4, salary.getPaymentDate());
////            stmt.setString(5, salary.getNotes());
////            stmt.executeUpdate();
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
//
//	private void insertSalaryIntoDatabase(MonthlySalary salary) {
//		try {
//			connectDatabase();
//			String query = "INSERT INTO MonthlySalary (BranchID, Month, SalaryAmount, PaymentDate, Notes) VALUES (?, ?, ?, ?, ?)";
//			PreparedStatement stmt = connection.prepareStatement(query);
//			stmt.setInt(1, salary.getBranchID());
//			stmt.setString(2, salary.getMonth());
//			stmt.setDouble(3, salary.getTotalSalary());
//			stmt.setString(4, salary.getPaymentDate());
//			stmt.setString(5, salary.getNotes());
//			stmt.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void loadSalariesFromDatabase() {
//		salaries.clear();
//		salaryListView.getItems().clear();
//		try {
//			connectDatabase();
//			String query = "SELECT * FROM MonthlySalary";
//			Statement stmt = connection.createStatement();
//			ResultSet rs = stmt.executeQuery(query);
//
//			while (rs.next()) {
//				MonthlySalary salary = new MonthlySalary(rs.getInt("BranchID"), rs.getString("Month"),
//						rs.getDouble("SalaryAmount"), rs.getString("PaymentDate"), rs.getString("Notes"));
//				salaries.add(salary);
//				salaryListView.getItems().add(salary);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void showAlert(String title, String message) {
//		Alert alert = new Alert(Alert.AlertType.INFORMATION);
//		alert.setTitle(title);
//		alert.setHeaderText(null);
//		alert.setContentText(message);
//		alert.showAndWait();
//	}
//
//	public static void main(String[] args) {
//		launch(args);
//	}
//
//	private void deleteSalary(MonthlySalary salary) {
//		try {
//			connectDatabase();
//			String query = "DELETE FROM MonthlySalary WHERE BranchID = ? AND Month = ?";
//			PreparedStatement stmt = connection.prepareStatement(query);
//			stmt.setInt(1, salary.getBranchID());
//			stmt.setString(2, salary.getMonth());
//			int rowsAffected = stmt.executeUpdate();
//			if (rowsAffected > 0) {
//				showAlert("Success", "Salary record deleted successfully.");
//				loadSalariesFromDatabase();
//			} else {
//				showAlert("Error", "Failed to delete the salary record.");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//	private void editSalary(MonthlySalary salary) {
//	    // Store the original month before editing
//	    String originalMonth = salary.getMonth();
//
//	    Dialog<MonthlySalary> dialog = new Dialog<>();
//	    dialog.setTitle("Edit Salary");
//
//	    // Branch ID (non-editable)
//	    TextField branchIdField = new TextField(String.valueOf(salary.getBranchID()));
//	    branchIdField.setPromptText("Branch ID");
//	    branchIdField.setDisable(true);
//
//	    // Month (editable)
//	    ComboBox<String> monthComboBox = new ComboBox<>();
//	    monthComboBox.getItems().addAll(
//	            "January", "February", "March", "April", "May", "June",
//	            "July", "August", "September", "October", "November", "December"
//	    );
//	    monthComboBox.setValue(salary.getMonth());
//
//	    // Total Salary (editable)
//	    TextField totalSalaryField = new TextField(String.valueOf(salary.getTotalSalary()));
//	    totalSalaryField.setPromptText("Total Salary");
//
//	    // Payment Date (editable)
//	    DatePicker paymentDatePicker = new DatePicker();
//	    paymentDatePicker.setValue(java.time.LocalDate.parse(salary.getPaymentDate()));
//
//	    // Notes (editable)
//	    TextField notesField = new TextField(salary.getNotes());
//	    notesField.setPromptText("Notes");
//
//	    // Layout for the input fields
//	    GridPane grid = new GridPane();
//	    grid.setHgap(10);
//	    grid.setVgap(10);
//	    grid.setPadding(new Insets(20));
//	    grid.addRow(0, new Label("Branch ID:"), branchIdField);
//	    grid.addRow(1, new Label("Month:"), monthComboBox);
//	    grid.addRow(2, new Label("Total Salary:"), totalSalaryField);
//	    grid.addRow(3, new Label("Payment Date:"), paymentDatePicker);
//	    grid.addRow(4, new Label("Notes:"), notesField);
//
//	    dialog.getDialogPane().setContent(grid);
//	    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
//	    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
//
//	    // Validate and update the salary object upon saving
//	    dialog.setResultConverter(dialogButton -> {
//	        if (dialogButton == saveButtonType) {
//	            try {
//	                String newMonth = monthComboBox.getValue();
//	                double newTotalSalary = Double.parseDouble(totalSalaryField.getText().trim());
//	                String newPaymentDate = (paymentDatePicker.getValue() != null)
//	                        ? paymentDatePicker.getValue().toString()
//	                        : null;
//	                String newNotes = notesField.getText().trim();
//
//	                if (newMonth == null || newPaymentDate == null) {
//	                    showAlert("Validation Error", "All fields are required.");
//	                    return null;
//	                }
//
//	                // Update the salary object with the new values
//	                salary.setMonth(newMonth);
//	                salary.setTotalSalary(newTotalSalary);
//	                salary.setPaymentDate(newPaymentDate);
//	                salary.setNotes(newNotes);
//
//	                return salary;
//	            } catch (NumberFormatException e) {
//	                showAlert("Validation Error", "Total Salary must be numeric.");
//	            }
//	        }
//	        return null;
//	    });
//
//	    // Handle the updated salary object
//	    dialog.showAndWait().ifPresent(updatedSalary -> {
//	        // Call the method to update the database, passing the original month
//	        updateSalaryInDatabase(updatedSalary, originalMonth);
//	        // Reload the list to reflect changes
//	        loadSalariesFromDatabase();
//	    });
//	}
//
////	private void editSalary(MonthlySalary salary) {
////		Dialog<MonthlySalary> dialog = new Dialog<>();
////		dialog.setTitle("Edit Salary");
////
////		TextField branchIdField = new TextField(String.valueOf(salary.getBranchID()));
////		branchIdField.setPromptText("Branch ID");
////		branchIdField.setDisable(true); // Prevent editing Branch ID
////
////		ComboBox<String> monthComboBox = new ComboBox<>();
////		monthComboBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August",
////				"September", "October", "November", "December");
////		monthComboBox.setValue(salary.getMonth());
////
////		TextField totalSalaryField = new TextField(String.valueOf(salary.getTotalSalary()));
////		totalSalaryField.setPromptText("Total Salary");
////
////		DatePicker paymentDatePicker = new DatePicker();
////		paymentDatePicker.setValue(java.time.LocalDate.parse(salary.getPaymentDate()));
////
////		TextField notesField = new TextField(salary.getNotes());
////		notesField.setPromptText("Notes");
////
////		GridPane grid = new GridPane();
////		grid.setHgap(10);
////		grid.setVgap(10);
////		grid.setPadding(new Insets(20));
////		grid.addRow(0, new Label("Branch ID:"), branchIdField);
////		grid.addRow(1, new Label("Month:"), monthComboBox);
////		grid.addRow(2, new Label("Total Salary:"), totalSalaryField);
////		grid.addRow(3, new Label("Payment Date:"), paymentDatePicker);
////		grid.addRow(4, new Label("Notes:"), notesField);
////
////		dialog.getDialogPane().setContent(grid);
////		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
////		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
////
////		dialog.setResultConverter(dialogButton -> {
////			if (dialogButton == saveButtonType) {
////				try {
////					String newMonth = monthComboBox.getValue();
////					double newTotalSalary = Double.parseDouble(totalSalaryField.getText().trim());
////					String newPaymentDate = (paymentDatePicker.getValue() != null)
////							? paymentDatePicker.getValue().toString()
////							: null;
////					String newNotes = notesField.getText().trim();
////
////					if (newMonth == null || newPaymentDate == null) {
////						showAlert("Validation Error", "All fields are required.");
////						return null;
////					}
////
//////                    salary.setMonth(newMonth);
//////                    salary.setTotalSalary(newTotalSalary);
//////                    salary.setPaymentDate(newPaymentDate);
//////                    salary.setNotes(newNotes);
////					return salary;
////				} catch (NumberFormatException e) {
////					showAlert("Validation Error", "Total Salary must be numeric.");
////				}
////			}
////			return null;
////		});
////
////		dialog.showAndWait().ifPresent(updatedSalary -> {
////            updateSalaryInDatabase(updatedSalary);
////
////			loadSalariesFromDatabase();
////		});
////	}
////    private void updateSalaryInDatabase(MonthlySalary salary) {
////        try {
////            connectDatabase();
////            String query = "UPDATE MonthlySalary SET Month = ?, SalaryAmount = ?, PaymentDate = ?, Notes = ? WHERE BranchID = ? AND Month = ?";
////            PreparedStatement stmt = connection.prepareStatement(query);
////            stmt.setString(1, salary.getMonth());
////            stmt.setDouble(2, salary.getTotalSalary());
////            stmt.setString(3, salary.getPaymentDate());
////            stmt.setString(4, salary.getNotes());
////            stmt.setInt(5, salary.getBranchID());
////            stmt.setString(6, salary.getMonth()); // Original Month
////            int rowsAffected = stmt.executeUpdate();
////            if (rowsAffected > 0) {
////                showAlert("Success", "Salary record updated successfully.");
////            } else {
////                showAlert("Error", "Failed to update the salary record.");
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
//
//	private void updateSalaryInDatabase(MonthlySalary salary, String originalMonth) {
//		try {
//			connectDatabase();
//			String query = "UPDATE MonthlySalary SET Month = ?, SalaryAmount = ?, PaymentDate = ?, Notes = ? WHERE BranchID = ? AND Month = ?";
//			PreparedStatement stmt = connection.prepareStatement(query);
//
//			// Update values
//			stmt.setString(1, salary.getMonth()); // New Month
//			stmt.setDouble(2, salary.getTotalSalary()); // New Salary Amount
//			stmt.setString(3, salary.getPaymentDate()); // New Payment Date
//			stmt.setString(4, salary.getNotes()); // New Notes
//
//			// Conditions
//			stmt.setInt(5, salary.getBranchID()); // Branch ID
//			stmt.setString(6, originalMonth); // Original Month
//
//			int rowsAffected = stmt.executeUpdate();
//			if (rowsAffected > 0) {
//				showAlert("Success", "Salary record updated successfully.");
//			} else {
//				showAlert("Error", "Failed to update the salary record.");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			showAlert("Database Error", "An error occurred while updating the record: " + e.getMessage());
//		}
//	}
//
//	private void connectDatabase() {
//		try {
//			String url = "jdbc:mysql://127.0.0.1:3306/FinalPhase?useSSL=false";
//			String username = "root";
//			String password = "root1234";
//			connection = DriverManager.getConnection(url, username, password);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//}
package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class RestaurantMonthlySalary extends Application {

    private BorderPane root;
    private Connection connection;
    private ListView<MonthlySalary> salaryListView;
    private ArrayList<MonthlySalary> salaries = new ArrayList<>();



    @Override
    public void start(Stage primaryStage) {
        salaryListView = new ListView<>();
        salaryListView.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black;");
        salaryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showSalaryDetails(newValue);
            }
        });

        root = new BorderPane();

        // Top toolbar
        HBox topToolbar = new HBox(10);
        topToolbar.setPadding(new Insets(10));
        topToolbar.setStyle("-fx-background-color: #283645;");

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Enter Branch ID or Month");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        // Search button
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        searchButton.setOnAction(e -> searchSalaries(searchField.getText()));

        // Add New button
        Button addButton = new Button("+ New");
        addButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");
        addButton.setOnAction(e -> addNewSalary());

        // Edit button
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        editButton.setOnAction(e -> {
            MonthlySalary selectedSalary = salaryListView.getSelectionModel().getSelectedItem();
            if (selectedSalary != null) {
                editSalary(selectedSalary);
            } else {
                showAlert("No Selection", "Please select a salary record to edit.");
            }
        });

        // Delete button
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteButton.setOnAction(e -> {
            MonthlySalary selectedSalary = salaryListView.getSelectionModel().getSelectedItem();
            if (selectedSalary != null) {
                deleteSalary(selectedSalary);
            } else {
                showAlert("No Selection", "Please select a salary record to delete.");
            }
        });
        Button totalSalaryButton = new Button("Total Salary");
        totalSalaryButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-font-size: 14px;");
        totalSalaryButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Branch Selection");
            dialog.setHeaderText("Enter Branch ID");
            dialog.setContentText("Please enter the Branch ID:");

            dialog.showAndWait().ifPresent(input -> {
                try {
                    int branchId = Integer.parseInt(input.trim());
                    showTotalSalaryForBranch(branchId);
                } catch (NumberFormatException ex) {
                    showAlert("Invalid Input", "Please enter a valid Branch ID.");
                }
            });
        });

        topToolbar.getChildren().add(totalSalaryButton);


        // Add all buttons to the top toolbar
        topToolbar.getChildren().addAll(searchField, searchButton, addButton, editButton, deleteButton);
        root.setTop(topToolbar);

        // Bottom toolbar
        HBox bottomToolbar = new HBox(10);
        bottomToolbar.setPadding(new Insets(10));
        bottomToolbar.setAlignment(Pos.CENTER);
        bottomToolbar.setStyle("-fx-background-color: #F5F5F5;");

        // Highest Total Salary button
        Button highestButton = new Button("Highest Total Salary");
        highestButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        highestButton.setOnAction(e -> showHighestTotalSalary());

        // Lowest Total Salary button
        Button lowestButton = new Button("Lowest Total Salary");
        lowestButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-font-size: 14px;");
        lowestButton.setOnAction(e -> showLowestTotalSalary());

        // Show Statistics button
        Button statsButton = new Button("Show Statistics");
        statsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        statsButton.setOnAction(e -> loadStatisticsView());

        // Add buttons to the bottom toolbar
        bottomToolbar.getChildren().addAll(highestButton, lowestButton, statsButton);
        root.setBottom(bottomToolbar);

        // Left sidebar
        VBox salarySidebar = new VBox(10);
        salarySidebar.setPadding(new Insets(10));
        salarySidebar.setStyle("-fx-background-color: #283645;");
        Label salaryLabel = new Label("Salaries");
        salaryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        salarySidebar.getChildren().addAll(salaryLabel, salaryListView);
        root.setLeft(salarySidebar);

        loadSalariesFromDatabase();
        showSalaryDetails(null);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Monthly Salaries Management");
        primaryStage.show();
    }

    private void showSalaryDetails(MonthlySalary salary) {
        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setStyle("-fx-background-color: #FFFFFF;");
        detailsBox.setAlignment(Pos.CENTER);

        if (salary != null) {
            Label branchLabel = new Label("Branch ID: " + salary.getBranchID());
            Label monthLabel = new Label("Month: " + salary.getMonth());
            Label totalSalaryLabel = new Label("Total Salary: " + salary.getTotalSalary());
            Label paymentDateLabel = new Label("Payment Date: " + salary.getPaymentDate());
            Label notesLabel = new Label("Notes: " + salary.getNotes());

            detailsBox.getChildren().addAll(branchLabel, monthLabel, totalSalaryLabel, paymentDateLabel, notesLabel);
        } else {
            detailsBox.getChildren().add(new Label("Select a salary record to view details."));
        }

        root.setCenter(detailsBox);
    }

    private void addNewSalary() {
        Dialog<MonthlySalary> dialog = new Dialog<>();
        dialog.setTitle("Add New Salary");

        TextField branchIdField = new TextField();
        branchIdField.setPromptText("Branch ID");

        // ComboBox for months
        ComboBox<String> monthComboBox = new ComboBox<>();
        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
        monthComboBox.setPromptText("Select Month");

        TextField totalSalaryField = new TextField();
        totalSalaryField.setPromptText("Total Salary");

        DatePicker paymentDatePicker = new DatePicker();

        TextField notesField = new TextField();
        notesField.setPromptText("Notes");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Branch ID:"), branchIdField);
        grid.addRow(1, new Label("Month:"), monthComboBox);
        grid.addRow(2, new Label("Total Salary:"), totalSalaryField);
        grid.addRow(3, new Label("Payment Date:"), paymentDatePicker);
        grid.addRow(4, new Label("Notes:"), notesField);

        dialog.getDialogPane().setContent(grid);
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int branchId = Integer.parseInt(branchIdField.getText().trim());
                    String month = monthComboBox.getValue();
                    double totalSalary = Double.parseDouble(totalSalaryField.getText().trim());
                    String paymentDate = (paymentDatePicker.getValue() != null) ? paymentDatePicker.getValue().toString() : null;

                    if (month == null || paymentDate == null) {
                        showAlert("Validation Error", "All fields are required.");
                        return null;
                    }

                    return new MonthlySalary(branchId, month, totalSalary, paymentDate, notesField.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert("Validation Error", "Branch ID and Total Salary must be numeric.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newSalary -> {
            insertSalaryIntoDatabase(newSalary);
            loadSalariesFromDatabase();
        });
    }


    
    
    
    
    
    private void insertSalaryIntoDatabase(MonthlySalary salary) {
        try {
            connectDatabase();
            String query = "INSERT INTO MonthlySalary (BranchID, Month, SalaryAmount, PaymentDate, Notes) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, salary.getBranchID());
            stmt.setString(2, salary.getMonth());
            stmt.setDouble(3, salary.getTotalSalary());
            stmt.setString(4, salary.getPaymentDate());
            stmt.setString(5, salary.getNotes());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadSalariesFromDatabase() {
        salaries.clear();
        salaryListView.getItems().clear();
        try {
            connectDatabase();
            String query = "SELECT * FROM MonthlySalary";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                MonthlySalary salary = new MonthlySalary(
                        rs.getInt("BranchID"),
                        rs.getString("Month"),
                        rs.getDouble("SalaryAmount"),
                        rs.getString("PaymentDate"),
                        rs.getString("Notes")
                );
                salaries.add(salary);
                salaryListView.getItems().add(salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
    
    
    
    
    


private void updateSalaryInDatabase(MonthlySalary salary, String originalMonth) {
		try {
			connectDatabase();
			String query = "UPDATE MonthlySalary SET Month = ?, SalaryAmount = ?, PaymentDate = ?, Notes = ? WHERE BranchID = ? AND Month = ?";
			PreparedStatement stmt = connection.prepareStatement(query);

			// Update values
			stmt.setString(1, salary.getMonth()); // New Month
			stmt.setDouble(2, salary.getTotalSalary()); // New Salary Amount
			stmt.setString(3, salary.getPaymentDate()); // New Payment Date
			stmt.setString(4, salary.getNotes()); // New Notes

			// Conditions
			stmt.setInt(5, salary.getBranchID()); // Branch ID
			stmt.setString(6, originalMonth); // Original Month

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				showAlert("Success", "Salary record updated successfully.");
			} else {
				showAlert("Error", "Failed to update the salary record.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Database Error", "An error occurred while updating the record: " + e.getMessage());
		}
	}

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void deleteSalary(MonthlySalary salary) {
        try {
            connectDatabase();
            String query = "DELETE FROM MonthlySalary WHERE BranchID = ? AND Month = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, salary.getBranchID());
            stmt.setString(2, salary.getMonth());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Salary record deleted successfully.");
                loadSalariesFromDatabase();
            } else {
                showAlert("Error", "Failed to delete the salary record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void editSalary(MonthlySalary salary) {
        Dialog<MonthlySalary> dialog = new Dialog<>();
        dialog.setTitle("Edit Salary");

        TextField branchIdField = new TextField(String.valueOf(salary.getBranchID()));
        branchIdField.setPromptText("Branch ID");
        branchIdField.setDisable(true); // Prevent editing Branch ID

        ComboBox<String> monthComboBox = new ComboBox<>();
        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
        monthComboBox.setValue(salary.getMonth());

        TextField totalSalaryField = new TextField(String.valueOf(salary.getTotalSalary()));
        totalSalaryField.setPromptText("Total Salary");

        DatePicker paymentDatePicker = new DatePicker();
        paymentDatePicker.setValue(java.time.LocalDate.parse(salary.getPaymentDate()));

        TextField notesField = new TextField(salary.getNotes());
        notesField.setPromptText("Notes");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Branch ID:"), branchIdField);
        grid.addRow(1, new Label("Month:"), monthComboBox);
        grid.addRow(2, new Label("Total Salary:"), totalSalaryField);
        grid.addRow(3, new Label("Payment Date:"), paymentDatePicker);
        grid.addRow(4, new Label("Notes:"), notesField);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String newMonth = monthComboBox.getValue();
                    double newTotalSalary = Double.parseDouble(totalSalaryField.getText().trim());
                    String newPaymentDate = (paymentDatePicker.getValue() != null) ? paymentDatePicker.getValue().toString() : null;
                    String newNotes = notesField.getText().trim();

                    if (newMonth == null || newPaymentDate == null) {
                        showAlert("Validation Error", "All fields are required.");
                        return null;
                    }

                    salary.setMonth(newMonth);
                    salary.setTotalSalary(newTotalSalary);
                    salary.setPaymentDate(newPaymentDate);
                    salary.setNotes(newNotes);
                    return salary;
                } catch (NumberFormatException e) {
                    showAlert("Validation Error", "Total Salary must be numeric.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedSalary -> {
            updateSalaryInDatabase(updatedSalary);
            loadSalariesFromDatabase();
        });
    }
    private void updateSalaryInDatabase(MonthlySalary salary) {
        try {
            connectDatabase();
            String query = "UPDATE MonthlySalary SET Month = ?, SalaryAmount = ?, PaymentDate = ?, Notes = ? WHERE BranchID = ? AND Month = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, salary.getMonth());
            stmt.setDouble(2, salary.getTotalSalary());
            stmt.setString(3, salary.getPaymentDate());
            stmt.setString(4, salary.getNotes());
            stmt.setInt(5, salary.getBranchID());
            stmt.setString(6, salary.getMonth()); // Original Month
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Salary record updated successfully.");
            } else {
                showAlert("Error", "Failed to update the salary record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showHighestTotalSalary() {
        try {
            connectDatabase();
            String query = """
                SELECT BranchID, SUM(SalaryAmount) AS TotalSalary
                FROM MonthlySalary
                GROUP BY BranchID
                ORDER BY TotalSalary DESC
                LIMIT 1
            """;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                int branchID = rs.getInt("BranchID");
                double totalSalary = rs.getDouble("TotalSalary");
                showAlert("Highest Total Salary", "Branch ID: " + branchID + "\nTotal Salary: " + totalSalary);
            } else {
                showAlert("No Data", "No salary data found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to retrieve highest total salary: " + e.getMessage());
        }
    }

    
    private void showLowestTotalSalary() {
        try {
            connectDatabase();
            String query = """
                SELECT BranchID, SUM(SalaryAmount) AS TotalSalary
                FROM MonthlySalary
                GROUP BY BranchID
                ORDER BY TotalSalary ASC
                LIMIT 1
            """;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                int branchID = rs.getInt("BranchID");
                double totalSalary = rs.getDouble("TotalSalary");
                showAlert("Lowest Total Salary", "Branch ID: " + branchID + "\nTotal Salary: " + totalSalary);
            } else {
                showAlert("No Data", "No salary data found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to retrieve lowest total salary: " + e.getMessage());
        }
    }

    private void loadStatisticsView() {
        VBox statsViewArea = new VBox(10);
        statsViewArea.setPadding(new Insets(20));
        statsViewArea.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645;");

        // Pie Chart: توزيع الرواتب حسب الفرع
        PieChart salaryChart = new PieChart();
        salaryChart.setTitle("Salary Distribution by Branch");

        try {
            String query = "SELECT BranchID, SUM(SalaryAmount) AS TotalSalary FROM MonthlySalary GROUP BY BranchID";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String branchID = "Branch " + rs.getInt("BranchID");
                double totalSalary = rs.getDouble("TotalSalary");
                salaryChart.getData().add(new PieChart.Data(branchID, totalSalary));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bar Chart: أعلى الرواتب لكل فرع
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Branch ID");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Highest Salary");

        BarChart<String, Number> highestSalaryChart = new BarChart<>(xAxis, yAxis);
        highestSalaryChart.setTitle("Highest Salary by Branch");

        try {
            String query = """
                SELECT BranchID, MAX(SalaryAmount) AS HighestSalary
                FROM MonthlySalary
                GROUP BY BranchID
            """;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
            while (rs.next()) {
                String branchID = "Branch " + rs.getInt("BranchID");
                double highestSalary = rs.getDouble("HighestSalary");
                dataSeries.getData().add(new XYChart.Data<>(branchID, highestSalary));
            }
            highestSalaryChart.getData().add(dataSeries);
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        // Add charts to the view
        statsViewArea.getChildren().addAll(salaryChart, highestSalaryChart);
        root.setCenter(statsViewArea); // Replace center content
    }
    
    private void searchSalaries(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadSalariesFromDatabase(); // إذا كان الحقل فارغًا، عرض جميع الرواتب
            return;
        }

        salaries.clear();
        salaryListView.getItems().clear();

        try {
            connectDatabase();
            String query = """
                SELECT * FROM MonthlySalary 
                WHERE BranchID LIKE ? OR Month LIKE ?
            """;
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, "%" + searchText.trim() + "%");
            stmt.setString(2, "%" + searchText.trim() + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MonthlySalary salary = new MonthlySalary(
                        rs.getInt("BranchID"),
                        rs.getString("Month"),
                        rs.getDouble("SalaryAmount"),
                        rs.getString("PaymentDate"),
                        rs.getString("Notes")
                );
                salaries.add(salary);
                salaryListView.getItems().add(salary);
            }

            if (salaries.isEmpty()) {
                showAlert("No Results", "No records found matching your search.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to execute search: " + e.getMessage());
        }
    }

    
    private void loadBranchTotalSalaries() {
        salaryListView.getItems().clear();

        try {
            connectDatabase();
            String query = """
                SELECT BranchID, SUM(MonthlySalary) AS TotalSalary
                FROM EmployeeSalaries
                GROUP BY BranchID
            """;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int branchID = rs.getInt("BranchID");
                double totalSalary = rs.getDouble("TotalSalary");

                // إنشاء كائن لتمثيل الفرع ومجموع الرواتب
                MonthlySalary salary = new MonthlySalary(branchID, "N/A", totalSalary, "Calculated", "N/A");
                salaryListView.getItems().add(salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load branch salaries: " + e.getMessage());
        }
    }

//    private void loadStatisticsView() {
//        VBox statsViewArea = new VBox(10);
//        statsViewArea.setPadding(new Insets(20));
//        statsViewArea.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #283645;");
//
//        // Pie Chart: توزيع الرواتب حسب أعلى مجموع لكل فرع
//        PieChart salaryChart = new PieChart();
//        salaryChart.setTitle("Highest Total Salaries by Branch");
//
//        try {
//            connectDatabase();
//            String query = """
//                SELECT BranchID, SUM(SalaryAmount) AS TotalSalary
//                FROM MonthlySalary
//                GROUP BY BranchID
//                ORDER BY TotalSalary DESC
//            """;
//            Statement stmt = connection.createStatement();
//            ResultSet rs = stmt.executeQuery(query);
//
//            while (rs.next()) {
//                String branchID = "Branch " + rs.getInt("BranchID");
//                double totalSalary = rs.getDouble("TotalSalary");
//                salaryChart.getData().add(new PieChart.Data(branchID, totalSalary));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert("Error", "Failed to load statistics: " + e.getMessage());
//        }
//
//        statsViewArea.getChildren().add(salaryChart);
//        root.setCenter(statsViewArea); // عرض المخطط في المركز
//    }
    private double getTotalSalaryForBranch(int branchId) {
        double totalSalary = 0.0;
        try {
            connectDatabase();
            String query = "SELECT SUM(SalaryAmount) AS TotalSalary FROM MonthlySalary WHERE BranchID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalSalary = rs.getDouble("TotalSalary");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to retrieve total salary: " + e.getMessage());
        }
        return totalSalary;
    }

    private void showTotalSalaryForBranch(int branchId) {
        double totalSalary = getTotalSalaryForBranch(branchId);
        showAlert("Total Salary for Branch " + branchId, "Total Salary: " + totalSalary);
    }

    private void connectDatabase() {
		try {
			String url = "jdbc:mysql://127.0.0.1:3306/FinalPhase?useSSL=false";
			String username = "root";
			String password = "root1234";
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
