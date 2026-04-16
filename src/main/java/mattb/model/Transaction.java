package mattb.model;

import java.util.Date;

public class Transaction {
    String toAccountName;
    String fromAccountName;
    double amount;
    String category;
    String memo;
    Date date;

    public Transaction(String toAccountName, String fromAccountName, double amount, String category, String memo, Date date) {
        this.toAccountName = toAccountName;
        this.fromAccountName = fromAccountName;
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.date = date;
    }

    public Transaction() {
    }

    public String getToAccountName() {
        return toAccountName;
    }

    public void setToAccountName(String toAccountName) {
        this.toAccountName = toAccountName;
    }

    public String getFromAccountName() {
        return fromAccountName;
    }

    public void setFromAccountName(String fromAccountName) {
        this.fromAccountName = fromAccountName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
