package mattb.model;

import java.time.LocalDate;

/**
 * A TransactionRequest object. It represents a transaction that will be processed
 *
 * @param date      {@link Transaction} date
 * @param fromAcc   Name of the {@link Account} where money came from
 * @param toAcc     Name of the {@link Account} id where money went
 * @param amount    Amount of money transferred
 * @param category  Categories of the transaction (comma separated)
 * @param memo      Memo of the transaction
 * @param id        Database id of the transaction (if editing)
 * @param isEditing {@code true} if this request will update a transaction, {@code false} if saving new
 */
public record TransactionRequest(LocalDate date, String fromAcc, String toAcc, String amount, String category,
                                 String memo, int id, boolean isEditing) {
}