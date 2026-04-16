package mattb.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mattb.model.Transaction;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;

public class TransactionController {
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Date> colDate;
    @FXML
    private TableColumn<Transaction, String> colFrom;
    @FXML
    private TableColumn<Transaction, String> colTo;
    @FXML
    private TableColumn<Transaction, String> colCategory;
    @FXML
    private TableColumn<Transaction, Double> colAmount;
    @FXML
    private TableColumn<Transaction, String> colMemo;
    @FXML
    private ComboBox<String> fromCombo;
    @FXML
    private ComboBox<String> toCombo;
    @FXML
    private TextField amountField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField memoField;
    @FXML
    private DatePicker datePicker;

    private ObservableList<Transaction> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (colDate != null) {
            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colFrom.setCellValueFactory(new PropertyValueFactory<>("fromAccountName"));
            colTo.setCellValueFactory(new PropertyValueFactory<>("toAccountName"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            colMemo.setCellValueFactory(new PropertyValueFactory<>("memo"));

            transactionTable.setItems(masterData);
            refreshTable();
        }

        if (fromCombo != null && toCombo != null) {
            ObservableList<String> accountNames = FXCollections.observableArrayList();

            String sql = "SELECT name FROM account";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    accountNames.add(rs.getString("name"));
                }

                fromCombo.setItems(accountNames);
                toCombo.setItems(accountNames);
            } catch (SQLException e) {
                System.err.println("Could not load accounts: " + e.getMessage());
            }

            amountField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*(\\.\\d*)?")) {
                    amountField.setText(oldVal);
                }
            });
        }
    }

    private void refreshTable() {
        HashMap<Integer, Transaction> map = new HashMap<>();
        masterData.clear();
        String url = "jdbc:sqlite:finance.db";
        String sql = """
                select t_id, date, fa.name as from_acc_name, ta.name as to_acc_name, amount, memo, cat_name from "transaction"
                left join tcat on t_id = trans
                left join category on cat = cat_id
                left join account ta on to_acc = ta.acc_id
                left join account fa on from_acc = fa.acc_id
                """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                if (map.containsKey(rs.getInt("t_id"))) {
                    map.get(rs.getInt("t_id")).setCategory(map.get(rs.getInt("t_id")).getCategory() + ", " + rs.getString("cat_name"));
                } else {
                    map.put(rs.getInt("t_id"), new Transaction(
                            rs.getString("to_acc_name"),
                            rs.getString("from_acc_name"),
                            rs.getDouble("amount"),
                            rs.getString("cat_name"),
                            rs.getString("memo"),
                            new java.util.Date(1000L * rs.getInt("date"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        masterData.addAll(map.values());
    }

    @FXML
    private void addNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_transaction.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: FIX BECAUSE CATEGORY COMES FROM SOME PLACE ELSE
     */
    @FXML
    private void onSave() {
        String sql = "insert into \"transaction\" (date, from_acc, to_acc, amount, memo) values (?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fromCombo.getValue());
            pstmt.setString(2, toCombo.getValue());
            pstmt.setDouble(3, Double.parseDouble(amountField.getText()));
            pstmt.setString(4, categoryField.getText());
            pstmt.setString(5, memoField.getText());
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                selectedDate = LocalDate.now();
            }
            pstmt.setInt(6, (int) selectedDate.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC));

            pstmt.executeUpdate();

            ((Stage) amountField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
