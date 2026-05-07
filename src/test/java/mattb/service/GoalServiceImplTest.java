package mattb.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.dao.GoalDAO;
import mattb.model.Goal;
import mattb.model.GoalResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {
    @Mock
    private GoalDAO goalDAO;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private GoalServiceImpl goalService;

    private final GoalResponse successResponse = new GoalResponse(true, "");
    private final GoalResponse failureResponse = new GoalResponse(false, "Please fill all required fields");

    @Test
    void testSaveGoalSuccess() {
        String accName = "test";
        when(accountService.getAccId(accName)).thenReturn(1);

        GoalResponse response = goalService.saveGoal(accName, "New Car", "5000.0");

        assertEquals(successResponse, response);
        verify(goalDAO, times(1)).saveGoal(1, "New Car", 5000.0);
    }

    @Test
    void testSaveGoalAccNameNull() {
        GoalResponse response = goalService.saveGoal(null, "New Car", "5000.0");

        assertEquals(failureResponse, response);
        verify(goalDAO, never()).saveGoal(anyInt(), anyString(), anyDouble());
    }

    @Test
    void testSaveGoalGoalNameNull() {
        GoalResponse response = goalService.saveGoal("null", null, "5000.0");

        assertEquals(failureResponse, response);
        verify(goalDAO, never()).saveGoal(anyInt(), anyString(), anyDouble());
    }

    @Test
    void testSaveGoalTargetNull() {
        GoalResponse response = goalService.saveGoal("null", "New Car", null);

        assertEquals(failureResponse, response);
        verify(goalDAO, never()).saveGoal(anyInt(), anyString(), anyDouble());
    }

    @Test
    void testSaveGoalAccNameDNE() {
        String accName = "test";
        when(accountService.getAccId(accName)).thenReturn(-1);

        GoalResponse response = goalService.saveGoal(accName, "New Car", "5000.0");

        assertEquals(failureResponse, response);
        verify(goalDAO, never()).saveGoal(anyInt(), anyString(), anyDouble());
    }

    @Test
    void testSaveGoalGoalNameBlank() {
        String accName = "test";
        when(accountService.getAccId(accName)).thenReturn(1);

        GoalResponse response = goalService.saveGoal(accName, "", "5000.0");

        assertEquals(failureResponse, response);
        verify(goalDAO, never()).saveGoal(anyInt(), anyString(), anyDouble());
    }

    @Test
    void testSaveGoalTargetBlank() {
        String accName = "test";
        when(accountService.getAccId(accName)).thenReturn(1);

        GoalResponse response = goalService.saveGoal(accName, "New Car", "");

        assertEquals(failureResponse, response);
        verify(goalDAO, never()).saveGoal(anyInt(), anyString(), anyDouble());
    }

    @Test
    void testUpdateGoalSuccess() {
        String name = "test";

        boolean response = goalService.updateGoal(name, "500.0", 1);

        assertTrue(response);
        verify(goalDAO, times(1)).updateGoal(name, 500.0, 1);
    }

    @Test
    void testUpdateGoalNameBlank() {
        boolean response = goalService.updateGoal("", "500.0", 1);

        assertFalse(response);
        verify(goalDAO, never()).updateGoal(anyString(), anyDouble(), anyInt());
    }

    @Test
    void testUpdateGoalTargetBlank() {
        boolean response = goalService.updateGoal("a", "", 1);

        assertFalse(response);
        verify(goalDAO, never()).updateGoal(anyString(), anyDouble(), anyInt());
    }

    @Test
    void testUpdateGoalIdBad() {
        boolean response = goalService.updateGoal("a", "a", 0);

        assertFalse(response);
        verify(goalDAO, never()).updateGoal(anyString(), anyDouble(), anyInt());
    }

    @Test
    void testDeleteGoal() {
        goalService.deleteGoal(anyInt());

        verify(goalDAO, times(1)).deleteGoal(anyInt());
    }

    @Test
    void testGetGoals() {
        ObservableList<Goal> goals = FXCollections.observableArrayList();

        when(goalDAO.getGoals()).thenReturn(goals);

        ObservableList<Goal> response = goalService.getGoals();

        assertEquals(goals, response);
    }

    @Test
    void testGetGoalByIdSuccess() {
        String accName = "test";
        when(accountService.getAccId(accName)).thenReturn(1);
        when(goalDAO.getGoalId(anyInt(), anyDouble(), anyDouble(), anyString())).thenReturn(1);

        int response = goalService.getGoalId(new Goal(0, 500.0, 5000.0, accName, "yeah"));

        assertEquals(1, response);
        verify(goalDAO, times(1)).getGoalId(1, 5000.0, 500.0, "yeah");
    }

    @Test
    void testGetGoalByIdFail() {
        String accName = "test";
        when(accountService.getAccId(accName)).thenReturn(-1);

        int response = goalService.getGoalId(new Goal(0, 500.0, 5000.0, accName, "yeah"));

        assertEquals(-1, response);
        verify(goalDAO, never()).getGoalId(anyInt(), anyDouble(), anyDouble(), anyString());
    }
}
