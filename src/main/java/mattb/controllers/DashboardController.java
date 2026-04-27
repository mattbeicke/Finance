package mattb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import mattb.Main;
import mattb.dao.DashboardDAO;
import mattb.dao.DashboardDAOImpl;

public class DashboardController {
    @FXML
    private Label netWorth;

    private DashboardDAO dashboardDAO;

    /**
     * Initializes all FXML items for the dashboard tab
     */
    @FXML
    private void initialize() {
        dashboardDAO = new DashboardDAOImpl(Main.getConn());

        netWorth.setText(Main.formatDouble(dashboardDAO.getNetWorth()));
    }
}
