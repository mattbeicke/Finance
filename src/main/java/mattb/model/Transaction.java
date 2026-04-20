package mattb.model;

import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(amount, that.amount) == 0 && Objects.equals(toAccountName, that.toAccountName) && Objects.equals(fromAccountName, that.fromAccountName) && Objects.equals(category, that.category) && Objects.equals(memo, that.memo) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toAccountName, fromAccountName, amount, category, memo, date);
    }
}
