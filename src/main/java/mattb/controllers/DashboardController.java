package mattb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import mattb.Main;
import mattb.dao.DashboardDAO;
import mattb.dao.DashboardDAOImpl;

public class DashboardController {
    @FXML
    private Button delete;
    @FXML
    private Button create;
    @FXML
    private ListView goalsListView;
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

        goalsListView.setCellFactory(param -> new GoalListCell());

        netWorth.setText(Main.formatDouble(dashboardDAO.getNetWorth()));
    }

    @FXML
    private void createGoal() {

    }

    @FXML
    private void deleteGoal() {

    }
}
