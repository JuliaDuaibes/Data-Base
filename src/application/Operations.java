//package application;
//
//import javafx.scene.paint.Color;
//import javafx.scene.paint.LinearGradient;
//import javafx.scene.paint.CycleMethod;
//import javafx.scene.paint.Stop;
//import javafx.scene.text.Text;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Properties;
//import javafx.scene.control.Button;
//import javafx.application.Application;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Pos;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableColumn.CellEditEvent;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextField;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.control.cell.TextFieldTableCell;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//import javafx.util.converter.FloatStringConverter;
//
//public class Operations extends Application {
//	public static void main(String[] args) {
//		launch(args);
//	}
//
//	private ArrayList<Menu> data; // ArrayList to store Menu objects
//	private ObservableList<Menu> datalist; // ObservableList for JavaFX bindings
//
//	private String dbURL;
//	private String dbUsername = "root";
//	private String dbPassword = "root1234";
//	private String URL = "127.0.0.1";
//	private String port = "3306";
//	private String dbName = "project";
//	private Connection con;
//
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		// Initialize data and datalist
//		data = new ArrayList<>();
//		try {
//			getData();
//			datalist = FXCollections.observableArrayList(data);
//
//			TableView(primaryStage);
//			primaryStage.show();
//		} catch (SQLException E) {
//			E.printStackTrace();
//		} catch (ClassNotFoundException E) {
//			E.printStackTrace();
//		}
//	}
//
//	private void TableView(Stage primaryStage) {
//		TableView<Menu> myDataTable = new TableView<>();
//
//		StackPane bp = new StackPane();
//		Scene scene = new Scene(new Group());
//		primaryStage.setTitle("Menu Table");
//		primaryStage.setWidth(500);
//		primaryStage.setHeight(500);
//
//		Label titleText = new Label("Menu Table");
//		titleText.setFont(new Font("Arial", 24));
//		titleText.setTextFill(Color.web("#09D1C7"));
//
//		titleText.setStyle("-fx-font-weight: bold;"); // Optional: Make text bold
//
//		myDataTable.setEditable(true);
//		myDataTable.setMaxHeight(200);
//		myDataTable.setMaxWidth(400);
//
//		// Define columns
//		TableColumn<Menu, Integer> IdCol = new TableColumn<>("itemId");
//		IdCol.setMinWidth(50);
//		IdCol.setCellValueFactory(new PropertyValueFactory<>("itemId"));
//
//		TableColumn<Menu, String> NameCol = new TableColumn<>("itemName");
//		NameCol.setMinWidth(50);
//		NameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
//
//		TableColumn<Menu, Float> PriceCol = new TableColumn<>("itemPrice");
//		PriceCol.setMinWidth(50);
//		PriceCol.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
//
//		TableColumn<Menu, String> CatogaryCol = new TableColumn<>("category");
//		CatogaryCol.setMinWidth(50);
//		CatogaryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
//
//		// Editing columns
//		NameCol.setCellFactory(TextFieldTableCell.forTableColumn());
//		NameCol.setOnEditCommit(t -> {
//			Menu menu = t.getTableView().getItems().get(t.getTablePosition().getRow());
//			String oldName = t.getOldValue();
//			String newName = t.getNewValue();
//			menu.setItemName(newName);
//			updateName(oldName, newName); // Update in the database
//		});
//
//		PriceCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
//		PriceCol.setOnEditCommit(t -> {
//			Menu menu = t.getTableView().getItems().get(t.getTablePosition().getRow());
//			Float oldPrice = t.getOldValue();
//			Float newPrice = t.getNewValue();
//			menu.setItemPrice(newPrice);
//			updatePrice(oldPrice, newPrice); // Update in the database
//		});
//
//		CatogaryCol.setCellFactory(TextFieldTableCell.forTableColumn());
//		CatogaryCol.setOnEditCommit(t -> {
//			Menu menu = t.getTableView().getItems().get(t.getTablePosition().getRow());
//			String oldCategory = t.getOldValue();
//			String newCategory = t.getNewValue();
//			menu.setCategory(newCategory);
//			updateCategory(oldCategory, newCategory); // Update in the database
//		});
//
//		// Table setup
//		myDataTable.setItems(datalist);
//		myDataTable.getColumns().addAll(IdCol, NameCol, PriceCol, CatogaryCol);
//
//		// Input fields for new menu item
//		final TextField Addid = new TextField();
//		Addid.setPromptText("IdItem");
//		Addid.setMaxWidth(IdCol.getPrefWidth());
//
//		final TextField Addname = new TextField();
//		Addname.setPromptText("itemName");
//		Addname.setMaxWidth(NameCol.getPrefWidth());
//
//		final TextField Addprice = new TextField();
//		Addprice.setPromptText("itemPrice");
//		Addprice.setMaxWidth(PriceCol.getPrefWidth());
//
//		final TextField AddCatogary = new TextField();
//		AddCatogary.setPromptText("ItemCatogary");
//		AddCatogary.setMaxWidth(CatogaryCol.getPrefWidth());
//
//		// Buttons for insert, delete, refresh
//		Button insertButton = styleButton("Insert");
//		insertButton.setOnAction(e -> {
//			Menu newMenu = new Menu(Integer.parseInt(Addid.getText()), Addname.getText(),
//					Float.parseFloat(Addprice.getText()), AddCatogary.getText());
//			datalist.add(newMenu);
//			insertData(newMenu);
//			Addid.clear();
//			Addname.clear();
//			Addprice.clear();
//			AddCatogary.clear();
//		});
//
//		Button deleteButton = styleButton("Delete");
//		deleteButton.setOnAction(e -> {
//			Menu selectedMenu = myDataTable.getSelectionModel().getSelectedItem();
//			if (selectedMenu != null) {
//				myDataTable.getItems().remove(selectedMenu);
//				deleteRow(selectedMenu);
//			}
//		});
//
//		Button refreshButton = styleButton("Refresh");
//		refreshButton.setOnAction(e -> myDataTable.refresh());
//
//		// Layout containers for input and buttons
//		VBox inputBox = new VBox(10, Addid, Addname, Addprice, AddCatogary);
//		HBox actionBox = new HBox(10, insertButton, deleteButton, refreshButton);
//		inputBox.setAlignment(Pos.CENTER);
//		actionBox.setAlignment(Pos.CENTER);
//
//		// Center the VBox content horizontally
//		VBox vbox = new VBox(20, titleText, myDataTable, actionBox, inputBox);
//		vbox.setAlignment(Pos.CENTER); // Center the entire VBox content
//		vbox.setSpacing(10); // Optional: Add spacing between the components for better visibility
//
//		// Ensure proper centering in StackPane
//		bp.getChildren().add(vbox);
//		StackPane.setAlignment(vbox, Pos.CENTER);
//		
//		 // Apply CSS styles for the table headers
//	   myDataTable.setStyle(
//	            "-fx-table-header-row {"
//	            + "    -fx-background-color: linear-gradient(to bottom, #4DB1CC, #1B879F);" // Gradient header
//	            + "    -fx-font-family: 'Segoe UI', Calibri, Arial, sans-serif;" // Matching font family
//	            + "    -fx-font-size: 14px;" // Header font size
//	            + "    -fx-font-weight: bold;" // Bold header text
//	            + "    -fx-text-fill: white;" // White header text
//	            + "    -fx-alignment: CENTER;" // Center alignment
//	            + "} "
//	    );
//	   // Style for individual columns (text alignment and font color)
//	    String columnStyle = "-fx-alignment: CENTER; "
//	            + "-fx-font-size: 12px; "
//	            + "-fx-font-family: 'Segoe UI', Calibri, Arial, sans-serif; "
//	            + "-fx-text-fill: #073A4B; "
//	            + "-fx-font-weight: bold;";
//	    
//	    IdCol.setStyle(columnStyle);
//	    NameCol.setStyle(columnStyle);
//	    PriceCol.setStyle(columnStyle);
//	    CatogaryCol.setStyle(columnStyle);
//	    
//
//	    // Optional: Add light gray grid lines and table background
//	    myDataTable.setStyle(
//	            "-fx-background-color: white;" // Table background
//	            + "-fx-border-color: lightgray;" // Light gray border
//	            + "-fx-border-width: 1px;" // Border width
//	            + "-fx-table-cell-border-color: lightgray;" // Cell border color
//	            + "-fx-grid-lines-visible: true;" // Grid lines visible
//	    );
//
//		// Set the scene and stage
//		((Group) scene.getRoot()).getChildren().add(bp);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	}
//
//	private void deleteRow(Menu menu) {
//		try {
//			// SQL query to delete a row where itemId matches
//			String sql = "DELETE FROM Menu WHERE itemId = ?";
//
//			// Connect to the database
//			connectDb();
//
//			// Create a PreparedStatement to execute the SQL query
//			PreparedStatement pst = con.prepareStatement(sql);
//
//			// Set the parameter in the query (itemId)
//			pst.setInt(1, menu.getItemId());
//
//			// Execute the delete query
//			int rowsAffected = pst.executeUpdate();
//
//			// If rows are affected, print a success message
//			if (rowsAffected > 0) {
//				System.out.println("Menu item with ID " + menu.getItemId() + " was deleted successfully.");
//			} else {
//				System.out.println("No menu item found with ID " + menu.getItemId() + ".");
//			}
//
//			// Close the PreparedStatement and Connection
//			pst.close();
//			con.close();
//		} catch (SQLException | ClassNotFoundException e) {
//			e.printStackTrace(); // Print error if any
//		}
//	}
//
//	private void insertData(Menu menu) {
//		try {
//			// Check if the itemId already exists in the database
//			String checkSql = "SELECT COUNT(*) FROM Menu WHERE itemId = ?";
//			connectDb();
//			PreparedStatement checkStmt = con.prepareStatement(checkSql);
//			checkStmt.setInt(1, menu.getItemId());
//			ResultSet checkRs = checkStmt.executeQuery();
//
//			checkRs.next();
//			int count = checkRs.getInt(1);
//
//			if (count == 0) {
//				 No duplicate, proceed with insert
//				String insertSql = "INSERT INTO Menu (itemId, itemName, itemPrice, category) VALUES (?, ?, ?, ?)";
//				PreparedStatement insertStmt = con.prepareStatement(insertSql);
//				insertStmt.setInt(1, menu.getItemId());
//				insertStmt.setString(2, menu.getItemName());
//				insertStmt.setFloat(3, menu.getItemPrice());
//				insertStmt.setString(4, menu.getCategory());
//				int rowsAffected = insertStmt.executeUpdate();
//
//				if (rowsAffected > 0) {
//					System.out.println("Inserted new menu item: " + menu);
//				}
//				insertStmt.close();
//			} else {
//				System.out.println("Duplicate itemId: " + menu.getItemId() + ". Insert operation skipped.");
//			}
//
//			checkRs.close();
//			checkStmt.close();
//			con.close();
//		} catch (SQLException | ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void updateName(String oldName, String newName) {
//		try {
//			String sql = "UPDATE Menu SET itemName = ? WHERE itemName = ?";
//			connectDb();
//			PreparedStatement pst = con.prepareStatement(sql);
//			pst.setString(1, newName);
//			pst.setString(2, oldName);
//			int rowsAffected = pst.executeUpdate();
//			if (rowsAffected > 0) {
//				System.out.println("Updated itemName: " + oldName + " -> " + newName);
//			}
//			pst.close();
//			con.close();
//		} catch (SQLException | ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void updatePrice(Float oldPrice, Float newPrice) {
//		try {
//			String sql = "UPDATE Menu SET itemPrice = ? WHERE itemPrice = ?";
//			connectDb();
//			PreparedStatement pst = con.prepareStatement(sql);
//			pst.setFloat(1, newPrice);
//			pst.setFloat(2, oldPrice);
//			int rowsAffected = pst.executeUpdate();
//			if (rowsAffected > 0) {
//				System.out.println("Updated itemPrice: " + oldPrice + " -> " + newPrice);
//			}
//			pst.close();
//			con.close();
//		} catch (SQLException | ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void updateCategory(String oldCategory, String newCategory) {
//		try {
//			String sql = "UPDATE Menu SET category = ? WHERE category = ?";
//			connectDb();
//			PreparedStatement pst = con.prepareStatement(sql);
//			pst.setString(1, newCategory);
//			pst.setString(2, oldCategory);
//			int rowsAffected = pst.executeUpdate();
//			if (rowsAffected > 0) {
//				System.out.println("Updated category: " + oldCategory + " -> " + newCategory);
//			}
//			pst.close();
//			con.close();
//		} catch (SQLException | ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void getData() throws SQLException, ClassNotFoundException {
//		String sql = "SELECT itemId, itemName, itemPrice, category FROM Menu";
//
//		connectDb(); // Establish the connection
//		System.out.println("Connection established.");
//
//		// Use try-with-resources for automatic resource management
//		Statement stmt = con.createStatement();
//		ResultSet rs = stmt.executeQuery(sql);
//
//		while (rs.next())
//			data.add(new Menu(Integer.parseInt(rs.getString(1)), // itemId
//					rs.getString(2), // itemName
//					Float.parseFloat(rs.getString(3)), // itemPrice (converted to float)
//					rs.getString(4) // category
//			));
//		rs.close();
//		stmt.close();
//
//		con.close();
//		System.out.println("Connention closed" + data.size());
//
//	}
//
//	private Connection connectDb() throws ClassNotFoundException, SQLException {
//		dbURL = "jdbc:mysql://" + URL + ":" + port + "/" + dbName + "?verifyServerCertificate=false";
//
//		Properties p = new Properties();
//		p.setProperty("user", dbUsername);
//		p.setProperty("password", dbPassword);
//		p.setProperty("useSSL", "false");
//		p.setProperty("autoReconnect", "true");
//
//		Class.forName("com.mysql.cj.jdbc.Driver");
//		con = DriverManager.getConnection(dbURL, p);
//		return con;
//
//	}
//
//	// style the buttons
//	private Button styleButton(String text) {
//		String buttonStyle = "-fx-background-color: linear-gradient(to right, #09D1C7, #15919B,#09D1C7); -fx-text-fill: white; -fx-background-radius: 30;";
//		String buttonHoverStyle = "-fx-background-color: linear-gradient(to right,  #15919B, #09D1C7,#15919B); -fx-text-fill: white; -fx-background-radius: 30;";
//		// One Player Button
//		Button button = new Button(text);
//		button.setStyle(buttonStyle);
//		button.setOnMouseEntered(e -> button.setStyle(buttonStyle));
//		button.setOnMouseExited(e -> button.setStyle(buttonHoverStyle));
//		return button;
//	}
//
//}
