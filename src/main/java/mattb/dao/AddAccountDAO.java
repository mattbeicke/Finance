package mattb.dao;

import javafx.collections.ObservableList;
import mattb.model.Account;

/**
 * DAO Interface for the AddAccountController
 *
 * @author Matthew Beicke
 */
public interface AddAccountDAO {
    /**
     * Gets a list of all (non-{@code External} Account Types)
     *
     * @return A list of all Account Types plus a message that tells the user to add more in the {@link Account accounts} tab
     */
    ObservableList<String> getAllTypes();

    /**
     * Looks up the database id of the provided {@code type}
     *
     * @param type The {@code Account Type's} value
     * @return The database id of the {@code Account Type} or -1 if not found
     */
    int getTypeId(String type);

    /**
     * Saves an {@link Account} to the database or updates one that is there already
     *
     * @param typeId  Database id of the {@link Account account's} {@code type}
     * @param balance Balance of {@link Account}
     * @param name    Name of {@link Account}
     * @param id      Database id of account (only used if {@code editing})
     * @param editing Whether we are editing ({@code true}) or saving new ({@code false})
     */
    void saveAccount(int typeId, double balance, String name, int id, boolean editing);
}
