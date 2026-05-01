package mattb.model;

import java.util.Date;

/**
 * A Transaction object. It represents a row from the {@code transaction} table and rows from the {@code tcat} table in the SQLite database.
 *
 * @param toAccountName   Name of account where money went
 * @param fromAccountName Name of account where money came from
 * @param amount          Amount of money transferred
 * @param category        Categories of the transaction
 * @param memo            Memo of the transaction
 * @param date            Date of the transaction
 */
public record Transaction(String toAccountName, String fromAccountName, double amount, String category, String memo,
                          Date date) {
}
