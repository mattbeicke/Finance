package mattb.model;

import java.util.Date;
import java.util.Objects;

public record Transaction(String toAccountName, String fromAccountName, double amount, String category, String memo,
                          Date date) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(amount, that.amount) == 0 && Objects.equals(toAccountName, that.toAccountName) && Objects.equals(fromAccountName, that.fromAccountName) && Objects.equals(category, that.category) && Objects.equals(memo, that.memo) && Objects.equals(date, that.date);
    }
}
