package mattb;

import org.springframework.stereotype.Service;

import java.util.prefs.Preferences;

/**
 * Handles connection to the {@link Preferences} API
 *
 * @author Matthew Beicke
 */
@Service
public class ConfigService {
    private final Preferences settings = Preferences.userNodeForPackage(ConfigService.class);

    private final String NUM_TRANSACTIONS_KEY = "num_transactions";
    private final String NUM_ACCOUNTS_KEY = "num_accounts";
    private final String DARK_MODE_KEY = "dark_mode";

    /**
     * Saves all inputted settings to the Registry via the {@link Preferences} API
     *
     * @param numTransactions Number of transactions per page
     * @param numAccounts     Number of accounts per page
     * @param darkMode        Whether using dark mode
     */
    public void save(int numTransactions, int numAccounts, boolean darkMode) {
        settings.putInt(NUM_TRANSACTIONS_KEY, numTransactions);
        settings.putInt(NUM_ACCOUNTS_KEY, numAccounts);
        settings.putBoolean(DARK_MODE_KEY, darkMode);
    }

    /**
     * Gets the number of transactions per page from the {@link Preferences} API
     *
     * @return Whatever is stored in the Registry via the {@link Preferences} API or {@code 25} as a default
     */
    public int getNumTransactions() {
        return settings.getInt(NUM_TRANSACTIONS_KEY, 25);
    }

    /**
     * Gets the number of accounts per page from the {@link Preferences} API
     *
     * @return Whatever is stored in the Registry via the {@link Preferences} API or {@code 25} as a default
     */
    public int getNumAccounts() {
        return settings.getInt(NUM_ACCOUNTS_KEY, 25);
    }

    /**
     * Gets the dark mode status from the {@link Preferences} API
     *
     * @return Whatever is stored in the Registry via the {@link Preferences} API or {@code false} (light theme) as a default
     */
    public boolean getDarkMode() {
        return settings.getBoolean(DARK_MODE_KEY, false);
    }
}
