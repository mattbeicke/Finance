package mattb.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import mattb.Main;
import mattb.dao.DashboardDAO;
import mattb.dao.DashboardDAOImpl;
import mattb.model.Goal;

public class DashboardController {
    @FXML
    private Button viewDetails;
    @FXML
    private Button create;
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

        ObservableList<Goal> goals = dashboardDAO.getGoals();

        goalsListView.setItems(goals);

        boolean hasNoGoals = goals.isEmpty();
        emptyStateLabel.setVisible(hasNoGoals);
        emptyStateLabel.setManaged(hasNoGoals);
        goalsListView.setVisible(!hasNoGoals);
        goalsListView.setManaged(!hasNoGoals);

        netWorth.setText(Main.formatDouble(dashboardDAO.getNetWorth()));
    }

    @FXML
    private void createGoal() {

    }

    @FXML
    private void viewGoalDetails() {

    }
}
