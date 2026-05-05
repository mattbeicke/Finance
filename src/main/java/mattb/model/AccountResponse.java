package mattb.model;

/**
 * An AccountResponse object. It represents the response from the DAO of a processed account
 *
 * @param success {@code true} if it finished successfully, {@code false} if not
 * @param message Message to be displayed in the Notification box
 */
public record AccountResponse(boolean success, String message) {
}
