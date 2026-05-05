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
import mattb.Main;
import mattb.model.Goal;
import mattb.service.AccountService;
import mattb.service.GoalService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static mattb.FinanceError.OPEN_NEW_GOAL_MODAL_FAIL;
import static mattb.FinanceError.OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL;

/**
 * Handles UI interactions on the {@code Dashboard} tab
 *
 * @author Matthew Beicke
 */
public class DashboardController {
    @FXML
    private ListView<Goal> goalList;
    @FXML
    private Label emptyStateLabel;
    @FXML
    private Label netWorth;

    private final ObservableList<Goal> goals = FXCollections.observableArrayList();
    private HashMap<Integer, Goal> map;

    private AccountService accountService;
    private GoalService goalService;

    /**
     * Initializes {@link FXML} items for the {@code Dashboard} tab and the {@link AccountService} and {@link GoalService}
     */
    @FXML
    private void initialize() {
        accountService = Main.getAccountService();
        goalService = Main.getGoalService();

        goalList.setCellFactory(_ -> new GoalListCellController());

        goalList.setItems(goals);
        refreshList();

        netWorth.setText(Main.formatDouble(accountService.getNetWorth()));
    }

    /**
     * Refreshes the {@link Goal} {@link ListView List}
     */
    private void refreshList() {
        map = (HashMap<Integer, Goal>) goalService.getGoals();
        goals.setAll(map.values());

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
            Parent root = loader.load();

            AddGoalController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Goal");
            Scene scene = new Scene(root);
            Main.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                Main.showNotification(true, "Goal Created");
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
        int id = goalService.getGoalIdFromMap(selected, map);
        if (id == -1) {
            Main.showNotification(false,"No goal selected");
            return;
        }

        try {
            URL resource = getClass().getResource("/mattb/controller/view_goal_details.fxml");
            if (resource == null) {
                new FinanceException(OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            ViewGoalDetailsController controller = loader.getController();

            controller.setFields(selected, id);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("View Goal Details");
            Scene scene = new Scene(root);
            Main.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                Main.showNotification(true, "Goal Updated");
            } else if (controller.isDeleteClicked()) {
                Main.showNotification(true, "Goal Deleted");
            }

            refreshList();
        } catch (IOException ignored) {
            new FinanceException(OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL).displayAndLog();
        }
    }
}
