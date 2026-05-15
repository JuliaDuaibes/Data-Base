package application;

public class Menu {
	private int menuItemId;
	private String itemName;
	private double itemPrice;
	private String category;
	private String size;
	private boolean isAvailable;
	private String imagePath;
	
	// Constructor with imagePath
	public Menu(int menuItemId, String itemName, double itemPrice, String category, String size, boolean isAvailable,
			String imagePath) {
		this.menuItemId = menuItemId;
		this.itemName = itemName;
		this.itemPrice = itemPrice;
		this.category = category;
		this.size = size;
		this.isAvailable = isAvailable;
		this.imagePath = imagePath;
	}

	// Constructor
	public Menu(String itemName, double itemPrice, String category, String imagePath) {
		this.itemName = itemName;
		this.itemPrice = itemPrice;
		this.category = category;
		this.imagePath = imagePath;
	}

	// Overloaded constructor without imagePath
	public Menu(int menuItemId, String itemName, double itemPrice, String category, String size, boolean isAvailable) {
		this(menuItemId, itemName, itemPrice, category, size, isAvailable, ""); // Default empty imagePath
	}

	// Getters and Setters
	public int getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(int menuItemId) {
		this.menuItemId = menuItemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public double getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(double itemPrice) {
		this.itemPrice = itemPrice;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean available) {
		isAvailable = available;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public String toString() {
		return "Menu{" + "menuItemId=" + menuItemId + ", itemName='" + itemName + '\'' + ", itemPrice=" + itemPrice
				+ ", category='" + category + '\'' + ", size='" + size + '\'' + ", isAvailable=" + isAvailable
				+ ", imagePath='" + imagePath + '\'' + '}';
	}
}
