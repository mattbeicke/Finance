package mattb.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mattb.model.Transaction;

import java.sql.*;
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

    private ObservableList<Transaction> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("to"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colMemo.setCellValueFactory(new PropertyValueFactory<>("memo"));

        transactionTable.setItems(masterData);

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
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
        System.out.println("Add New Button Clicked!");
    }
}
