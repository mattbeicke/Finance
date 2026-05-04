package mattb;

import java.util.prefs.Preferences;

public class Config {
    private static final Preferences settings = Preferences.userNodeForPackage(Config.class);

    private static final String NUM_TRANSACTIONS_KEY = "num_transactions";
    private static final String NUM_ACCOUNTS_KEY = "num_accounts";
    private static final String DARK_MODE_KEY = "dark_mode";

    public static void save(int numTransactions, int numAccounts, boolean darkMode) {
        settings.putInt(NUM_TRANSACTIONS_KEY, numTransactions);
        settings.putInt(NUM_ACCOUNTS_KEY, numAccounts);
        settings.putBoolean(DARK_MODE_KEY, darkMode);
    }

    public static int getNumTransactions() {
        return settings.getInt(NUM_TRANSACTIONS_KEY, 25);
    }

    public static int getNumAccounts() {
        return settings.getInt(NUM_ACCOUNTS_KEY, 25);
    }

    public static boolean getDarkMode() {
        return settings.getBoolean(DARK_MODE_KEY, false);
    }
}
