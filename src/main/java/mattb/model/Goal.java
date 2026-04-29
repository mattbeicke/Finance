package mattb.model;

import java.util.ArrayList;

public class Goal {
    double current;
    double target;
    ArrayList<String> accounts;
    String name;

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public ArrayList<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<String> accounts) {
        this.accounts = accounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Goal(double current, double target, ArrayList<String> accounts, String name) {
        this.current = current;
        this.target = target;
        this.accounts = accounts;
        this.name = name;
    }
}
