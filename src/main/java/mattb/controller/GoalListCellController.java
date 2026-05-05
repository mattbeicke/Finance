package mattb.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import mattb.FinanceException;
import mattb.Main;
import mattb.model.Goal;

import java.io.IOException;

import static mattb.FinanceError.OPEN_GOALS_LIST_FAILED;

/**
 * Handles setting up the UI for the {@code Goal} List
 *
 * @author Matthew Beicke
 */
public class GoalListCellController extends ListCell<Goal> {
    private Node root;
    @FXML
    private Label goalName;
    @FXML
    private Label currentBalance;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label goalTarget;
    @FXML
    private Label goalAccount;

    /**
     * Initializes {@link FXML} items for the {@link Goal} {@link ListView List}
     */
    public GoalListCellController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controller/goal_cell.fxml"));
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
            goalName.setText(goal.name());
            currentBalance.setText(Main.formatDouble(goal.current()));
            goalTarget.setText(Main.formatDouble(goal.target()));
            goalAccount.setText(goal.account());

            if (goal.current() <= goal.initial()) {
                progressBar.setProgress(0);
            } else {
                progressBar.setProgress((goal.current() - goal.initial()) / (goal.target() - goal.initial()));
            }

            setGraphic(root);
        }
    }
}