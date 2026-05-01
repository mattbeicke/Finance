package mattb;

/**
 * List of all the custom Error codes and messages. Used by {@code FinanceException}
 *
 * @author Matthew Beicke
 */
public enum FinanceError {
    /**
     * Opening Tabs/Modals
     **/
    OPEN_MAIN_FAILED("Failed to open Main page"),
    OPEN_DASHBOARD_TAB_FAILED("Failed to open Dashboard tab"),
    OPEN_TRANSACTIONS_TAB_FAILED("Failed to open Transactions tab"),
    OPEN_ACCOUNTS_TAB_FAILED("Failed to open Accounts tab"),
    OPEN_SETTINGS_TAB_FAILED("Failed to open Settings tab"),
    OPEN_NEW_ACCOUNT_MODAL_FAIL("Failed to open Create New Account modal"),
    OPEN_NEW_TYPE_MODAL_FAIL("Failed to open Create New Account Type modal"),
    OPEN_EDIT_ACCOUNT_MODAL_FAIL("Failed to open Edit Account modal"),
    OPEN_UPDATE_BALANCE_MODAL_FAIL("Failed to open Update Balance modal"),
    OPEN_NEW_TRANSACTION_MODAL_FAIL("Failed to open Create New Transaction modal"),
    OPEN_EDIT_TRANSACTION_MODAL_FAIL("Failed to open Edit Transaction modal"),
    OPEN_GOALS_LIST_FAILED("Failed to open Goals list"),
    OPEN_NEW_GOAL_MODAL_FAIL("Failed to open Create New Goal modal"),
    OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL("Failed to open View Goal Details modal"),

    /**
     * SQL
     **/
    DATABASE_CONNECTION_FAIL("Failed to connect to database"),
    SCHEMA_NOT_FOUND("Database schema file not found"),
    DATABASE_CREATION_FAIL("Failed to initialize tables"),
    // Saving
    SAVE_ACCOUNT_TYPE_FAIL("Error saving new type to database"),
    SAVE_ACCOUNT_FAIL("Error saving account to database"),
    SAVE_TRANSACTION_FAIL("Error saving transaction to database"),
    SAVE_CATEGORY_FAIL("Error saving new category to database"),
    SAVE_TRANSACTION_CATEGORY_FAIL("Error saving transactions categories to database"),
    SAVE_GOAL_FAIL("Error saving goal to database"),
    UPDATE_HIDDEN_ACCOUNT_LIST_FAIL("Error updating list of hidden accounts in database"),
    UPDATE_HIDDEN_TRANSACTION_LIST_FAIL("Error updating list of hidden transactions in database"),
    UPDATE_ACCOUNT_BALANCE_FAIL("Error updating account balance in database"),
    UPDATE_GOAL_FAIL("Error updating goal in database"),
    DELETE_GOAL_FAIL("Error deleting goal from database"),
    // Loading
    LOAD_ACCOUNT_TYPES_FAIL("Error loading account types from database"),
    LOAD_ACCOUNTS_FAIL("Error loading accounts from database"),
    LOAD_TRANSACTIONS_FAIL("Error loading transactions from database"),
    LOAD_GOALS_FAIL("Error loading goals from database"),
    // Finding
    GET_ACCOUNT_TYPE_ID_FAIL("Error finding account type id in database"),
    GET_ACCOUNT_ID_FAIL("Error finding account id in database"),
    GET_TRANSACTION_ID_FAIL("Error finding transaction id in database"),
    GET_CATEGORY_ID_FAIL("Error finding category id in database"),
    GET_ACCOUNT_BALANCE_FAIL("Error finding account balance in database"),

    /**
     * Misc
     */
    NET_WORTH_FAIL("Error calculating Net Worth");

    private final String message;

    FinanceError(String message) {
        this.message = message;
    }

    /**
     * Simply returns the Error message associated with the Error
     *
     * @return The Error message that's connected to the FinanceError code
     */
    public String getMessage() {
        return message;
    }
}
