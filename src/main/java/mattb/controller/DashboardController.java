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
import mattb.dao.AccountDAO;
import mattb.dao.AccountDAOImpl;
import mattb.dao.GoalDAO;
import mattb.dao.GoalDAOImpl;
import mattb.model.Goal;

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

    private AccountDAO accountDAO;
    private GoalDAO goalDAO;

    /**
     * Initializes {@link FXML} items for the {@code Dashboard} tab and the {@code DAOs}
     */
    @FXML
    private void initialize() {
        accountDAO = new AccountDAOImpl(Main.getConn());
        goalDAO = new GoalDAOImpl(Main.getConn());

        goalList.setCellFactory(_ -> new GoalListCellController());

        goalList.setItems(goals);
        refreshList();

        netWorth.setText(Main.formatDouble(accountDAO.getNetWorth()));
    }

    /**
     * Refreshes the {@link Goal} {@link ListView List}
     */
    private void refreshList() {
        map = goalDAO.getGoals();
        goals.setAll(map.values());

        boolean hasNoGoals = goals.isEmpty();
        emptyStateLabel.setVisible(hasNoGoals);
        emptyStateLabel.setManaged(hasNoGoals);
        goalList.setVisible(!hasNoGoals);
        goalList.setManaged(!hasNoGoals);
    }

    /**
     * Gets the database id of the inputted {@link Goal}
     *
     * @param g The {@link Goal} to get the id of
     * @return The database id of the {@link Goal} or -1 if it's not found
     */
    private int getId(Goal g) {
        if (g == null) return -1;

        for (Integer i : map.keySet()) {
            if (map.get(i).equals(g)) {
                return i;
            }
        }
        return -1;
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

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Goal");
            Scene scene = new Scene(root);
            Main.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

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
        int id = getId(selected);
        if (id == -1) return;

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

            refreshList();
        } catch (IOException ignored) {
            new FinanceException(OPEN_VIEW_GOAL_DETAILS_MODAL_FAIL).displayAndLog();
        }
    }
}
