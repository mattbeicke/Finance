package mattb.service;

import javafx.collections.ObservableList;
import mattb.dao.AccountDAO;
import mattb.model.Account;

import java.util.Map;

public class AccountServiceImpl implements AccountService {
    private final AccountDAO accountDAO;

    public AccountServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public Map<Integer, Account> getPagedAccounts(boolean onHidden, int perPage, int page) {
        return accountDAO.getAllAccounts(onHidden, perPage, page);
    }

    @Override
    public int getMaxPage(boolean onHidden, int perPage) {
        int count = accountDAO.getAccountCount(onHidden);
        return (int) Math.ceil(count / (double) perPage);
    }

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

    @Override
    public void toggleVisibility(Account account, Map<Integer, Account> currentMap, boolean currentState) {
        int id = getAccountIdFromMap(account, currentMap);
        if (id != -1) {
            accountDAO.updateAccountVisibility(id, currentState);
        }
    }

    @Override
    public ObservableList<String> getAccountTypes() {
        return accountDAO.getAllTypes();
    }

    @Override
    public int getTypeIdByName(String typeName) {
        if (typeName == null || typeName.isBlank()) return -1;
        return accountDAO.getTypeId(typeName);
    }

    @Override
    public void saveAccount(int typeId, double balance, String name, int id, boolean isEditing) {
        accountDAO.saveAccount(typeId, balance, name, id, isEditing);
    }

    @Override
    public void saveAccountType(String type) {
        if (type.isBlank()) return;

        accountDAO.saveAccountType(type);
    }

    @Override
    public double getNetWorth() {
        return accountDAO.getNetWorth();
    }

    @Override
    public ObservableList<String> getAccountNames(){
        return accountDAO.getAccountNames();
    }

    @Override
    public int getAccId(String name) {
        if (name.isBlank()) return -1;

        return accountDAO.getAccId(name);
    }

    @Override
    public void updateBalances(double amount, int fromAccId, int toAccId){
        accountDAO.updateBalances(amount, fromAccId, toAccId);
    }
}