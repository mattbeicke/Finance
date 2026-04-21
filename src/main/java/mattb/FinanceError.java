package mattb;

public enum FinanceError {
    /**
     * Opening Tabs/Modals
     **/
    OPEN_DASHBOARD_TAB_FAILED("Failed to open Dashboard tab"),
    OPEN_TRANSACTIONS_TAB_FAILED("Failed to open Transactions tab"),
    OPEN_ACCOUNTS_TAB_FAILED("Failed to open Accounts tab"),
    OPEN_NEW_ACCOUNT_MODAL_FAIL("Failed to open Create New Account modal"),
    OPEN_NEW_TYPE_MODAL_FAIL("Failed to open Create New Account Type modal"),
    OPEN_EDIT_ACCOUNT_MODAL_FAIL("Failed to open Edit Account modal"),
    OPEN_UPDATE_BALANCE_MODAL_FAIL("Failed to open Update Balance modal"),
    OPEN_NEW_TRANSACTION_MODAL_FAIL("Failed to open Create New Transaction modal"),
    OPEN_EDIT_TRANSACTION_MODAL_FAIL("Failed to open Edit Transaction modal"),

    /**
     * SQL
     **/
    DATABASE_CONNECTION_FAIL("Failed to connect to database"),
    // Saving
    SAVE_ACCOUNT_TYPE_FAIL("Error saving new type to database"),
    SAVE_ACCOUNT_FAIL("Error saving account to database"),
    SAVE_TRANSACTION_FAIL("Error saving transaction to database"),
    SAVE_CATEGORY_FAIL("Error saving new category to database"),
    SAVE_TRANSACTION_CATEGORY_FAIL("Error saving transactions categories to database"),
    UPDATE_HIDDEN_ACCOUNT_LIST_FAIL("Error updating list of hidden accounts in database"),
    UPDATE_HIDDEN_TRANSACTION_LIST_FAIL("Error updating list of hidden transactions in database"),
    UPDATE_ACCOUNT_BALANCE_FAIL("Error updating account balance in database"),
    // Loading
    LOAD_ACCOUNT_TYPES_FAIL("Error loading account types from database"),
    LOAD_ACCOUNTS_FAIL("Error loading accounts from database"),
    LOAD_TRANSACTIONS_FAIL("Error loading transactions from database"),
    LOAD_HIDDEN_TRANSACTIONS_FAIL("Error loading hidden transactions from database"),
    LOAD_HIDDEN_ACCOUNTS_FAIL("Error loading hidden accounts from database"),
    // Finding
    GET_ACCOUNT_TYPE_ID_FAIL("Error finding account type id in database"),
    GET_ACCOUNT_ID_FAIL("Error finding account id in database"),
    GET_TRANSACTION_ID_FAIL("Error finding transaction id in database"),
    GET_CATEGORY_ID_FAIL("Error finding category id in database");

    private final String message;

    FinanceError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
