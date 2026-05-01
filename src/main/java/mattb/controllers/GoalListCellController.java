package mattb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import mattb.FinanceException;
import mattb.model.Goal;

import java.io.IOException;

import static mattb.FinanceError.OPEN_GOALS_LIST_FAILED;

public class GoalListCellController extends ListCell<Goal> {
    private Node root;
    @FXML
    private Label goalNameAndCurrentBalance;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label goalBalanceAndAccount;

    /**
     * Initializes {@link FXML} items for the {@link Goal} {@link ListView List}
     */
    public GoalListCellController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/goal_cell.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException ignored) {
            new FinanceException(OPEN_GOALS_LIST_FAILED).displayAndLog();
        }
    }

    /**
     * Sets up a row in the {@link ListView List} based on the supplied {@link Goal}
     *
     * @param goal  The new item for the cell.
     * @param empty Whether this cell represents data from the list. If it
     *              is empty, then it does not represent any domain data, but is a cell
     *              being used to render an "empty" row.
     */
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