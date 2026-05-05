package mattb.service;

import javafx.collections.ObservableList;
import mattb.dao.AccountDAO;
import mattb.model.Account;

import java.util.Map;

/**
 * Service Implementation for {@link Account Accounts}
 *
 * @author Matthew Beicke
 */
public class AccountServiceImpl implements AccountService {
    private final AccountDAO accountDAO;

    /**
     * Sets up DAO connection
     *
     * @param accountDAO Connection to the {@link AccountDAO}
     */
    public AccountServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Account> getPagedAccounts(boolean onHidden, int perPage, int page) {
        return accountDAO.getAllAccounts(onHidden, perPage, page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPage(boolean onHidden, int perPage) {
        int count = accountDAO.getAccountCount(onHidden);
        return (int) Math.ceil(count / (double) perPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAccountIdFromMap(Account account, Map<Integer, Account> map) {
        if (account == null || map == null) return -1;
        for (Map.Entry<Integer, Account> entry : map.entrySet()) {
            if (entry.getValue().equals(account)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean toggleVisibility(Account account, Map<Integer, Account> currentMap, boolean currentState) {
        int id = getAccountIdFromMap(account, currentMap);

        if (id != -1) {
            accountDAO.updateAccountVisibility(id, currentState);
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getAccountTypes() {
        return accountDAO.getAllTypes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTypeIdByName(String typeName) {
        if (typeName == null || typeName.isBlank()) return -1;

        return accountDAO.getTypeId(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAccount(int typeId, double balance, String name, int id, boolean isEditing) {
        if (isEditing) {
            accountDAO.updateAccount(typeId, balance, name, id);
        } else {
            accountDAO.insertAccount(typeId, balance, name);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveAccountType(String type) {
        if (type.isBlank()) return true;

        accountDAO.saveAccountType(type);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNetWorth() {
        return accountDAO.getNetWorth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getAccountNames() {
        return accountDAO.getAccountNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getAccountNamesExternal() {
        return accountDAO.getAccountNamesExternal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAccId(String name) {
        if (name == null || name.isBlank() || name.equals("Add more via Accounts tab")) return -1;

        return accountDAO.getAccId(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBalances(double amount, int fromAccId, int toAccId) {
        accountDAO.updateBalances(amount, fromAccId, toAccId);
    }
}