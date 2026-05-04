package mattb;

import java.util.prefs.Preferences;

/**
 * Handles connection to the {@link Preferences} API
 *
 * @author Matthew Beicke
 */
public class Config {
    private static final Preferences settings = Preferences.userNodeForPackage(Config.class);

    private static final String NUM_TRANSACTIONS_KEY = "num_transactions";
    private static final String NUM_ACCOUNTS_KEY = "num_accounts";
    private static final String DARK_MODE_KEY = "dark_mode";

    /**
     * Saves all inputted settings
     *
     * @param numTransactions Number of transactions per page
     * @param numAccounts     Number of accounts per page
     * @param darkMode        Whether using dark mode
     */
    public static void save(int numTransactions, int numAccounts, boolean darkMode) {
        settings.putInt(NUM_TRANSACTIONS_KEY, numTransactions);
        settings.putInt(NUM_ACCOUNTS_KEY, numAccounts);
        settings.putBoolean(DARK_MODE_KEY, darkMode);
    }

    /**
     * Gets the number of transactions per page from the {@link Preferences} API
     *
     * @return Whatever is stored in the preferences or 25 (as a default)
     */
    public static int getNumTransactions() {
        return settings.getInt(NUM_TRANSACTIONS_KEY, 25);
    }

    /**
     * Gets the number of accounts per page from the {@link Preferences} API
     *
     * @return Whatever is stored in the preferences or 25 (as a default)
     */
    public static int getNumAccounts() {
        return settings.getInt(NUM_ACCOUNTS_KEY, 25);
    }

    /**
     * Gets the dark mode status from the {@link Preferences} API
     *
     * @return Whatever is stored in the preferences or false (light theme as default)
     */
    public static boolean getDarkMode() {
        return settings.getBoolean(DARK_MODE_KEY, false);
    }
}
