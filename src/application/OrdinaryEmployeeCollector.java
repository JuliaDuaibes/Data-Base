//package application;
//import javafx.beans.value.ObservableValue;
//import javafx.util.StringConverter;
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
//import java.util.ArrayList;
//
//import java.sql.*;
//import javafx.geometry.*;
//import javafx.scene.paint.Color;
//import javafx.application.Application;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.stage.Stage;
//
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//
//public class OrdinaryEmployeeCollector extends Application {
//
//    private TableView<OrdinaryEmployee> tableView = new TableView<>();
//    private Label statusLabel = new Label();
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) {
//        tableView = createOrdinaryEmployeeTableView();
//
//        Button addButton = createStyledButtonWithImage("Add Employee", "ADD (2).png");
//        Button updateButton = createStyledButtonWithImage("Update Employee", "update.png");
//        Button deleteButton = createStyledButtonWithImage("Delete Employee", "delete.png");
//        Button backToMainButton = createStyledButton("Back to Main");
//        Button viewRoleStatsButton = createStyledButtonWithImage("View Role Distribution","icons8-seo-graph-66.png");
//        viewRoleStatsButton.setOnAction(e -> openEmployeeChartScreen(stage));
//        
//        backToMainButton.setOnAction(e -> {
//			RestaurantManagementSystem resurantStage = new RestaurantManagementSystem();
//			resurantStage.start(stage);
//		});
//
//        TextField searchField = new TextField();
//        searchField.setPromptText("Search by Name or ID...");
//        Button searchButton = createStyledButtonWithImage("Search", "search (1).png");
//        Button clearSearchButton = createStyledButton("Clear");
//
//        HBox searchBox = new HBox(10, searchField, searchButton, clearSearchButton);
//        searchBox.setPadding(new Insets(10));
//
//        addButton.setOnAction(e -> showEmployeeForm(stage, "Add Employee", null));
//        updateButton.setOnAction(e -> {
//            OrdinaryEmployee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
//            if (selectedEmployee != null) {
//                showEmployeeForm(stage, "Update Employee", selectedEmployee);
//            } else {
//                updateStatusMessage("Please select an employee to update.", true);
//            }
//        });
//        deleteButton.setOnAction(e -> {
//            OrdinaryEmployee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
//            if (selectedEmployee != null) {
//                tableView.getItems().remove(selectedEmployee);
//                deleteEmployee(selectedEmployee.getEmpNum());
//                updateStatusMessage("Employee deleted successfully.", false);
//            } else {
//                updateStatusMessage("Please select an employee to delete.", true);
//            }
//        });
//        searchButton.setOnAction(e -> {
//            String searchText = searchField.getText().trim();
//            if (!searchText.isEmpty()) {
//                ObservableList<OrdinaryEmployee> results = searchEmployeesById(searchText);
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
//        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, viewRoleStatsButton);
//        buttonBox.setPadding(new Insets(10));
//        VBox root = new VBox(10, statusLabel, searchBox, buttonBox, tableView, backToMainButton);
//        root.setPadding(new Insets(10));
//       // root.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
//
//        root.setBackground(new Background(new BackgroundFill(Color.web("#283645"), null, null)));
//        
//        double screenWidth = Screen.getPrimary().getBounds().getWidth();
//        double screenHeight = Screen.getPrimary().getBounds().getHeight();
//
//        // Scene setup
//        Scene scene = new Scene(root, screenWidth, screenHeight); // Adjusted window size
//
//       // Scene scene = new Scene(root, 800, 600);
//        stage.setScene(scene);
//        stage.setTitle("Ordinary Employee Manager");
//        stage.show();
//    }
//
//    private TableView<OrdinaryEmployee> createOrdinaryEmployeeTableView() {
//        TableView<OrdinaryEmployee> table = new TableView<>();
//        TableColumn<OrdinaryEmployee, Integer> idColumn = new TableColumn<>("ID");
//        TableColumn<OrdinaryEmployee, String> roleColumn = new TableColumn<>("Role");
//        TableColumn<OrdinaryEmployee, Double> rateColumn = new TableColumn<>("Hourly Rate");
//        TableColumn<OrdinaryEmployee, Date> hireDateColumn = new TableColumn<>("Hire Date");
//        TableColumn<OrdinaryEmployee, Integer> workDaysColumn = new TableColumn<>("Work Days");
//        TableColumn<OrdinaryEmployee, Boolean> isActiveColumn = new TableColumn<>("Is Active");
//
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("empNum"));
//        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
//        rateColumn.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
//        hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
//        workDaysColumn.setCellValueFactory(new PropertyValueFactory<>("workDays"));
//        isActiveColumn.setCellValueFactory(cellData -> cellData.getValue().isActiveProperty());
//
//        table.getColumns().addAll(idColumn, roleColumn, rateColumn, hireDateColumn, workDaysColumn, isActiveColumn);
//        table.setItems(fetchAllEmployees());
//        return table;
//    }
//
//    private void showEmployeeForm(Stage owner, String title, OrdinaryEmployee employee) {
//        Stage formStage = new Stage();
//        formStage.setTitle(title);
//
//        TextField empNumField = new TextField();
//        ComboBox<String> roleComboBox = new ComboBox<>();
//        roleComboBox.getItems().addAll(
//            "Head Chef", "Sous Chef", "Waiter", "Dishwasher", "Delivery Driver"
//        ); // Add the specific roles
//        roleComboBox.setValue("Head Chef");  // Set a default value, you can choose another one based on your preference
// // Default role
//        TextField rateField = new TextField();
//        DatePicker hireDatePicker = new DatePicker(); // DatePicker for hire date
//        TextField workDaysField = new TextField();
//        CheckBox isActiveCheckbox = new CheckBox("Active");
//
//        if (employee != null) {
//            empNumField.setText(String.valueOf(employee.getEmpNum()));
//            roleComboBox.setValue(employee.getRole());
//            rateField.setText(String.valueOf(employee.getHourlyRate()));
//            hireDatePicker.setValue(employee.getHireDate().toLocalDate());
//            workDaysField.setText(String.valueOf(employee.getWorkDays()));
//            isActiveCheckbox.setSelected(employee.isActive());
//        }
//
//        GridPane form = new GridPane();
//        form.setPadding(new Insets(10));
//        form.setHgap(10);
//        form.setVgap(10);
//        form.add(new Label("Employee Number:"), 0, 0);
//        form.add(empNumField, 1, 0);
//        form.add(new Label("Role:"), 0, 1);
//        form.add(roleComboBox, 1, 1);
//        form.add(new Label("Hourly Rate:"), 0, 2);
//        form.add(rateField, 1, 2);
//        form.add(new Label("Hire Date:"), 0, 3);
//        form.add(hireDatePicker, 1, 3);
//        form.add(new Label("Work Days:"), 0, 4);
//        form.add(workDaysField, 1, 4);
//        form.add(isActiveCheckbox, 1, 5);
//
//        Button saveButton = new Button("Save");
//        Button cancelButton = new Button("Cancel");
//        HBox buttonBox = new HBox(10, saveButton, cancelButton);
//        form.add(buttonBox, 1, 6);
//
//        saveButton.setOnAction(e -> {
//            try {
//                int empNum = Integer.parseInt(empNumField.getText());
//                String role = roleComboBox.getValue(); // Get the selected role from the ComboBox
//                double rate = Double.parseDouble(rateField.getText());
//                Date hireDate = Date.valueOf(hireDatePicker.getValue()); // Convert LocalDate to Date
//                int workDays = Integer.parseInt(workDaysField.getText());
//                boolean isActive = isActiveCheckbox.isSelected();
//
//                if (employee == null) {
//                    addEmployee(empNum, role, rate, hireDate, workDays, isActive);
//                } else {
//                    updateEmployee(empNum, role, rate, hireDate, workDays, isActive);
//                }
//                tableView.setItems(fetchAllEmployees());
//                formStage.close();
//            } catch (NumberFormatException ex) {
//                updateStatusMessage("Ensure all fields are valid.", true);
//            }
//        });
//
//        cancelButton.setOnAction(e -> formStage.close());
//
//        Scene formScene = new Scene(form, 400, 300);
//        formStage.setScene(formScene);
//        formStage.initOwner(owner);
//        formStage.show();
//    }
//
//    private void addEmployee(int empNum, String role, double rate, Date hireDate, int workDays, boolean isActive) {
//        String query = "INSERT INTO OrdinaryEmployee (empNum, role, hourlyRate, hireDate, workDays, isActive) VALUES (?, ?, ?, ?, ?, ?)";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, empNum);
//            stmt.setString(2, role);
//            stmt.setDouble(3, rate);
//            stmt.setDate(4, hireDate);
//            stmt.setInt(5, workDays);
//            stmt.setBoolean(6, isActive);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error adding employee: " + e.getMessage(), true);
//        }
//    }
//
//    private void updateEmployee(int empNum, String role, double rate, Date hireDate, int workDays, boolean isActive) {
//        String query = "UPDATE OrdinaryEmployee SET role = ?, hourlyRate = ?, hireDate = ?, workDays = ?, isActive = ? WHERE empNum = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, role);
//            stmt.setDouble(2, rate);
//            stmt.setDate(3, hireDate);
//            stmt.setInt(4, workDays);
//            stmt.setBoolean(5, isActive);
//            stmt.setInt(6, empNum);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error updating employee: " + e.getMessage(), true);
//        }
//    }
//
//    private ObservableList<OrdinaryEmployee> fetchAllEmployees() {
//        ObservableList<OrdinaryEmployee> employees = FXCollections.observableArrayList();
//        String query = "SELECT * FROM OrdinaryEmployee";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                employees.add(new OrdinaryEmployee(
//                        rs.getInt("empNum"),
//                        rs.getString("role"),
//                        rs.getDouble("hourlyRate"),
//                        rs.getDate("hireDate"),
//                        rs.getInt("workDays"),
//                        rs.getBoolean("isActive")));
//            }
//        } catch (SQLException e) {
//            updateStatusMessage("Error fetching employees: " + e.getMessage(), true);
//        }
//        return employees;
//    }
//
//    private void updateStatusMessage(String message, boolean isError) {
//        statusLabel.setText(message);
//        statusLabel.setTextFill(isError ? Color.RED : Color.GREEN);
//    }
//
//    private ObservableList<OrdinaryEmployee> searchEmployeesById(String searchText) {
//        ObservableList<OrdinaryEmployee> results = FXCollections.observableArrayList();
//        String query = "SELECT * FROM OrdinaryEmployee WHERE empNum LIKE ? OR empName LIKE ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, "%" + searchText + "%");
//            stmt.setString(2, "%" + searchText + "%");
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                results.add(new OrdinaryEmployee(
//                        rs.getInt("empNum"),
//                        rs.getString("role"),
//                        rs.getDouble("hourlyRate"),
//                        rs.getDate("hireDate"),
//                        rs.getInt("workDays"),
//                        rs.getBoolean("isActive")));
//            }
//        } catch (SQLException e) {
//            updateStatusMessage("Error searching employees: " + e.getMessage(), true);
//        }
//        return results;
//    }
//
//    private void deleteEmployee(int empNum) {
//        String query = "DELETE FROM OrdinaryEmployee WHERE empNum = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, empNum);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error deleting employee: " + e.getMessage(), true);
//        }
//    }
//    
//    private Button createStyledButton(String text) {
//        Button button = new Button(text);
//       // button.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
//        button.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
//        return button;
//    }
//
//    private Button createStyledButtonWithImage(String text, String imagePath) {
//        Image image = new Image(getClass().getResourceAsStream(imagePath));
//        ImageView imageView = new ImageView(image);
//        imageView.setFitHeight(20);
//        imageView.setFitWidth(20);
//
//        Button button = new Button(text);
//       // button.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
//        button.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
//        button.setGraphic(imageView);
//
//        return button;
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
//        ObservableList<XYChart.Data<String, Number>> activeStatusChartData = FXCollections.observableArrayList();
//        
//        // List to hold employee names and hire dates for the BarChart
//        ArrayList<XYChart.Data<String, Number>> allEmployeesData = new ArrayList<>();
//        String youngestEmpName = "";
//        Date youngestEmpHireDate = null;
//        String oldestEmpName = "";
//        Date oldestEmpHireDate = null;
//
//        try {
//            // Establish the database connection
//            Connection connection = DatabaseConnection.getConnection();
//
//            if (connection != null) {
//                // Query to get the count of employees by role
//                String roleQuery = "SELECT role, COUNT(*) AS count FROM OrdinaryEmployee GROUP BY role";
//                java.sql.Statement statement = connection.createStatement();
//                ResultSet roleResultSet = statement.executeQuery(roleQuery);
//
//                // Process the result set and add data to PieChart
//                while (roleResultSet.next()) {
//                    String role = roleResultSet.getString("role");
//                    int count = roleResultSet.getInt("count");
//
//                    // Add data to PieChart with percentage calculation
//                    double totalEmployees = getTotalEmployeeCount(connection);
//                    double percentage = (count / totalEmployees) * 100;
//                    pieChartData.add(new PieChart.Data(role + " (" + String.format("%.1f", percentage) + "%)", count));
//                }
//
//                // Query to get all employees and their hire dates
//                String allEmployeesQuery = "SELECT e.empName, o.hireDate FROM Employee e " +
//                                            "JOIN OrdinaryEmployee o ON e.empNum = o.empNum " +
//                                            "ORDER BY o.hireDate ASC";
//                ResultSet allEmployeesResultSet = statement.executeQuery(allEmployeesQuery);
//
//                while (allEmployeesResultSet.next()) {
//                    String empName = allEmployeesResultSet.getString("empName");
//                    Date hireDate = allEmployeesResultSet.getDate("hireDate");
//
//                    // Add employee data to the BarChart
//                    allEmployeesData.add(new XYChart.Data<>(empName, hireDate != null ? hireDate.getTime() : 0));
//
//                    // Track the youngest employee (latest hire date)
//                    if (youngestEmpHireDate == null || hireDate.after(youngestEmpHireDate)) {
//                        youngestEmpHireDate = hireDate;
//                        youngestEmpName = empName;
//                    }
//
//                    // Track the oldest employee (earliest hire date)
//                    if (oldestEmpHireDate == null || hireDate.before(oldestEmpHireDate)) {
//                        oldestEmpHireDate = hireDate;
//                        oldestEmpName = empName;
//                    }
//                }
//
//                // Query to count active and inactive employees
//                String statusQuery = "SELECT isActive, COUNT(*) AS count FROM OrdinaryEmployee GROUP BY isActive";
//                ResultSet statusResultSet = statement.executeQuery(statusQuery);
//
//                int activeCount = 0;
//                int inactiveCount = 0;
//
//                while (statusResultSet.next()) {
//                    int isActive = statusResultSet.getInt("isActive");
//                    int count = statusResultSet.getInt("count");
//
//                    if (isActive == 1) {
//                        activeCount = count;
//                    } else {
//                        inactiveCount = count;
//                    }
//                }
//
//                connection.close();
//
//                // Add data for Active vs Inactive Employees to BarChart
//                activeStatusChartData.add(new XYChart.Data<>("Active", activeCount));
//                activeStatusChartData.add(new XYChart.Data<>("Inactive", inactiveCount));
//
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Handle the exception appropriately (e.g., show an error message to the user)
//        }
//
//        // Create PieChart for Role Distribution with Percentages
//        PieChart pieChart = new PieChart(pieChartData);
//        pieChart.setTitle("Employee Roles Distribution");
//
//        // Create BarChart for All Employees with their Hire Dates
//        CategoryAxis xAxis = new CategoryAxis();
//        xAxis.setLabel("Employee Name");
//        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("Hire Date (Time in ms)");
//
//        BarChart<String, Number> allEmployeesBarChart = new BarChart<>(xAxis, yAxis);
//        allEmployeesBarChart.setTitle("All Employees by Hire Date");
//
//        // Create and populate the BarChart Series for all employees
//        XYChart.Series<String, Number> allEmployeesSeries = new XYChart.Series<>();
//        allEmployeesSeries.setName("All Employees");
//        allEmployeesSeries.getData().addAll(allEmployeesData);
//        allEmployeesBarChart.getData().add(allEmployeesSeries);
//
//        // Create BarChart for Active vs Inactive Employees
//        BarChart<String, Number> activeStatusChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
//        activeStatusChart.setTitle("Active vs Inactive Employees");
//
//        // Create and populate the Active vs Inactive Employees BarChart
//        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
//        series2.setName("Employee Status");
//        series2.getData().addAll(activeStatusChartData);
//        activeStatusChart.getData().add(series2);
//
//        // Add label for youngest employee (smallest hire date)
//       /* Label youngestEmpLabel = new Label("Youngest Employee: " + youngestEmpName + " (Hired on: " + youngestEmpHireDate + ")");
//        youngestEmpLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");*/
//
//        // Add label for oldest employee (earliest hire date)
//        Label oldestEmpLabel = new Label("Oldest Employee: " + oldestEmpName + " (Hired on: " + oldestEmpHireDate + ")");
//        oldestEmpLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
//
//        // Layout for charts with the youngest and oldest employee labels
//        VBox chartLayout = new VBox(20, pieChart, allEmployeesBarChart, activeStatusChart,  oldestEmpLabel);
//        chartLayout.setPadding(new Insets(20));
//        chartLayout.setAlignment(Pos.CENTER);
//
//        // Set up the Scene and Stage for displaying the charts
//        Scene chartScene = new Scene(chartLayout, 800, 600);
//        chartStage.setScene(chartScene);
//        chartStage.initOwner(owner);
//        chartStage.show();
//        chartStage.setFullScreen(true);
//    }
//
//    // Helper function to get the total employee count
//    private double getTotalEmployeeCount(Connection connection) throws SQLException {
//        String totalEmployeeQuery = "SELECT COUNT(*) AS count FROM Employee";
//        java.sql.Statement totalEmployeeStatement = connection.createStatement();
//        ResultSet totalEmployeeResultSet = totalEmployeeStatement.executeQuery(totalEmployeeQuery);
//
//        if (totalEmployeeResultSet.next()) {
//            return totalEmployeeResultSet.getDouble("count");
//        }
//        return 0;
//    }
//
//
//    
//    
//
//}
