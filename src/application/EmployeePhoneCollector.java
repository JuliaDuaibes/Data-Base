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
//public class EmployeePhoneCollector extends Application {
//
//    private ComboBox<Integer> empNumComboBox;
//    private TextField phoneField;
//    private TableView<EmployeePhone> phoneTableView;
//    private ObservableList<EmployeePhone> phones;
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
//        phoneField = new TextField();
//        phoneField.setPromptText("Enter Phone Number");
//
//        // Create TableView to display Employee Phones
//        phoneTableView = new TableView<>();
//        phones = FXCollections.observableArrayList();
//        phoneTableView.setItems(phones);
//
//        phoneTableView.setStyle(
//                "-fx-background-color: #52425c;"
//                        + "-fx-control-inner-background: #aa98b5;"
//                        + "-fx-control-inner-background-alt: #cbc1d2;"
//                        + "-fx-table-cell-border-color: #52425c;"
//                        + "-fx-table-header-border-color: #52425c;"
//                        + "-fx-text-fill: white;"
//        );
//
//        // Employee Number column
//        TableColumn<EmployeePhone, Integer> empNumColumn = new TableColumn<>("Employee Number");
//        empNumColumn.setCellValueFactory(new PropertyValueFactory<>("empNum"));
//        empNumColumn.setPrefWidth(100);
//
//        // Phone Number column
//        TableColumn<EmployeePhone, String> phoneColumn = new TableColumn<>("Phone Number");
//        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
//
//        phoneTableView.getColumns().addAll(empNumColumn, phoneColumn);
//
//        
//        
//        
//        Button addButton = createStyledButtonWithImage("Add Employee", "ADD (2).png");
//        Button updateButton = createStyledButtonWithImage("Update Employee", "update.png");
//        Button deleteButton = createStyledButtonWithImage("Delete Employee", "delete.png");
//        Button backToMainButton = createStyledButton("Back");
//
//        addButton.setOnAction(e -> addPhone());
//        updateButton.setOnAction(e -> updatePhone());
//        deleteButton.setOnAction(e -> deletePhone());
//       // Button   backToMainButton = new Button ("Back");
//        
//        backToMainButton.setOnAction(e -> {
//        	EmployeeCollector resurantStage = new EmployeeCollector();
//			resurantStage.start(stage);
//		});
//
//
//        // Layout for buttons
//        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton , backToMainButton);
//        buttonBox.setPadding(new Insets(10));
//
//        // Layout for form
//        GridPane form = new GridPane();
//        form.setPadding(new Insets(10));
//        form.setHgap(10);
//        form.setVgap(10);
//
//       
//        
//        
//        Label empNumLabel = new Label("Employee Number:");
//        empNumLabel.setStyle("-fx-text-fill: white;");
//        form.add(empNumLabel, 0, 0);
//        form.add(empNumComboBox, 1, 0);
//
//        Label phoneLabel = new Label("Phone Number:");
//        phoneLabel.setStyle("-fx-text-fill: white;");
//        form.add(phoneLabel, 0, 1);
//        form.add(phoneField, 1, 1);
//        empNumComboBox.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
//        // Main layout
//        VBox root = new VBox(10, form, buttonBox, new Label("Search by Employee Number:"), searchEmpNumField, phoneTableView);
//        root.setPadding(new Insets(10));
//        root.setBackground(new Background(new BackgroundFill(Color.web("#283645"), null, null))); // Set background color
//     // Get screen dimensions
//        double screenWidth = Screen.getPrimary().getBounds().getWidth();
//        double screenHeight = Screen.getPrimary().getBounds().getHeight();
//
//        // Scene setup
//        Scene scene = new Scene(root, screenWidth, screenHeight); // Adjusted window size
//        // Scene setup
//        //Scene scene = new Scene(root, 500, 400);
//        stage.setTitle("Employee Phone Collector");
//        stage.setScene(scene);
//        stage.show();
//
//        // Add listeners
//        empNumComboBox.setOnAction(e -> loadPhones());
//        searchEmpNumField.textProperty().addListener((observable, oldValue, newValue) -> searchEmployeePhones(newValue));
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
//    private void loadPhones() {
//        Integer selectedEmpNum = empNumComboBox.getValue();
//        String empNumInput = searchEmpNumField.getText();
//
//        if (selectedEmpNum != null) {
//            ObservableList<EmployeePhone> newPhones = fetchEmployeePhones(selectedEmpNum);
//            phones.setAll(newPhones);
//        } else if (!empNumInput.isEmpty()) {
//            try {
//                int empNum = Integer.parseInt(empNumInput);
//                ObservableList<EmployeePhone> filteredPhones = fetchEmployeePhones(empNum);
//                phones.setAll(filteredPhones);
//            } catch (NumberFormatException e) {
//                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid Employee Number.");
//            }
//        }
//    }
//
//    private void searchEmployeePhones(String empNumInput) {
//        if (empNumInput.isEmpty()) {
//            phones.clear();
//            return;
//        }
//
//        try {
//            int empNum = Integer.parseInt(empNumInput);
//            ObservableList<EmployeePhone> filteredPhones = fetchEmployeePhones(empNum);
//            phones.setAll(filteredPhones);
//        } catch (NumberFormatException e) {
//            phones.clear();
//            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid Employee Number.");
//        }
//    }
//
//    private ObservableList<EmployeePhone> fetchEmployeePhones(int empNum) {
//        ObservableList<EmployeePhone> phones = FXCollections.observableArrayList();
//        String query = "SELECT * FROM EmployeePhones WHERE empNum = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, empNum);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            while (resultSet.next()) {
//                int employeeNumFromDb = resultSet.getInt("empNum");
//                String phone = resultSet.getString("phone");
//                phones.add(new EmployeePhone(employeeNumFromDb, phone));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return phones;
//    }
//
// 
//    
//    
//    private void addPhone() {
//        Integer selectedEmpNum = empNumComboBox.getValue();
//        String phone = phoneField.getText();
//
//        if (selectedEmpNum != null && !phone.isEmpty()) {
//            if (phone.length() != 10) { // التحقق من طول الرقم
//                showAlert(Alert.AlertType.WARNING, "Invalid Phone Number", "Phone number must be exactly 10 digits.");
//                return; // الخروج إذا كان الرقم غير صالح
//            }
//            if (phoneExists(selectedEmpNum, phone)) {
//                showAlert(Alert.AlertType.WARNING, "Phone Exists", "This phone number already exists for this employee.");
//            } else {
//                if (insertPhone(selectedEmpNum, phone)) {
//                    loadPhones();
//                    phoneField.clear();
//                    showAlert(Alert.AlertType.INFORMATION, "Success", "Phone added successfully.");
//                } else {
//                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add phone.");
//                }
//            }
//        } else {
//            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please select an employee and enter a phone number.");
//        }
//    }
//
//
//    private boolean phoneExists(int empNum, String phone) {
//        String query = "SELECT COUNT(*) FROM EmployeePhones WHERE empNum = ? AND phone = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, empNum);
//            preparedStatement.setString(2, phone);
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
//    private boolean insertPhone(int empNum, String phone) {
//        String query = "INSERT INTO EmployeePhones (empNum, phone) VALUES (?, ?)";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, empNum);
//            preparedStatement.setString(2, phone);
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private void updatePhone() {
//        EmployeePhone selectedPhone = phoneTableView.getSelectionModel().getSelectedItem();
//
//        if (selectedPhone != null) {
//            String updatedPhone = phoneField.getText();
//
//            if (!updatedPhone.isEmpty()) {
//                if (updatePhoneInDatabase(selectedPhone.getPhone(), updatedPhone)) {
//                    loadPhones();
//                    phoneField.clear();
//                    showAlert(Alert.AlertType.INFORMATION, "Success", "Phone updated successfully.");
//                } else {
//                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update phone.");
//                }
//            } else {
//                showAlert(Alert.AlertType.WARNING, "Missing Data", "Please enter a phone number.");
//            }
//        } else {
//            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a phone to update.");
//        }
//    }
//
//    private boolean updatePhoneInDatabase(String oldPhone, String updatedPhone) {
//        String query = "UPDATE EmployeePhones SET phone = ? WHERE phone = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setString(1, updatedPhone);
//            preparedStatement.setString(2, oldPhone);
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private void deletePhone() {
//        EmployeePhone selectedPhone = phoneTableView.getSelectionModel().getSelectedItem();
//
//        if (selectedPhone != null) {
//            if (deletePhoneFromDatabase(selectedPhone.getPhone())) {
//                loadPhones();
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Phone deleted successfully.");
//            } else {
//                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete phone.");
//            }
//        } else {
//            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a phone to delete.");
//        }
//    }
//
//    private boolean deletePhoneFromDatabase(String phone) {
//        String query = "DELETE FROM EmployeePhones WHERE phone = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setString(1, phone);
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
