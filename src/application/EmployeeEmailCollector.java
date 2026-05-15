//package application;
//
//import javafx.application.Application;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.stage.Screen;
//import javafx.stage.Stage;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class EmployeeEmailCollector extends Application {
//
//    private ComboBox<Integer> empNumComboBox;
//    private TextField emailField;
//    private TableView<EmployeeEmail> emailTableView;
//    private ObservableList<EmployeeEmail> emails;
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    private TextField searchEmpNumField;
//
//    @Override
//    public void start(Stage stage) {
//        // Set up UI elements
//        empNumComboBox = new ComboBox<>();
//        empNumComboBox.setPromptText("Select Employee Number");
//
//        searchEmpNumField = new TextField();
//        searchEmpNumField.setPromptText("Search by Employee Number");
//
//        // Fetch employee numbers from the database and populate ComboBox
//        empNumComboBox.setItems(fetchEmployeeNums());
//
//        emailField = new TextField();
//        emailField.setPromptText("Enter Email Address");
//
//        // Create TableView to display Employee Emails
//        emailTableView = new TableView<>();
//        emails = FXCollections.observableArrayList();
//        emailTableView.setItems(emails);
//
//        emailTableView.setStyle(
//                "-fx-background-color: #52425c;"
//                        + "-fx-control-inner-background: #aa98b5;"
//                        + "-fx-control-inner-background-alt: #cbc1d2;"
//                        + "-fx-table-cell-border-color: #52425c;"
//                        + "-fx-table-header-border-color: #52425c;"
//                        + "-fx-text-fill: white;"
//        );
//
//        // Employee Number column
//        TableColumn<EmployeeEmail, Integer> empNumColumn = new TableColumn<>("Employee Number");
//        empNumColumn.setCellValueFactory(new PropertyValueFactory<>("empNum"));
//        empNumColumn.setPrefWidth(100);
//
//        // Email Address column
//        TableColumn<EmployeeEmail, String> emailColumn = new TableColumn<>("Email Address");
//        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
//
//        emailTableView.getColumns().addAll(empNumColumn, emailColumn);
//
//       
//        
//        
//        Button addButton = createStyledButtonWithImage("Add Employee", "ADD (2).png");
//        Button updateButton = createStyledButtonWithImage("Update Employee", "update.png");
//        Button deleteButton = createStyledButtonWithImage("Delete Employee", "delete.png");
//        Button backToMainButton = createStyledButton("Back");
//        
//        backToMainButton.setOnAction(e -> {
//        	EmployeeCollector resurantStage = new EmployeeCollector();
//			resurantStage.start(stage);
//		});
//
//
//
//        addButton.setOnAction(e -> addEmail());
//        updateButton.setOnAction(e -> updateEmail());
//        deleteButton.setOnAction(e -> deleteEmail());
//
//        // Layout for buttons
//        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton,backToMainButton);
//        buttonBox.setPadding(new Insets(10));
//
//        // Layout for form
//        GridPane form = new GridPane();
//        form.setPadding(new Insets(10));
//        form.setHgap(10);
//        form.setVgap(10);
//
//      
//        Label empNumLabel = new Label("Employee Number:");
//        empNumLabel.setStyle("-fx-text-fill: white;");
//        form.add(empNumLabel, 0, 0);
//        form.add(empNumComboBox, 1, 0);
//
//        Label phoneLabel = new Label("Email Address::");
//        phoneLabel.setStyle("-fx-text-fill: white;");
//        form.add(phoneLabel, 0, 1);
//        form.add(emailField, 1, 1);
//        empNumComboBox.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
//
//        // Main layout
//        VBox root = new VBox(10, form, buttonBox, new Label("Search by Employee Number:"), searchEmpNumField, emailTableView);
//        root.setPadding(new Insets(10));
//        root.setBackground(new Background(new BackgroundFill(Color.web("#283645"), null, null))); // Set background color
//        // Get screen dimensions
//           double screenWidth = Screen.getPrimary().getBounds().getWidth();
//           double screenHeight = Screen.getPrimary().getBounds().getHeight();
//
//           // Scene setup
//           Scene scene = new Scene(root, screenWidth, screenHeight); // Adjusted window size
//
//        // Scene setup
//       // Scene scene = new Scene(root, 500, 400);
//        stage.setTitle("Employee Email Collector");
//        stage.setScene(scene);
//        stage.show();
//
//        // Add listeners
//        empNumComboBox.setOnAction(e -> loadEmails());
//        searchEmpNumField.textProperty().addListener((observable, oldValue, newValue) -> searchEmployeeEmails(newValue));
//    }
//
//    private ObservableList<Integer> fetchEmployeeNums() {
//        ObservableList<Integer> empNums = FXCollections.observableArrayList();
//        String query = "SELECT empNum FROM Employee";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query);
//             ResultSet resultSet = preparedStatement.executeQuery()) {
//
//            while (resultSet.next()) {
//                int empNum = resultSet.getInt("empNum");
//                empNums.add(empNum);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return empNums;
//    }
//
//    private void loadEmails() {
//        Integer selectedEmpNum = empNumComboBox.getValue();
//        String empNumInput = searchEmpNumField.getText();
//
//        // If an employee number is selected in the combo box
//        if (selectedEmpNum != null) {
//            ObservableList<EmployeeEmail> newEmails = fetchEmployeeEmails(selectedEmpNum);
//            emails.setAll(newEmails);
//        }
//        // If there's input in the search field, search emails based on that
//        else if (!empNumInput.isEmpty()) {
//            try {
//                int empNum = Integer.parseInt(empNumInput);
//                ObservableList<EmployeeEmail> filteredEmails = fetchEmployeeEmails(empNum);
//                emails.setAll(filteredEmails);
//            } catch (NumberFormatException e) {
//                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid Employee Number.");
//            }
//        }
//    }
//
//    private void searchEmployeeEmails(String empNumInput) {
//        if (empNumInput.isEmpty()) {
//            emails.clear();
//            return;
//        }
//
//        try {
//            int empNum = Integer.parseInt(empNumInput);
//            ObservableList<EmployeeEmail> filteredEmails = fetchEmployeeEmails(empNum);
//            emails.setAll(filteredEmails);
//        } catch (NumberFormatException e) {
//            emails.clear();
//            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid Employee Number.");
//        }
//    }
//
//    private ObservableList<EmployeeEmail> fetchEmployeeEmails(int empNum) {
//        ObservableList<EmployeeEmail> emails = FXCollections.observableArrayList();
//        String query = "SELECT * FROM EmployeeEmails WHERE empNum = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, empNum);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            while (resultSet.next()) {
//                int employeeNumFromDb = resultSet.getInt("empNum");
//                String email = resultSet.getString("email");
//                emails.add(new EmployeeEmail(employeeNumFromDb, email));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return emails;
//    }
//
//    private void addEmail() {
//        Integer selectedEmpNum = empNumComboBox.getValue();
//        String email = emailField.getText();
//
//        if (selectedEmpNum != null && !email.isEmpty()) {
//            if (emailExists(selectedEmpNum, email)) {
//                showAlert(Alert.AlertType.WARNING, "Email Exists", "This email already exists for this employee.");
//            } else {
//                if (insertEmail(selectedEmpNum, email)) {
//                    loadEmails();
//                    emailField.clear();
//                    showAlert(Alert.AlertType.INFORMATION, "Success", "Email added successfully.");
//                } else {
//                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add email.");
//                }
//            }
//        } else {
//            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please select an employee and enter an email.");
//        }
//    }
//
//    private boolean emailExists(int empNum, String email) {
//        String query = "SELECT COUNT(*) FROM EmployeeEmails WHERE empNum = ? AND email = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, empNum);
//            preparedStatement.setString(2, email);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            if (resultSet.next()) {
//                return resultSet.getInt(1) > 0;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//    private boolean insertEmail(int empNum, String email) {
//        String query = "INSERT INTO EmployeeEmails (empNum, email) VALUES (?, ?)";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, empNum);
//            preparedStatement.setString(2, email);
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private void updateEmail() {
//        EmployeeEmail selectedEmail = emailTableView.getSelectionModel().getSelectedItem();
//
//        if (selectedEmail != null) {
//            String updatedEmail = emailField.getText();
//
//            if (!updatedEmail.isEmpty()) {
//                if (updateEmailInDatabase(selectedEmail.getEmail(), updatedEmail)) {
//                    loadEmails();
//                    emailField.clear();
//                    showAlert(Alert.AlertType.INFORMATION, "Success", "Email updated successfully.");
//                } else {
//                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update email.");
//                }
//            } else {
//                showAlert(Alert.AlertType.WARNING, "Missing Data", "Please enter an email.");
//            }
//        } else {
//            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an email to update.");
//        }
//    }
//
//    private boolean updateEmailInDatabase(String oldEmail, String updatedEmail) {
//        String query = "UPDATE EmployeeEmails SET email = ? WHERE email = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setString(1, updatedEmail);
//            preparedStatement.setString(2, oldEmail);
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private void deleteEmail() {
//        EmployeeEmail selectedEmail = emailTableView.getSelectionModel().getSelectedItem();
//
//        if (selectedEmail != null) {
//            if (deleteEmailFromDatabase(selectedEmail.getEmail())) {
//                loadEmails();
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Email deleted successfully.");
//            } else {
//                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete email.");
//            }
//        } else {
//            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an email to delete.");
//        }
//    }
//
//    private boolean deleteEmailFromDatabase(String email) {
//        String query = "DELETE FROM EmployeeEmails WHERE email = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setString(1, email);
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private void showAlert(Alert.AlertType alertType, String title, String message) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//    
//    private Button createStyledButtonWithImage(String text, String imagePath) {
//        // Create an ImageView with the specified image path
//        Image image = new Image(getClass().getResourceAsStream(imagePath));
//        ImageView imageView = new ImageView(image);
//        imageView.setFitHeight(20); // Adjust image size
//        imageView.setFitWidth(20);
//
//        // Create the button and add the image to it
//        Button button = new Button(text);
//        button.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
//        button.setGraphic(imageView); // Set the image on the button
//
//        return button;
//    }
//    
//    private Button createStyledButton(String text) {
//        Button button = new Button(text);
//        button.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
//        return button;
//    }
//    
//    private ImageView createImageView(String imagePath, double width, double height) {
//        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
//        imageView.setFitWidth(width);
//        imageView.setFitHeight(height);
//        return imageView;
//    }
//}
