package mattb.model;

import java.util.Objects;

public record Account(double balance, String type, String name) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Double.compare(balance, account.balance) == 0 && Objects.equals(type, account.type) && Objects.equals(name, account.name);
    }
}
