package mattb.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.dao.AccountDAO;
import mattb.model.Account;
import mattb.model.AccountResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountDAO accountDAO;

    @InjectMocks
    private AccountServiceImpl accountService;

    private final AccountResponse successResponse = new AccountResponse(true, "");
    private final AccountResponse failureResponse = new AccountResponse(false, "Please fill all required fields");


    @Test
    void testProcessAccountSuccessCreating() {
        when(accountDAO.getTypeId("test")).thenReturn(1);

        AccountResponse response = accountService.processAccount("test", "40", "My Savings", anyInt(), false);

        assertEquals(successResponse, response);
        verify(accountDAO, times(1)).insertAccount(anyInt(), anyDouble(), anyString());
    }

    @Test
    void testProcessAccountSuccessEditing() {
        when(accountDAO.getTypeId("test")).thenReturn(1);

        AccountResponse response = accountService.processAccount("test", "40", "My Savings", anyInt(), true);

        assertEquals(successResponse, response);
        verify(accountDAO, times(1)).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessAccountTypeNull() {
        AccountResponse response = accountService.processAccount(null, "40", "My Savings", 1, true);

        assertEquals(failureResponse, response);
        verify(accountDAO, never()).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessAccountTypeBlank() {
        AccountResponse response = accountService.processAccount("", "40", "My Savings", 1, true);

        assertEquals(failureResponse, response);
        verify(accountDAO, never()).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessAccountTypeDNE() {
        when(accountDAO.getTypeId("test")).thenReturn(-1);

        AccountResponse response = accountService.processAccount("test", "40", "My Savings", 1, true);

        assertEquals(failureResponse, response);
        verify(accountDAO, never()).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessBalanceBlank() {
        when(accountDAO.getTypeId("test")).thenReturn(1);

        AccountResponse response = accountService.processAccount("test", "", "My Savings", 1, true);

        assertEquals(failureResponse, response);
        verify(accountDAO, never()).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessNameBlank() {
        when(accountDAO.getTypeId("test")).thenReturn(1);

        AccountResponse response = accountService.processAccount("test", "40", "", 1, true);

        assertEquals(failureResponse, response);
        verify(accountDAO, never()).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testToggleVisibilitySuccess() {
        when(accountDAO.getAccId("test")).thenReturn(1);

        boolean response = accountService.toggleVisibility(new Account(1.0, "Savings", "test"), true);

        assertTrue(response);
        verify(accountDAO, times(1)).updateAccountVisibility(1, true);
    }

    @Test
    void testToggleVisibilityAccountNull() {
        boolean response = accountService.toggleVisibility(null, true);

        assertFalse(response);
        verify(accountDAO, never()).updateAccountVisibility(anyInt(), anyBoolean());
    }

    @Test
    void testToggleVisibilityAccountDNE() {
        when(accountDAO.getAccId("test")).thenReturn(-1);

        boolean response = accountService.toggleVisibility(new Account(1.0, "Savings", "test"), true);

        assertFalse(response);
        verify(accountDAO, never()).updateAccountVisibility(anyInt(), anyBoolean());
    }

    @Test
    void testUpdateBalances() {
        accountService.updateBalances(anyDouble(), anyInt(), anyInt());

        verify(accountDAO, times(1)).updateBalances(anyDouble(), anyInt(), anyInt());
    }

    @Test
    void testGetNetWorth() {
        when(accountDAO.getNetWorth()).thenReturn(1.0);

        double d = accountService.getNetWorth();

        assertEquals(1.0, d);
        verify(accountDAO, times(1)).getNetWorth();
    }

    @Test
    void testGetPagedAccounts() {
        ObservableList<Account> accounts = FXCollections.observableArrayList();

        when(accountDAO.getAllAccounts(anyBoolean(), anyInt(), anyInt())).thenReturn(accounts);

        ObservableList<Account> response = accountService.getPagedAccounts(anyBoolean(), anyInt(), anyInt());

        assertEquals(accounts, response);
        verify(accountDAO, times(1)).getAllAccounts(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void testGetAccountNames() {
        ObservableList<String> accounts = FXCollections.observableArrayList();

        when(accountDAO.getAccountNames()).thenReturn(accounts);

        ObservableList<String> response = accountService.getAccountNames();

        assertEquals(accounts, response);
        verify(accountDAO, times(1)).getAccountNames();
    }

    @Test
    void testGetAccountNamesExternal() {
        ObservableList<String> accounts = FXCollections.observableArrayList();

        when(accountDAO.getAccountNamesExternal()).thenReturn(accounts);

        ObservableList<String> response = accountService.getAccountNamesExternal();

        assertEquals(accounts, response);
        verify(accountDAO, times(1)).getAccountNamesExternal();
    }

    @Test
    void testGetAccIdSuccess() {
        when(accountDAO.getAccId("test")).thenReturn(1);

        int response = accountService.getAccId("test");

        assertEquals(1, response);
        verify(accountDAO, times(1)).getAccId(anyString());
    }

    @Test
    void testGetAccIdNull() {
        int response = accountService.getAccId(null);

        assertEquals(-1, response);
        verify(accountDAO, never()).getAccId(anyString());
    }

    @Test
    void testGetAccIdBlank() {
        int response = accountService.getAccId("");

        assertEquals(-1, response);
        verify(accountDAO, never()).getAccId(anyString());
    }

    @Test
    void testGetAccIdInvalid() {
        int response = accountService.getAccId("Add more via Accounts tab");

        assertEquals(-1, response);
        verify(accountDAO, never()).getAccId(anyString());
    }

    @Test
    void testGetMaxPage() {
        when(accountDAO.getAccountCount(true)).thenReturn(10);

        int response = accountService.getMaxPage(true, 5);

        assertEquals(2, response);
        verify(accountDAO, times(1)).getAccountCount(true);
    }

    @Test
    void testSaveAccountTypeSuccess() {
        boolean response = accountService.saveAccountType("type");

        assertTrue(response);
        verify(accountDAO, times(1)).saveAccountType(anyString());
    }

    @Test
    void testSaveAccountTypeFail() {
        boolean response = accountService.saveAccountType("");

        assertFalse(response);
        verify(accountDAO, never()).saveAccountType(anyString());
    }

    @Test
    void testGetAccountTypes() {
        ObservableList<String> accountTypes = FXCollections.observableArrayList();

        when(accountDAO.getAllTypes()).thenReturn(accountTypes);

        ObservableList<String> response = accountService.getAccountTypes();

        assertEquals(accountTypes, response);
        verify(accountDAO, times(1)).getAllTypes();
    }
}
