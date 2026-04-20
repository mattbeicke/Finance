package mattb.model;

import java.util.Objects;

public class Account {
    double balance;
    String type;
    String name;

    public Account() {
    }

    public Account(double balance, String type, String name) {
        this.balance = balance;
        this.type = type;
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Double.compare(balance, account.balance) == 0 && Objects.equals(type, account.type) && Objects.equals(name, account.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, type, name);
    }
}
