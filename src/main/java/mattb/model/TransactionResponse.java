package mattb.model;

/**
 * A TransactionRequest object. It represents the response from the DAO of a processed transaction
 *
 * @param success                     {@code true} if it finished successfully, {@code false} if not
 * @param message                     Message to be displayed in the Notification box
 * @param requiresBalanceConfirmation {@code true} if the {@code Update Balances} modal should open, {@code false} if not
 */
public record TransactionResponse(boolean success, String message, boolean requiresBalanceConfirmation) {
}
