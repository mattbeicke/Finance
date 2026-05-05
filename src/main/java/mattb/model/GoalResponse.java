package mattb.model;

/**
 * A GoalResponse object. It represents the response from the DAO of a processed goal
 *
 * @param success {@code true} if it finished successfully, {@code false} if not
 * @param message Message to be displayed in the Notification box
 */
public record GoalResponse(boolean success, String message) {
}
