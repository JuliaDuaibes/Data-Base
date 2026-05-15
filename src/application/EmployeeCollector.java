//package application;
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.chart.BarChart;
//import javafx.scene.chart.CategoryAxis;
//import javafx.scene.chart.NumberAxis;
//import javafx.scene.chart.PieChart;
//import javafx.scene.chart.XYChart;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.stage.Screen;
//import javafx.stage.Stage;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import java.sql.*;
//import javafx.geometry.*;
//import javafx.scene.paint.Color;
//import javafx.application.Application;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//public class EmployeeCollector extends Application {
//
//    private TableView<Employee> tableView = new TableView<>();
//    private Label statusLabel = new Label();
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) {
//        // Setup TableView
//        tableView = createEmployeeTableView();
//
//        // Setup buttons
//     
//        // Setup buttons with images
//        Button addButton = createStyledButtonWithImage("Add Employee", "ADD (2).png");
//        Button updateButton = createStyledButtonWithImage("Update Employee", "update.png");
//        Button deleteButton = createStyledButtonWithImage("Delete Employee", "delete.png");
//        Button backToMainButton = createStyledButton("Back to Main");
//        Button next = createStyledButton("next");
//        Button viewRoleStatsButton = createStyledButtonWithImage("View Charts","icons8-seo-graph-66.png");
//        // Setup buttons
//       
//        
//        next.setOnAction(e -> {
//        	MangEmp MG = new MangEmp();
//        	MG.start(stage);
//		});
//        
//        backToMainButton.setOnAction(e -> {
//			RestaurantManagementSystem resurantStage = new RestaurantManagementSystem();
//			resurantStage.start(stage);
//		});
//
//        // Button action
//        viewRoleStatsButton.setOnAction(e -> openEmployeeChartScreen(stage));
//
//        // Search field and button
//        TextField searchField = new TextField();
//        searchField.setPromptText("Search by Name or ID...");
//        
//        Button searchButton = createStyledButtonWithImage("Search", "search (1).png");
//        Button clearSearchButton = createStyledButton("Clear");
//
//        HBox searchBox = new HBox(10, searchField, searchButton, clearSearchButton);
//        searchBox.setPadding(new Insets(10));
//
//        // Button actions
//        addButton.setOnAction(e -> showEmployeeForm(stage, "Add Employee", null));
//        updateButton.setOnAction(e -> {
//            Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
//            if (selectedEmployee != null) {
//                showEmployeeForm(stage, "Update Employee", selectedEmployee);
//            } else {
//                updateStatusMessage("Please select an employee to update.", true);
//            }
//        });
//        deleteButton.setOnAction(e -> {
//            Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
//            if (selectedEmployee != null) {
//                tableView.getItems().remove(selectedEmployee); // Remove from TableView
//                deleteEmployee(selectedEmployee.getEmpNum()); // Delete from database
//                updateStatusMessage("Employee deleted successfully.", false);
//            } else {
//                updateStatusMessage("Please select an employee to delete.", true);
//            }
//        });
//        searchButton.setOnAction(e -> {
//            String searchText = searchField.getText().trim();
//            if (!searchText.isEmpty()) {
//                ObservableList<Employee> results = searchEmployeesByNameOrId(searchText);
//                tableView.setItems(results);
//            } else {
//                updateStatusMessage("Enter a name or ID to search.", true);
//            }
//        });
//        clearSearchButton.setOnAction(e -> {
//            searchField.clear();
//            tableView.setItems(fetchAllEmployees());
//        });
//
//      
//        // Layout
//        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton,viewRoleStatsButton,next);
//        buttonBox.setPadding(new Insets(10));
//        VBox root = new VBox(10, statusLabel, searchBox, buttonBox, tableView, backToMainButton);
//        root.setPadding(new Insets(10));
//       // root.setBackground(new Background(new BackgroundFill(Color.LAVENDER, null, null)));
//        root.setBackground(new Background(new BackgroundFill(Color.web("#283645"), null, null))); // Set background color
//        double screenWidth = Screen.getPrimary().getBounds().getWidth();
//        double screenHeight = Screen.getPrimary().getBounds().getHeight();
//
//        // Scene setup
//        Scene scene = new Scene(root, screenWidth, screenHeight); // Adjusted window size
//
//        
//        // Scene setup
//        //Scene scene = new Scene(root, 800, 600);
//        stage.setScene(scene);
//        stage.setTitle("Employee Manager");
//        stage.show();
//    }
//
//    
//    private void openEmployeeChartScreen(Stage owner) {
//        Stage chartStage = new Stage();
//        chartStage.setTitle("Employee Charts");
//
//        // ObservableLists to hold data for PieChart and BarChart
//        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
//        ObservableList<XYChart.Data<String, Number>> barChartData = FXCollections.observableArrayList();
//
//        try {
//            // Establish the database connection using DatabaseConnection
//            Connection connection = DatabaseConnection.getConnection();
//
//            if (connection != null) {
//                // Query to get the count of employees by branch
//                String query = "SELECT branchId, COUNT(*) AS count FROM Employee GROUP BY branchId";
//                java.sql.Statement statement = connection.createStatement();
//                ResultSet resultSet = statement.executeQuery(query);
//
//                // Process the result set and add data to PieChart and BarChart
//                while (resultSet.next()) {
//                    int branchId = resultSet.getInt("branchId");
//                    int count = resultSet.getInt("count");
//
//                    // Add data to PieChart
//                    pieChartData.add(new PieChart.Data("Branch " + branchId, count));
//
//                    // Add data to BarChart
//                    barChartData.add(new XYChart.Data<>("Branch " + branchId, count));
//                }
//
//                // Close the connection
//                connection.close();
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Handle the exception appropriately (e.g., show an error message to the user)
//        }
//
//        // Create PieChart
//        PieChart pieChart = new PieChart(pieChartData);
//        pieChart.setTitle("Employees by Branch");
//
//        // Create BarChart
//        CategoryAxis xAxis = new CategoryAxis();
//        xAxis.setLabel("Branch ID");
//        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("Employee Count");
//        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
//        barChart.setTitle("Employees Breakdown by Branch");
//
//        // Create and populate the BarChart Series
//        XYChart.Series<String, Number> series = new XYChart.Series<>();
//        series.setName("Employee Count");
//        series.getData().addAll(barChartData);
//        barChart.getData().add(series);
//
//        // Layout for charts
//        VBox chartLayout = new VBox(20, pieChart, barChart);
//        chartLayout.setPadding(new Insets(20));
//        chartLayout.setAlignment(Pos.CENTER);
//
//        // Set up the Scene and Stage for displaying the charts
//        Scene chartScene = new Scene(chartLayout, 600, 400);
//        chartStage.setScene(chartScene);
//        chartStage.initOwner(owner);
//        chartStage.show();
//        chartStage.setFullScreen(true);
//    }
//
//	private TableView<Employee> createEmployeeTableView() {
//        TableView<Employee> table = new TableView<>();
//        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
//        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
//        TableColumn<Employee, Integer> branchIdColumn = new TableColumn<>("Branch ID");
//
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("empNum"));
//        idColumn.setPrefWidth(100);
//
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("empName"));
//        nameColumn.setPrefWidth(200);
//
//        branchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
//        branchIdColumn.setPrefWidth(150);
//
//        table.getColumns().addAll(idColumn, nameColumn, branchIdColumn);
//        table.setItems(fetchAllEmployees());
//        return table;
//    }
//
//    private void showEmployeeForm(Stage owner, String title, Employee employee) {
//        Stage formStage = new Stage();
//        formStage.setTitle(title);
//
//        TextField nameField = new TextField();
//        TextField branchIdField = new TextField();
//
//        if (employee != null) {
//            nameField.setText(employee.getEmpName());
//            branchIdField.setText(String.valueOf(employee.getBranchId()));
//        }
//
//        GridPane form = new GridPane();
//        form.setPadding(new Insets(10));
//        form.setHgap(10);
//        form.setVgap(10);
//        form.add(new Label("Name:"), 0, 0);
//        form.add(nameField, 1, 0);
//        form.add(new Label("Branch ID:"), 0, 1);
//        form.add(branchIdField, 1, 1);
//
//        Button saveButton = new Button("Save");
//        Button cancelButton = new Button("Cancel");
//        HBox buttonBox = new HBox(10, saveButton, cancelButton);
//        form.add(buttonBox, 1, 2);
//
//        saveButton.setOnAction(e -> {
//            String name = nameField.getText();
//            String branchIdText = branchIdField.getText();
//
//            if (name.isEmpty() || branchIdText.isEmpty()) {
//                updateStatusMessage("Fields can't be empty.", true);
//                return;
//            }
//
//            try {
//                int branchId = Integer.parseInt(branchIdText);
//                if (employee == null) {
//                    addEmployee(name, branchId);
//                } else {
//                    updateEmployee(employee.getEmpNum(), name, branchId);
//                }
//                tableView.setItems(fetchAllEmployees());
//                formStage.close();
//            } catch (NumberFormatException ex) {
//                updateStatusMessage("Branch ID must be a number.", true);
//            }
//        });
//
//        cancelButton.setOnAction(e -> formStage.close());
//
//        Scene formScene = new Scene(form, 400, 200);
//        formStage.setScene(formScene);
//        formStage.initOwner(owner);
//        formStage.show();
//    }
//
//    private void addEmployee(String name, int branchId) {
//        String query = "INSERT INTO Employee (empName, branchId) VALUES (?, ?)";
//        try (Connection conn =  DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, name);
//            stmt.setInt(2, branchId);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error adding employee: " + e.getMessage(), true);
//        }
//    }
//
//    private void updateEmployee(int empNum, String name, int branchId) {
//        String query = "UPDATE Employee SET empName = ?, branchId = ? WHERE empNum = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, name);
//            stmt.setInt(2, branchId);
//            stmt.setInt(3, empNum);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error updating employee: " + e.getMessage(), true);
//        }
//    }
//
//    private void deleteEmployee(int empNum) {
//        String query = "DELETE FROM Employee WHERE empNum = ?";
//        try (Connection conn =  DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, empNum);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error deleting employee: " + e.getMessage(), true);
//        }
//    }
//
//    private ObservableList<Employee> fetchAllEmployees() {
//        ObservableList<Employee> employees = FXCollections.observableArrayList();
//        String query = "SELECT * FROM Employee";
//        try (Connection conn = DatabaseConnection.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                employees.add(new Employee(
//                    rs.getInt("empNum"),
//                    rs.getString("empName"),
//                    rs.getInt("branchId")
//                ));
//            }
//        } catch (SQLException e) {
//            updateStatusMessage("Error fetching employees: " + e.getMessage(), true);
//        }
//        return employees;
//    }
//
//    private ObservableList<Employee> searchEmployeesByNameOrId(String searchText) {
//        ObservableList<Employee> employees = FXCollections.observableArrayList();
//        String query = "SELECT * FROM Employee WHERE empName LIKE ? OR empNum = ?";
//        try (Connection conn =  DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, "%" + searchText + "%");
//            stmt.setInt(2, searchText.matches("\\d+") ? Integer.parseInt(searchText) : 0);
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                employees.add(new Employee(
//                    rs.getInt("empNum"),
//                    rs.getString("empName"),
//                    rs.getInt("branchId")
//                ));
//            }
//        } catch (SQLException e) {
//            updateStatusMessage("Error searching employees: " + e.getMessage(), true);
//        }
//        return employees;
//    }
//
//    private void updateStatusMessage(String message, boolean isError) {
//        statusLabel.setText(message);
//        statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
//    }
//
//    private Button createStyledButton(String text) {
//        Button button = new Button(text);
//        button.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
//        return button;
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
//}
