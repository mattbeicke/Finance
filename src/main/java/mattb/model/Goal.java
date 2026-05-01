package mattb.model;

/**
 * A Goal object. It represents a row from the {@code goal} table in the SQLite database.
 *
 * @param current Current balance of account goal is tracking
 * @param initial Initial balance of account when goal was created
 * @param target  Target balance of account by this goal
 * @param account Name of account this goal is tracking
 * @param name    Name of this goal
 */
public record Goal(double current, double initial, double target, String account, String name) {
}
