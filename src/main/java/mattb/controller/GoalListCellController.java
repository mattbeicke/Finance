package mattb.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import mattb.FinanceException;
import mattb.UIUtilities;
import mattb.model.Goal;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

import static mattb.FinanceError.OPEN_GOALS_LIST_FAILED;

/**
 * Handles setting up the UI for the {@code Goal} List
 *
 * @author Matthew Beicke
 */
@Component
public class GoalListCellController extends ListCell<Goal> {
    private Node root;
    @FXML
    @SuppressWarnings("unused")
    private Label goalName;
    @FXML
    @SuppressWarnings("unused")
    private Label currentBalance;
    @FXML
    @SuppressWarnings("unused")
    private ProgressBar progressBar;
    @FXML
    @SuppressWarnings("unused")
    private Label goalTarget;
    @FXML
    @SuppressWarnings("unused")
    private Label goalAccount;

    private final ApplicationContext context;
    private final UIUtilities uiUtilities;

    /**
     * Initializes {@link FXML} items for the {@link Goal} {@link ListView List}
     *
     * @param context     Used to create modals properly
     * @param uiUtilities The connection to the {@link UIUtilities Utlities Class}
     */
    public GoalListCellController(ApplicationContext context, UIUtilities uiUtilities) {
        this.context = context;
        this.uiUtilities = uiUtilities;

        try {
            URL resource = getClass().getResource("/mattb/controller/goal_cell.fxml");
            FXMLLoader loader = new FXMLLoader(resource);

            loader.setControllerFactory(this.context::getBean);
            loader.setController(this);

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
            currentBalance.setText(uiUtilities.formatDouble(goal.current()));
            goalTarget.setText(uiUtilities.formatDouble(goal.target()));
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