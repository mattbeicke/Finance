package mattb.dao;

public interface DashboardDAO {
    /**
     * Sums the users non-hidden account balances
     *
     * @return Net worth of the user
     */
    double getNetWorth();
}
