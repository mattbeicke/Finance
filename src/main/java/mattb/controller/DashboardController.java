package mattb.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mattb.FinanceException;
import mattb.UIUtilities;
import mattb.model.Goal;
import mattb.service.AccountService;
import mattb.service.GoalService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

import static mattb.FinanceError.OPEN_NEW_GOAL_MODAL_FAIL;
import static mattb.FinanceError.OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL;

/**
 * Handles UI interactions on the {@code Dashboard} tab
 *
 * @author Matthew Beicke
 */
@Component
public class DashboardController {
    @FXML
    private ListView<Goal> goalList;
    @FXML
    private Label emptyStateLabel;
    @FXML
    private Label netWorth;

    private final ApplicationContext context;
    private final AccountService accountService;
    private final GoalService goalService;
    private final UIUtilities uiUtilities;

    private final ObservableList<Goal> goals = FXCollections.observableArrayList();

    /**
     * Used by Spring Boot to do dependency injection for the below items
     *
     * @param context        Used to create modals properly
     * @param accountService The connection to the {@link AccountService Account Service}
     * @param goalService    The connection to the {@link GoalService Goal Service}
     * @param uiUtilities    The connection to the {@link UIUtilities Utlities Class}
     */
    public DashboardController(ApplicationContext context, AccountService accountService, GoalService goalService, UIUtilities uiUtilities) {
        this.context = context;
        this.accountService = accountService;
        this.goalService = goalService;
        this.uiUtilities = uiUtilities;
    }

    /**
     * Initializes {@link FXML} items for the {@code Dashboard} tab
     */
    @FXML
    private void initialize() {
        goalList.setCellFactory(_ -> new GoalListCellController(context, uiUtilities));

        goalList.setItems(goals);
        refreshList();

        netWorth.setText(uiUtilities.formatDouble(accountService.getNetWorth()));
    }

    /**
     * Refreshes the {@link Goal} {@link ListView List}
     */
    private void refreshList() {
        goals.setAll(goalService.getGoals());

        boolean hasNoGoals = goals.isEmpty();
        emptyStateLabel.setVisible(hasNoGoals);
        emptyStateLabel.setManaged(hasNoGoals);
        goalList.setVisible(!hasNoGoals);
        goalList.setManaged(!hasNoGoals);
    }

    /**
     * Opens the {@code Add new Goal} modal
     */
    @FXML
    private void createGoal() {
        try {
            URL resource = getClass().getResource("/mattb/controller/add_goal.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_GOAL_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            AddGoalController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Goal");
            Scene scene = new Scene(root);
            uiUtilities.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                uiUtilities.showNotification(true, "Goal Created");
            }

            refreshList();
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_GOAL_MODAL_FAIL).displayAndLog();
        }
    }

    /**
     * Opens the {@code View Goal Details} modal
     */
    @FXML
    private void viewGoalDetails() {
        Goal selected = goalList.getSelectionModel().getSelectedItem();
        int id = goalService.getGoalId(selected);
        if (id == -1) {
            uiUtilities.showNotification(false, "No goal selected");
            return;
        }

        try {
            URL resource = getClass().getResource("/mattb/controller/view_goal_details.fxml");
            if (resource == null) {
                new FinanceException(OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            ViewGoalDetailsController controller = loader.getController();

            controller.setFields(selected, id);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("View Goal Details");
            Scene scene = new Scene(root);
            uiUtilities.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                uiUtilities.showNotification(true, "Goal Updated");
            } else if (controller.isDeleteClicked()) {
                uiUtilities.showNotification(true, "Goal Deleted");
            }

            refreshList();
        } catch (IOException ignored) {
            new FinanceException(OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL).displayAndLog();
        }
    }
}
