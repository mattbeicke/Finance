package mattb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import mattb.FinanceException;
import mattb.model.Goal;

import java.io.IOException;

import static mattb.FinanceError.OPEN_GOALS_LIST_FAILED;

public class GoalListCellController extends ListCell<Goal> {
    private FXMLLoader loader;
    private Node root;
    @FXML
    private Label goalNameAndCurrentBalance;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label goalBalanceAndAccount;


    public GoalListCellController() {
        loader = new FXMLLoader(getClass().getResource("/mattb/controllers/goal_cell.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException ignored) {
            new FinanceException(OPEN_GOALS_LIST_FAILED).displayAndLog();
        }
    }

    @Override
    protected void updateItem(Goal goal, boolean empty) {
        super.updateItem(goal, empty);

        if (empty || goal == null) {
            setGraphic(null);
        } else {
            goalNameAndCurrentBalance.setText(goal.name() + " $" + goal.current());
            goalBalanceAndAccount.setText("$" + goal.target());
            if (goal.current() <= goal.initial()) {
                progressBar.setProgress(0);
            } else {
                progressBar.setProgress((goal.current() - goal.initial()) / (goal.target() - goal.initial()));
            }

            setGraphic(root);
        }
    }
}