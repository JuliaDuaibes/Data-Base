//package application;
//import javafx.application.Application;
//import java.util.Objects;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.chart.PieChart;
//import javafx.scene.chart.XYChart;
//import javafx.scene.chart.BarChart;
//import javafx.scene.chart.CategoryAxis;
//import javafx.scene.chart.NumberAxis;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.stage.Screen;
//import javafx.stage.Stage;
//
//import java.sql.*;
//import java.util.Objects;
//public class ManagerCollector extends Application {
//
//    private TableView<Manager> tableView = new TableView<>();
//    private Label statusLabel = new Label();
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) {
//        tableView = createManagerTableView();
//
//        Button addButton = createStyledButtonWithImage("Add Manager", "ADD (2).png");
//        Button updateButton = createStyledButtonWithImage("Update Manager", "update.png");
//        Button deleteButton = createStyledButtonWithImage("Delete Manager", "delete.png");
//        Button backToMainButton = createStyledButton("Back to Main");
//       // Button viewChartButton = createStyledButton("View Manager Charts");
//        Button viewRoleStatsButton = createStyledButtonWithImage("View Charts ","icons8-seo-graph-66.png");
//
//        viewRoleStatsButton.setOnAction(e -> openManagerChartScreen(stage));
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
//        addButton.setOnAction(e -> showManagerForm(stage, "Add Manager", null));
//        updateButton.setOnAction(e -> {
//            Manager selectedManager = tableView.getSelectionModel().getSelectedItem();
//            if (selectedManager != null) {
//                showManagerForm(stage, "Update Manager", selectedManager);
//            } else {
//                updateStatusMessage("Please select a manager to update.", true);
//            }
//        });
//        deleteButton.setOnAction(e -> {
//            Manager selectedManager = tableView.getSelectionModel().getSelectedItem();
//            if (selectedManager != null) {
//                tableView.getItems().remove(selectedManager);
//                deleteManager(selectedManager.getEmpNum());
//                updateStatusMessage("Manager deleted successfully.", false);
//            } else {
//                updateStatusMessage("Please select a manager to delete.", true);
//            }
//        });
//        searchButton.setOnAction(e -> {
//            String searchText = searchField.getText().trim();
//            if (!searchText.isEmpty()) {
//                ObservableList<Manager> results = searchManagersByNameOrId(searchText);
//                tableView.setItems(results);
//            } else {
//                updateStatusMessage("Enter a name or ID to search.", true);
//            }
//        });
//        clearSearchButton.setOnAction(e -> {
//            searchField.clear();
//            tableView.setItems(fetchAllManagers());
//        });
//
//        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton,  viewRoleStatsButton);
//        buttonBox.setPadding(new Insets(10));
//        VBox root = new VBox(10, statusLabel, searchBox, buttonBox, tableView, backToMainButton);
//        root.setPadding(new Insets(10));
//       
//        root.setBackground(new Background(new BackgroundFill(Color.web("#283645"), null, null))); // Set background color
//
//        double screenWidth = Screen.getPrimary().getBounds().getWidth();
//        double screenHeight = Screen.getPrimary().getBounds().getHeight();
//
//        // Scene setup
//        Scene scene = new Scene(root, screenWidth, screenHeight); // Adjusted window size
//
//       // Scene scene = new Scene(root, 800, 600);
//        stage.setScene(scene);
//        stage.setTitle("Manager Manager");
//        stage.show();
//    }
//
// 
//    private void openManagerChartScreen(Stage owner) {
//        Stage chartStage = new Stage();
//        chartStage.setTitle("Manager Statistics");
//
//        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
//        ObservableList<XYChart.Data<String, Number>> barChartData = FXCollections.observableArrayList();
//        Label highestSalaryLabel = new Label();
//
//        // Initialize highestBranch outside try block for scope access
//        final String[] highestBranch = {""};  // Using an array to hold the value
//        double highestSalary = 0.0;       
//        try (Connection connection = DatabaseConnection.getConnection()) {
//            if (connection != null) {
//                // استعلام للحصول على المدير صاحب أعلى راتب
//                String queryHighestSalary = """
//                        SELECT Manager.fixedSalary, Employee.empName, Branch.branchName 
//                        FROM Manager 
//                        INNER JOIN Employee ON Manager.empNum = Employee.empNum 
//                        INNER JOIN Branch ON Employee.branchId = Branch.branchId 
//                        ORDER BY Manager.fixedSalary DESC LIMIT 1
//                        """;
//                Statement statement = connection.createStatement();
//                ResultSet resultSet = statement.executeQuery(queryHighestSalary);
//
//                if (resultSet.next()) {
//                    String empName = resultSet.getString("empName");
//                    highestSalary = resultSet.getDouble("fixedSalary");
//                    highestBranch[0] = resultSet.getString("branchName"); // تخزين اسم الفرع
//                    highestSalaryLabel.setText("Highest Salary: " + highestSalary + 
//                                                " by " + empName + 
//                                                " in Branch: " + highestBranch[0]);
//                } else {
//                    highestSalaryLabel.setText("No data available for highest salary.");
//                }
//
//                // استعلام لتوزيع المديرين حسب الفروع
//                String queryDistribution = """
//                        SELECT Branch.branchName, COUNT(*) AS managerCount
//                        FROM Manager 
//                        INNER JOIN Employee ON Manager.empNum = Employee.empNum 
//                        INNER JOIN Branch ON Employee.branchId = Branch.branchId 
//                        GROUP BY Branch.branchName
//                        """;
//                resultSet = statement.executeQuery(queryDistribution);
//
//                while (resultSet.next()) {
//                    String branchName = resultSet.getString("branchName");
//                    int count = resultSet.getInt("managerCount");
//
//                    PieChart.Data slice = new PieChart.Data(branchName, count);
//                    pieChartData.add(slice);
//
//                    // منطق تمييز الفرع صاحب أعلى راتب
//                    slice.nodeProperty().addListener((observable, oldValue, newValue) -> {
//                        if (newValue != null && highestBranch[0] != null && !highestBranch[0].isEmpty() && 
//                            branchName != null && branchName.equals(highestBranch[0])) {
//                            newValue.setStyle("-fx-pie-color: gold;");
//                        }
//                    });
//
//                    barChartData.add(new XYChart.Data<>(branchName, count));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//        // Create PieChart
//        PieChart pieChart = new PieChart(pieChartData);
//        pieChart.setTitle("Manager Distribution by Branch");
//        pieChart.setLegendVisible(true);
//
//        // Add data labels to PieChart
//        for (PieChart.Data data : pieChartData) {
//            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue());
//            Tooltip.install(data.getNode(), tooltip);
//        }
//
//        // Create BarChart
//        CategoryAxis xAxis = new CategoryAxis();
//        xAxis.setLabel("Branch");
//        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("Number of Managers");
//        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
//        barChart.getData().add(new XYChart.Series<>("Manager Distribution", barChartData));
//        barChart.setTitle("Manager Count per Branch");
//
//        VBox chartBox = new VBox(10, highestSalaryLabel, pieChart, barChart);
//        chartBox.setPadding(new Insets(10));
//        Scene scene = new Scene(chartBox, 800, 600);
//        chartStage.setScene(scene);
//        chartStage.show();
//    }
//
//
//
//    // Include all other methods (createManagerTableView, showManagerForm, etc.) here...
//    private TableView<Manager> createManagerTableView() {
//        TableView<Manager> table = new TableView<>();
//        TableColumn<Manager, Integer> idColumn = new TableColumn<>("ID");
//        TableColumn<Manager, Double> salaryColumn = new TableColumn<>("Fixed Salary");
//
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("empNum"));
//        idColumn.setPrefWidth(100);
//        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("fixedSalary"));
//        salaryColumn.setPrefWidth(150);
//
//        table.getColumns().addAll(idColumn, salaryColumn);
//        table.setItems(fetchAllManagers());
//        return table;
//    }
//
//    private void showManagerForm(Stage owner, String title, Manager manager) {
//        Stage formStage = new Stage();
//        formStage.setTitle(title);
//
//        TextField salaryField = new TextField();
//        TextField empNumField = new TextField();
//
//        if (manager != null) {
//            empNumField.setText(String.valueOf(manager.getEmpNum()));
//            salaryField.setText(String.valueOf(manager.getFixedSalary()));
//        }
//
//        GridPane form = new GridPane();
//        form.setPadding(new Insets(10));
//        form.setHgap(10);
//        form.setVgap(10);
//        form.add(new Label("Employee Number:"), 0, 0);
//        form.add(empNumField, 1, 0);
//        form.add(new Label("Fixed Salary:"), 0, 1);
//        form.add(salaryField, 1, 1);
//
//        Button saveButton = new Button("Save");
//        Button cancelButton = new Button("Cancel");
//        HBox buttonBox = new HBox(10, saveButton, cancelButton);
//        form.add(buttonBox, 1, 2);
//
//        saveButton.setOnAction(e -> {
//            String salaryText = salaryField.getText();
//            String empNumText = empNumField.getText();
//
//            if (empNumText.isEmpty() || salaryText.isEmpty()) {
//                updateStatusMessage("Fields can't be empty.", true);
//                return;
//            }
//
//            try {
//                int empNum = Integer.parseInt(empNumText);
//                double salary = Double.parseDouble(salaryText);
//                if (manager == null) {
//                    addManager(empNum, salary);
//                } else {
//                    updateManager(manager.getEmpNum(), salary);
//                }
//                tableView.setItems(fetchAllManagers());
//                formStage.close();
//            } catch (NumberFormatException ex) {
//                updateStatusMessage("Salary and Employee Number must be numbers.", true);
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
//    private void addManager(int empNum, double salary) {
//        String query = "INSERT INTO Manager (empNum, fixedSalary) VALUES (?, ?)";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, empNum);
//            stmt.setDouble(2, salary);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error adding manager: " + e.getMessage(), true);
//        }
//    }
//
//    private void updateManager(int empNum, double salary) {
//        String query = "UPDATE Manager SET fixedSalary = ? WHERE empNum = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setDouble(1, salary);
//            stmt.setInt(2, empNum);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error updating manager: " + e.getMessage(), true);
//        }
//    }
//
//    private void deleteManager(int empNum) {
//        String query = "DELETE FROM Manager WHERE empNum = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, empNum);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            updateStatusMessage("Error deleting manager: " + e.getMessage(), true);
//        }
//    }
//
//    private ObservableList<Manager> fetchAllManagers() {
//        ObservableList<Manager> managers = FXCollections.observableArrayList();
//        String query = "SELECT * FROM Manager";
//        try (Connection conn = DatabaseConnection.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                managers.add(new Manager(
//                        rs.getInt("empNum"),
//                        rs.getDouble("fixedSalary")
//                ));
//            }
//        } catch (SQLException e) {
//            updateStatusMessage("Error fetching managers: " + e.getMessage(), true);
//        }
//        return managers;
//    }
//
//    private ObservableList<Manager> searchManagersByNameOrId(String searchText) {
//        ObservableList<Manager> managers = FXCollections.observableArrayList();
//        String query = "SELECT * FROM Manager WHERE empNum = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, Integer.parseInt(searchText));
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                managers.add(new Manager(
//                        rs.getInt("empNum"),
//                        rs.getDouble("fixedSalary")
//                ));
//            }
//        } catch (SQLException e) {
//            updateStatusMessage("Error searching managers: " + e.getMessage(), true);
//        }
//        return managers;
//    }
//
//    private void updateStatusMessage(String message, boolean isError) {
//        statusLabel.setText(message);
//        statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
//    }
//
//    private Button createStyledButton(String text) {
//        Button button = new Button(text);
//        
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
//        button.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
//        
//        button.setGraphic(imageView);
//
//        return button;
//    }
//
//    
//}
