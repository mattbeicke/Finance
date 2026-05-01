package mattb.model;

/**
 * An Account object. It represents a row from the {@code account} table in the SQLite database.
 *
 * @param balance Account balance
 * @param type    Account type
 * @param name    Account name
 */
public record Account(double balance, String type, String name) {
}
