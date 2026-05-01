package mattb.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import mattb.dao.AccountDAO;
import mattb.model.Account;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class AccountControllerTest extends ApplicationTest {
    private AccountDAO mockAccountDAO;

   /* @Override
    public void start(Stage stage) throws Exception {
        mockAccountDAO = Mockito.mock(AccountDAO.class);
        HashMap<Integer, Account> accounts = new HashMap<>();
        accounts.put(0, new Account(0, "", "a"));
        accounts.put(1, new Account(0, "", "b"));
        Mockito.when(mockAccountDAO.getAllAccounts(anyBoolean())).thenReturn(accounts);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/accounts.fxml"));
        loader.setControllerFactory(type -> {
            if (type == AccountController.class) {
                AccountController controller = new AccountController();
                controller.setAccountDAO(mockAccountDAO);
                return controller;
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Parent root = loader.load();

        AccountController accountController = loader.getController();
        accountController.setAccountDAO(mockAccountDAO);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testViewHiddenTogglesButtonText() {
        verifyThat("#viewHidden", hasText("View Hidden"));

        clickOn("#viewHidden");

        verifyThat("#hideAccount", hasText("Unhide Selected"));
        verifyThat("#viewHidden", hasText("Reset View"));

        clickOn("#viewHidden");

        verifyThat("#viewHidden", hasText("View Hidden"));
        verifyThat("#hideAccount", hasText("Hide Selected"));
    }

    @Test
    void testHideAccount() {
        clickOn("#hideAccount");
        verify(mockAccountDAO, Mockito.never()).updateAccountVisibility(anyInt(), anyBoolean());

        clickOn(lookup("#accountTable")
                .lookup(".table-row-cell").nth(1) // row index 1
                .lookup(".table-cell").nth(1) // column index 2
                .queryAs(TableCell.class));

        clickOn("#hideAccount");

        verify(mockAccountDAO, Mockito.times(1)).updateAccountVisibility(eq(1), anyBoolean());
    }

    @Test
    void testAddNewAccountOpensModal() throws TimeoutException {
        clickOn("#addAccount");

        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> listTargetWindows().stream()
                .filter(w -> w instanceof Stage)
                .map(w -> (Stage) w)
                .anyMatch(s -> "Add New Account".equals(s.getTitle())));

        Stage modalStage = (Stage) window("Add New Account");

        assertNotNull(modalStage, "The modal should be open");
        assertTrue(modalStage.isShowing());

        type(KeyCode.ESCAPE);

        verify(mockAccountDAO, atLeastOnce()).getAllAccounts(anyBoolean());
    }*/
}
