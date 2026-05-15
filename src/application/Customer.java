package application;

public class Customer {
	private int customerId;
	private String name;
	private String phoneNumber;
	private String paymentMethod;
	private boolean isDiningIn;

	public Customer(int customerId, String name, String phoneNumber, String paymentMethod, boolean isDiningIn) {
		this.customerId = customerId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.paymentMethod = paymentMethod;
		this.isDiningIn = isDiningIn;
	}
	public Customer( String name, String phoneNumber, String paymentMethod, boolean isDiningIn) {
//		this.customerId = customerId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.paymentMethod = paymentMethod;
		this.isDiningIn = isDiningIn;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setDiningIn(boolean isDiningIn) {
		this.isDiningIn = isDiningIn;
	}

	public int getCustomerId() {
		return customerId;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public boolean isDiningIn() {
		return isDiningIn;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", name=" + name + ", phoneNumber=" + phoneNumber
				+ ", paymentMethod=" + paymentMethod + ", isDiningIn=" + isDiningIn + "]";
	}
}
