

package application;

public class MonthlySalary {
    private int branchID;
    private String month;
    private double totalSalary;
    private String paymentDate;
    private String notes;

    public MonthlySalary(int branchID, String month, double totalSalary, String paymentDate, String notes) {
        this.branchID = branchID;
        this.month = month;
        this.totalSalary = totalSalary;
        this.paymentDate = paymentDate;
        this.notes = notes;
    }

    
    public void setBranchID(int branchID) {
		this.branchID = branchID;
	}


	public void setMonth(String month) {
		this.month = month;
	}


	public void setTotalSalary(double totalSalary) {
		this.totalSalary = totalSalary;
	}


	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	public int getBranchID() {
        return branchID;
    }

    public String getMonth() {
        return month;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "Branch ID: " + branchID + ", Month: " + month + ", Total Salary: " + totalSalary;
    }
}
//package application;
////
////public class MonthlySalary {
////	private	int BranchID ;//                              -- معرف الفرع (مرتبط بـ Branches)
////	private String Month ;//NULL,               -- الشهر (مثل: January, February)
////	private double SalaryAmount;// DECIMAL(10, 2) NOT NULL,     -- قيمة الراتب
////	private int   PaymentDate;  //              -- تاريخ الدفع
////	private String NotesTEXT;
////	public int getBranchID() {
////		return BranchID;
////	}
////	public void setBranchID(int branchID) {
////		BranchID = branchID;
////	}
////	public String getMonth() {
////		return Month;
////	}
////	public void setMonth(String month) {
////		Month = month;
////	}
////	public double getSalaryAmount() {
////		return SalaryAmount;
////	}
////	public void setSalaryAmount(double salaryAmount) {
////		SalaryAmount = salaryAmount;
//package application;
//
//import java.util.ArrayList;
//
//public class MonthlySalary {
//    private int branchID;                  // رقم الفرع
//    private String month;                  // الشهر
//    private double totalSalary;            // مجموع الأجور
//    private String paymentDate;            // تاريخ الدفع
//    private String notes;                  // ملاحظات
//    private ArrayList<Salary> salaries;    // قائمة بالأجور لكل موظف
//
//    public MonthlySalary(int branchID, String month, double totalSalary, String paymentDate, String notes) {
//        this.branchID = branchID;
//        this.month = month;
//        this.totalSalary = totalSalary;
//        this.paymentDate = paymentDate;
//        this.notes = notes;
//        this.salaries = new ArrayList<>();
//    }
//
//    // إضافة راتب لموظف في قائمة الرواتب
//    public void addSalary(Salary salary) {
//        if (salary.getBranchId() == this.branchID && salary.getMonth().equals(this.month)) {
//            salaries.add(salary);
//            calculateTotalSalary();
//        } else {
//            throw new IllegalArgumentException("Salary branch or month does not match MonthlySalary.");
//        }
//    }
//
//    // حساب المجموع الكلي للرواتب
//    private void calculateTotalSalary() {
//        totalSalary = salaries.stream().mapToDouble(Salary::calculateSalary).sum();
//    }
//
//    // Getters and Setters
//    public int getBranchID() {
//        return branchID;
//    }
//
//    public void setBranchID(int branchID) {
//        this.branchID = branchID;
//    }
//
//    public String getMonth() {
//        return month;
//    }
//
//    public void setMonth(String month) {
//        this.month = month;
//    }
//
//    public double getTotalSalary() {
//        return totalSalary;
//    }
//
//    public void setTotalSalary(double totalSalary) {
//        this.totalSalary = totalSalary;
//    }
//
//    public String getPaymentDate() {
//        return paymentDate;
//    }
//
//    public void setPaymentDate(String paymentDate) {
//        this.paymentDate = paymentDate;
//    }
//
//    public String getNotes() {
//        return notes;
//    }
//
//    public void setNotes(String notes) {
//        this.notes = notes;
//    }
//
//    public ArrayList<Salary> getSalaries() {
//        return salaries;
//    }
//
//    @Override
//    public String toString() {
//        return "MonthlySalary{" +
//                "branchID=" + branchID +
//                ", month='" + month + '\'' +
//                ", totalSalary=" + totalSalary +
//                ", paymentDate='" + paymentDate + '\'' +
//                ", notes='" + notes + '\'' +
//                ", salaries=" + salaries +
//                '}';
//    }
//}

////	}
////	public int getPaymentDate() {
////		return PaymentDate;
////	}
////	public void setPaymentDate(int paymentDate) {
////		PaymentDate = paymentDate;
////	}
////	public String getNotesTEXT() {
////		return NotesTEXT;
////	}
////	public void setNotesTEXT(String notesTEXT) {
////		NotesTEXT = notesTEXT;
////	}
////	public MonthlySalary(int branchID, String month, double salaryAmount, int paymentDate, String notesTEXT) {
////		super();
////		BranchID = branchID;
////		Month = month;
////		SalaryAmount = salaryAmount;
////		PaymentDate = paymentDate;
////		NotesTEXT = notesTEXT;
////	}
////	@Override
////	public String toString() {
////		return "MonthlySalary [BranchID=" + BranchID + ", Month=" + Month + ", SalaryAmount=" + SalaryAmount
////				+ ", PaymentDate=" + PaymentDate + ", NotesTEXT=" + NotesTEXT + "]";
////	}                  
////
////	
////	
////}
//public class MonthlySalary {
//    private int branchID;
//    private String month;
//    private double salaryAmount;
//    private String paymentDate;
//    private String notes;
//
//    public MonthlySalary(int branchID, String month, double salaryAmount, String paymentDate, String notes) {
//        this.branchID = branchID;
//        this.month = month;
//        this.salaryAmount = salaryAmount;
//        this.paymentDate = paymentDate;
//        this.notes = notes;
//    }
//
//    public int getBranchID() {
//        return branchID;
//    }
//
//    public void setBranchID(int branchID) {
//        this.branchID = branchID;
//    }
//
//    public String getMonth() {
//        return month;
//    }
//
//    public void setMonth(String month) {
//        this.month = month;
//    }
//
//    public double getSalaryAmount() {
//        return salaryAmount;
//    }
//
//    public void setSalaryAmount(double salaryAmount) {
//        this.salaryAmount = salaryAmount;
//    }
//
//    public String getPaymentDate() {
//        return paymentDate;
//    }
//
//    public void setPaymentDate(String paymentDate) {
//        this.paymentDate = paymentDate;
//    }
//
//    public String getNotes() {
//        return notes;
//    }
//
//    public void setNotes(String notes) {
//        this.notes = notes;
//    }
//
//    @Override
//    public String toString() {
//        return "BranchID: " + branchID + ", Month: " + month + ", Salary: " + salaryAmount;
//    }
//}
//
