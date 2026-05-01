package mattb.model;

import java.util.Date;

public record Transaction(String toAccountName, String fromAccountName, double amount, String category, String memo,
                          Date date) {
}
