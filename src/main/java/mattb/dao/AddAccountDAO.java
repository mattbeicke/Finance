package mattb.dao;

import javafx.collections.ObservableList;

public interface AddAccountDAO {
    /**
     * Gets list of all Account Types
     *
     * @return A list of all Account Types plus a message that tells the user to add more in the account tab
     */
    ObservableList<String> getAllTypes();

    /**
     * Looks up the database id of the provided account type
     *
     * @param type Provided account type
     * @return The database id of the account type or -1 if not found
     */
    int getTypeId(String type);

    /**
     * Saves or updates an account to or in the database
     *
     * @param typeId  Database id of the accounts type
     * @param balance Balance of account
     * @param name    Name of account
     * @param id      Database id of account (if editing)
     * @param editing Whether we are editing or saving new
     */
    void saveAccount(int typeId, double balance, String name, int id, boolean editing);
}
