package mattb.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mattb.FinanceException;
import mattb.Main;
import mattb.dao.DashboardDAO;
import mattb.dao.DashboardDAOImpl;
import mattb.model.Goal;

import java.io.IOException;
import java.net.URL;

import static mattb.FinanceError.OPEN_NEW_GOAL_MODAL_FAIL;

public class DashboardController {
    @FXML
    private ListView<Goal> goalsListView;
    @FXML
    private Label emptyStateLabel;
    @FXML
    private Label netWorth;

    private DashboardDAO dashboardDAO;

    /**
     * Initializes all FXML items for the dashboard tab
     */
    @FXML
    private void initialize() {
        dashboardDAO = new DashboardDAOImpl(Main.getConn());

        goalsListView.setCellFactory(_ -> new GoalListCellController());

        refreshList();

        netWorth.setText(Main.formatDouble(dashboardDAO.getNetWorth()));
    }

    /**
     * Refreshes the goal list
     */
    private void refreshList() {
        ObservableList<Goal> goals = dashboardDAO.getGoals();
        goalsListView.setItems(goals);

        boolean hasNoGoals = goals.isEmpty();
        emptyStateLabel.setVisible(hasNoGoals);
        emptyStateLabel.setManaged(hasNoGoals);
        goalsListView.setVisible(!hasNoGoals);
        goalsListView.setManaged(!hasNoGoals);
    }

    /**
     * Opens create new goal modal
     */
    @FXML
    private void createGoal() {
        try {
            URL resource = getClass().getResource("/mattb/controllers/add_goal.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_GOAL_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Goal");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            refreshList();
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_GOAL_MODAL_FAIL).displayAndLog();
        }
    }

    @FXML
    private void viewGoalDetails() {

    }
}
