package mattb.service;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.dao.TransactionDAO;
import mattb.model.Transaction;
import mattb.model.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {
    @Mock
    private TransactionDAO transactionDAO;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private final TransactionResponse failureResponse = new TransactionResponse(false, "Please fill all required fields", false);

    @Test
    void testProcessTransactionInsertSuccess() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(3);

        when(transactionDAO.findOrCreateCategory("cat1")).thenReturn(4);
        when(transactionDAO.findOrCreateCategory("cat1")).thenReturn(5);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "cat1, cat2", null, 0, false);

        assertEquals(new TransactionResponse(true, "", true), response);
        verify(transactionDAO, times(1)).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, times(2)).findOrCreateCategory(anyString());
        verify(transactionDAO, times(2)).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionUpdateSuccess() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.findOrCreateCategory("cat1")).thenReturn(4);
        when(transactionDAO.findOrCreateCategory("cat1")).thenReturn(5);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "cat1, cat2", null, 6, true);

        assertEquals(new TransactionResponse(true, "", false), response);
        verify(transactionDAO, times(1)).updateTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString(), anyInt());
        verify(transactionDAO, times(2)).findOrCreateCategory(anyString());
        verify(transactionDAO, times(2)).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionFromAccIdDNE() {
        when(accountService.getAccId("ac1")).thenReturn(-1);
        when(accountService.getAccId("ac2")).thenReturn(-1);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "cat1, cat2", null, 0, false);

        assertEquals(failureResponse, response);
        verify(transactionDAO, never()).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, never()).findOrCreateCategory(anyString());
        verify(transactionDAO, never()).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionToAccIdDNE() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(-1);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "cat1, cat2", null, 0, false);

        assertEquals(failureResponse, response);
        verify(transactionDAO, never()).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, never()).findOrCreateCategory(anyString());
        verify(transactionDAO, never()).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionAmountNull() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(1);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", null, "cat1, cat2", null, 0, false);

        assertEquals(failureResponse, response);
        verify(transactionDAO, never()).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, never()).findOrCreateCategory(anyString());
        verify(transactionDAO, never()).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionAmountBlank() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(1);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "", "cat1, cat2", null, 0, false);

        assertEquals(failureResponse, response);
        verify(transactionDAO, never()).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, never()).findOrCreateCategory(anyString());
        verify(transactionDAO, never()).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionMemoNotNull() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(3);

        when(transactionDAO.findOrCreateCategory("cat1")).thenReturn(4);
        when(transactionDAO.findOrCreateCategory("cat1")).thenReturn(5);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "cat1, cat2", "null", 0, false);

        assertEquals(new TransactionResponse(true, "", true), response);
        verify(transactionDAO, times(1)).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, times(2)).findOrCreateCategory(anyString());
        verify(transactionDAO, times(2)).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionMemoBlank() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(3);

        when(transactionDAO.findOrCreateCategory("cat1")).thenReturn(4);
        when(transactionDAO.findOrCreateCategory("cat1")).thenReturn(5);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "cat1, cat2", "", 0, false);

        assertEquals(new TransactionResponse(true, "", true), response);
        verify(transactionDAO, times(1)).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, times(2)).findOrCreateCategory(anyString());
        verify(transactionDAO, times(2)).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionBadTID() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(-1);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "cat1, cat2", "", 0, false);

        assertEquals(new TransactionResponse(false, "Please try again later", false), response);
        verify(transactionDAO, times(1)).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, never()).findOrCreateCategory(anyString());
        verify(transactionDAO, never()).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionCategoryNull() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(3);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", null, null, 0, false);

        assertEquals(new TransactionResponse(true, "", true), response);
        verify(transactionDAO, times(1)).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, never()).findOrCreateCategory(anyString());
        verify(transactionDAO, never()).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionCategoryBlank() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(3);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "", null, 0, false);

        assertEquals(new TransactionResponse(true, "", true), response);
        verify(transactionDAO, times(1)).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, never()).findOrCreateCategory(anyString());
        verify(transactionDAO, never()).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionCategoriesBlank() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(3);

        when(transactionDAO.findOrCreateCategory("a")).thenReturn(4);
        when(transactionDAO.findOrCreateCategory("b")).thenReturn(5);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "a, ,b", null, 0, false);

        assertEquals(new TransactionResponse(true, "", true), response);
        verify(transactionDAO, times(1)).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, times(2)).findOrCreateCategory(anyString());
        verify(transactionDAO, times(2)).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testProcessTransactionCategoryDNE() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        when(transactionDAO.insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(3);

        when(transactionDAO.findOrCreateCategory("a")).thenReturn(-1);

        TransactionResponse response = transactionService.processTransaction(null, "ac1", "ac2", "100", "a", null, 0, false);

        assertEquals(new TransactionResponse(true, "", true), response);
        verify(transactionDAO, times(1)).insertTransaction(any(), anyInt(), anyInt(), anyDouble(), anyString());
        verify(transactionDAO, times(1)).findOrCreateCategory(anyString());
        verify(transactionDAO, never()).linkTransactionCategory(anyInt(), anyInt());
    }

    @Test
    void testUpdateBalancesSuccess() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        boolean response = transactionService.updateBalances("ac1", "ac2", "100");

        assertTrue(response);
        verify(accountService, times(1)).updateBalances(anyDouble(), anyInt(), anyInt());
    }

    @Test
    void testUpdateBalancesFromAccDNE() {
        when(accountService.getAccId("ac1")).thenReturn(-1);
        when(accountService.getAccId("ac2")).thenReturn(-1);

        boolean response = transactionService.updateBalances("ac1", "ac2", "100");

        assertFalse(response);
        verify(accountService, never()).updateBalances(anyDouble(), anyInt(), anyInt());
    }

    @Test
    void testUpdateBalancesToAccDNE() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(-1);

        boolean response = transactionService.updateBalances("ac1", "ac2", "100");

        assertFalse(response);
        verify(accountService, never()).updateBalances(anyDouble(), anyInt(), anyInt());
    }

    @Test
    void testUpdateBalancesAmountNull() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        boolean response = transactionService.updateBalances("ac1", "ac2", null);

        assertFalse(response);
        verify(accountService, never()).updateBalances(anyDouble(), anyInt(), anyInt());
    }

    @Test
    void testUpdateBalancesAmountBlank() {
        when(accountService.getAccId("ac1")).thenReturn(1);
        when(accountService.getAccId("ac2")).thenReturn(2);

        boolean response = transactionService.updateBalances("ac1", "ac2", "");

        assertFalse(response);
        verify(accountService, never()).updateBalances(anyDouble(), anyInt(), anyInt());
    }

    @Test
    void testToggleVisibilitySuccess() {
        when(transactionDAO.getTID(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(1);

        boolean response = transactionService.toggleVisibility(new Transaction("n1", "n2", 100.0, "cats, cats", "memo", null), true);

        assertTrue(response);
        verify(transactionDAO, times(1)).updateTransactionVisibility(anyInt(), anyBoolean());
    }

    @Test
    void testToggleVisibilityTransactionNull() {
        boolean response = transactionService.toggleVisibility(null, true);

        assertFalse(response);
        verify(transactionDAO, never()).updateTransactionVisibility(anyInt(), anyBoolean());
    }

    @Test
    void testToggleVisibilityTransactionDNE() {
        when(transactionDAO.getTID(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(-1);

        boolean response = transactionService.toggleVisibility(new Transaction("n1", "n2", 100.0, "cats, cats", "memo", null), true);

        assertFalse(response);
        verify(transactionDAO, never()).updateTransactionVisibility(anyInt(), anyBoolean());
    }

    @Test
    void testGetPagedTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        when(transactionDAO.getAllTransactions(anyBoolean(), anyInt(), anyInt())).thenReturn(transactions);

        ObservableList<Transaction> response = transactionService.getPagedTransactions(anyBoolean(), anyInt(), anyInt());

        assertEquals(response, transactions);
        verify(transactionDAO, times(1)).getAllTransactions(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void testGetTIDSuccess() {
        when(accountService.getAccId(anyString())).thenReturn(1);

        when(transactionDAO.getTID(any(), anyInt(), anyInt(), anyDouble(), anyString())).thenReturn(1);

        int response = transactionService.getTID(new Transaction("n1", "n2", 100.0, "cats, cats", "memo", null));

        assertEquals(1, response);
        verify(transactionDAO, times(1)).getTID(any(), anyInt(), anyInt(), anyDouble(), anyString());
    }

    @Test
    void testGetTIDToAccIdDNE() {
        when(accountService.getAccId("to")).thenReturn(-1);
        when(accountService.getAccId("from")).thenReturn(1);

        int response = transactionService.getTID(new Transaction("to", "from", 100.0, "cats, cats", "memo", null));

        assertEquals(-1, response);
        verify(transactionDAO, never()).getTID(any(), anyInt(), anyInt(), anyDouble(), anyString());
    }

    @Test
    void testGetTIDFromAccIdDNE() {
        when(accountService.getAccId("to")).thenReturn(1);
        when(accountService.getAccId("from")).thenReturn(-1);

        int response = transactionService.getTID(new Transaction("to", "from", 100.0, "cats, cats", "memo", null));

        assertEquals(-1, response);
        verify(transactionDAO, never()).getTID(any(), anyInt(), anyInt(), anyDouble(), anyString());
    }

    @Test
    void testGetMaxPage() {
        when(transactionDAO.getTransactionCount(true)).thenReturn(10);

        int response = transactionService.getMaxPage(true, 5);

        assertEquals(2, response);
        verify(transactionDAO, times(1)).getTransactionCount(anyBoolean());
    }
}
