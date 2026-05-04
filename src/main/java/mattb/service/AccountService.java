package mattb.service;

import javafx.collections.ObservableList;
import mattb.model.Account;

import java.util.Map;

public interface AccountService {
    Map<Integer, Account> getPagedAccounts(boolean onHidden, int perPage, int page);

    void toggleVisibility(Account account, Map<Integer, Account> currentMap, boolean currentState);

    int getMaxPage(boolean onHidden, int perPage);

    int getAccountIdFromMap(Account account, Map<Integer, Account> map);

    ObservableList<String> getAccountTypes();

    int getTypeIdByName(String typeName);

    void saveAccount(int typeId, double balance, String name, int id, boolean isEditing);

    void saveAccountType(String type);

    double getNetWorth();

    ObservableList<String> getAccountNames();

    int getAccId(String name);

    void updateBalances(double amount, int fromAccId, int toAccId);
}