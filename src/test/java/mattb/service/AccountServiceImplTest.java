package mattb.service;

import mattb.dao.AccountDAO;
import mattb.model.AccountResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        AccountResponse response = accountService.processAccount("test", "40", "savinger", anyInt(), false);

        assertEquals(successResponse, response);
        verify(accountDAO, times(1)).insertAccount(anyInt(), anyDouble(), anyString());
    }

    @Test
    void testProcessAccountSuccessEditing() {
        when(accountDAO.getTypeId("test")).thenReturn(1);

        AccountResponse response = accountService.processAccount("test", "40", "savinger", anyInt(), true);

        assertEquals(successResponse, response);
        verify(accountDAO, times(1)).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessAccountTypeNull() {
        AccountResponse response = accountService.processAccount(null, "40", "savinger", 1, true);

        assertEquals(failureResponse, response);
        verify(accountDAO, never()).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessAccountTypeBlank() {
        AccountResponse response = accountService.processAccount("", "40", "savinger", 1, true);

        assertEquals(failureResponse, response);
        verify(accountDAO, never()).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessAccountTypeDNE() {
        when(accountDAO.getTypeId("test")).thenReturn(-1);

        AccountResponse response = accountService.processAccount("test", "40", "savinger", 1, true);

        assertEquals(failureResponse, response);
        verify(accountDAO, never()).updateAccount(anyInt(), anyDouble(), anyString(), anyInt());
    }

    @Test
    void testProcessBalanceBlank() {
        when(accountDAO.getTypeId("test")).thenReturn(1);

        AccountResponse response = accountService.processAccount("test", "", "savinger", 1, true);

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
}
