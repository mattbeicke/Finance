package mattb.model;

import java.time.LocalDate;

/**
 * A TransactionRequest object. It represents a transaction that will be processed
 *
 * @param date      {@link Transaction} date
 * @param fromId    Database id of the {@link Account} where money came from
 * @param toId      Database id of the {@link Account} id where money went
 * @param amount    Amount of money transferred
 * @param category  Categories of the transaction (comma separated)
 * @param memo      Memo of the transaction
 * @param id        Database id of the transaction (if editing)
 * @param isEditing {@code true} if this request will update a transaction, {@code false} if saving new
 */
public record TransactionRequest(LocalDate date, int fromId, int toId, double amount, String category, String memo,
                                 int id, boolean isEditing) {
}