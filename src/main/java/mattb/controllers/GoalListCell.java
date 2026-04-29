package mattb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import mattb.model.Goal;

import java.io.IOException;

public class GoalListCell extends ListCell<Goal> {
    private FXMLLoader loader;
    private Node root;
    @FXML
    private Label currentBalanceLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label goalBalanceLabel;

    @Override
    protected void updateItem(Goal goal, boolean empty) {
        super.updateItem(goal, empty);

        if (empty || goal == null) {
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("GoalCell.fxml"));
                loader.setController(this);
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            currentBalanceLabel.setText("$" + goal.getCurrent());
            goalBalanceLabel.setText("$" + goal.getTarget());
            progressBar.setProgress(goal.getCurrent() / goal.getTarget());

            setGraphic(root);
        }
    }
}