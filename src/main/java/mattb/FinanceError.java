package mattb;

/**
 * List of all the custom Error codes and messages. Used by {@code FinanceException}
 *
 * @author Matthew Beicke
 */
public enum FinanceError {
    // Opening Tabs/Modals/things

    /**
     * Error code for a failure to open the JavaFXApp page
     */
    OPEN_MAIN_FAILED("Failed to open JavaFXApp page"),
    /**
     * Error code for a failure to open the Dashboard tab
     */
    OPEN_DASHBOARD_TAB_FAILED("Failed to open Dashboard tab"),
    /**
     * Error code for a failure to open the Transactions tab
     */
    OPEN_TRANSACTIONS_TAB_FAILED("Failed to open Transactions tab"),
    /**
     * Error code for a failure to open the Accounts tab
     */
    OPEN_ACCOUNTS_TAB_FAILED("Failed to open Accounts tab"),
    /**
     * Error code for a failure to open the Settings tab
     */
    OPEN_SETTINGS_TAB_FAILED("Failed to open Settings tab"),
    /**
     * Error code for a failure to open the Create New Account modal
     */
    OPEN_NEW_ACCOUNT_MODAL_FAIL("Failed to open Create New Account modal"),
    /**
     * Error code for a failure to open the Create New Account Type modal
     */
    OPEN_NEW_TYPE_MODAL_FAIL("Failed to open Create New Account Type modal"),
    /**
     * Error code for a failure to open the Edit Account modal
     */
    OPEN_EDIT_ACCOUNT_MODAL_FAIL("Failed to open Edit Account modal"),
    /**
     * Error code for a failure to open the Update Balances modal
     */
    OPEN_UPDATE_BALANCE_MODAL_FAIL("Failed to open Update Balance modal"),
    /**
     * Error code for a failure to open the Create New Transaction modal
     */
    OPEN_NEW_TRANSACTION_MODAL_FAIL("Failed to open Create New Transaction modal"),
    /**
     * Error code for a failure to open the Edit Transaction modal
     */
    OPEN_EDIT_TRANSACTION_MODAL_FAIL("Failed to open Edit Transaction modal"),
    /**
     * Error code for a failure to open the Create New Goal modal
     */
    OPEN_NEW_GOAL_MODAL_FAIL("Failed to open Create New Goal modal"),
    /**
     * Error code for a failure to open the View Goal Details modal
     */
    OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL("Failed to open View Goal Details modal"),
    /**
     * Error code for a failure to open the Goals list
     */
    OPEN_GOALS_LIST_FAILED("Failed to open Goals list"),
    /**
     * Error code for a failure to open the Dark Theme stylesheet
     */
    OPEN_DARK_THEME_FAIL("Failed to open dark theme stylesheet"),

    // SQL

    /**
     * Error code for a failure to connect to the database
     */
    DATABASE_CONNECTION_FAIL("Failed to connect to database"),
    /**
     * Error code for a failure to open the schema.sql file
     */
    SCHEMA_NOT_FOUND("Database schema file not found"),
    /**
     * Error code for a failure to initialize database tables
     */
    DATABASE_CREATION_FAIL("Failed to initialize tables"),
    // Saving
    /**
     * Error code for a failure to save a new account type to the database
     */
    SAVE_ACCOUNT_TYPE_FAIL("Error saving new account type to database"),
    /**
     * Error code for a failure to save an account to the database
     */
    SAVE_ACCOUNT_FAIL("Error saving account to database"),
    /**
     * Error code for a failure to save a transaction to the database
     */
    SAVE_TRANSACTION_FAIL("Error saving transaction to database"),
    /**
     * Error code for a failure to save a new category to the database
     */
    SAVE_CATEGORY_FAIL("Error saving new category to database"),
    /**
     * Error code for a failure to save a transactions categories to the database
     */
    SAVE_TRANSACTION_CATEGORY_FAIL("Error saving transactions categories to database"),
    /**
     * Error code for a failure to save a new goal to the database
     */
    SAVE_GOAL_FAIL("Error saving goal to database"),
    // Updating
    /**
     * Error code for a failure to update the hidden account list in the database
     */
    UPDATE_HIDDEN_ACCOUNT_LIST_FAIL("Error updating list of hidden accounts in database"),
    /**
     * Error code for a failure to update the hidden transaction list in the database
     */
    UPDATE_HIDDEN_TRANSACTION_LIST_FAIL("Error updating list of hidden transactions in database"),
    /**
     * Error code for a failure to update an account balance in the database
     */
    UPDATE_ACCOUNT_BALANCE_FAIL("Error updating account balance in database"),
    /**
     * Error code for a failure to update a goal in the database
     */
    UPDATE_GOAL_FAIL("Error updating goal in database"),
    // Loading
    /**
     * Error code for a failure to load account types from the database
     */
    LOAD_ACCOUNT_TYPES_FAIL("Error loading account types from database"),
    /**
     * Error code for a failure to load accounts from the database
     */
    LOAD_ACCOUNTS_FAIL("Error loading accounts from database"),
    /**
     * Error code for a failure to load transactions from the database
     */
    LOAD_TRANSACTIONS_FAIL("Error loading transactions from database"),
    /**
     * Error code for a failure to load goals from the database
     */
    LOAD_GOALS_FAIL("Error loading goals from database"),
    // Finding
    /**
     * Error code for a failure to find an account type id in the database
     */
    GET_ACCOUNT_TYPE_ID_FAIL("Error finding account type id in database"),
    /**
     * Error code for a failure to find an account id in the database
     */
    GET_ACCOUNT_ID_FAIL("Error finding account id in database"),
    /**
     * Error code for a failure to find a transaction id in the database
     */
    GET_TRANSACTION_ID_FAIL("Error finding transaction id in database"),
    /**
     * Error code for a failure to find a category id in the database
     */
    GET_CATEGORY_ID_FAIL("Error finding category id in database"),
    /**
     * Error code for a failure to find account count from the database
     */
    GET_ACCOUNT_COUNT_FAIL("Error in finding account count in database"),
    /**
     * Error code for a failure to find transaction from the database
     */
    GET_TRANSACTION_COUNT_FAIL("Error in finding transaction count in database"),
    /**
     * Error code for a failure in calculating net
     */
    NET_WORTH_FAIL("Error calculating Net Worth"),
    // Deleting
    /**
     * Error code for a failure to clear categories
     */
    CLEAR_CATEGORIES_FAIL("Error clearing categories"),
    /**
     * Error code for a failure to save a new type to the database
     */
    DELETE_GOAL_FAIL("Error deleting goal from database"),;

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
