package application;

import java.util.Date;

public class Order {
    private int orderId;
    private int customerId;
    private int menuItemId;
    private int quantity;
    private int branchId; 
    private Date orderDate;

    public Order(int orderId, int customerId, int menuItemId,int branchId, int quantity, Date orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.branchId=branchId;
    }

    public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setMenuItemId(int menuItemId) {
		this.menuItemId = menuItemId;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public int getCustomerId() {
        return customerId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", customerId=" + customerId + ", menuItemId=" + menuItemId
                + ", quantity=" + quantity + ", orderDate=" + orderDate + "]";
    }
}