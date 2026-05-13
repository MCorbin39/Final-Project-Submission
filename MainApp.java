import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * =========================================================
 * Personal Budget Tracker
 * Final Project Submission
 * =========================================================
 * Features:
 * - Add income and expense transactions
 * - Delete transactions
 * - Display totals and balance
 * - JavaFX TableView
 * - Pie chart visualization
 * - Error handling and validation
 * - Object-oriented design with inheritance
 * =========================================================
 */

/* =========================================================
   ABSTRACT TRANSACTION CLASS
   ========================================================= */
abstract class Transaction {

    private int id;
    private double amount;
    private LocalDate date;
    private String description;

    public Transaction(int id, double amount, LocalDate date, String description) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public abstract String getType();
}

/* =========================================================
   INCOME CLASS
   ========================================================= */
class Income extends Transaction {

    private String source;

    public Income(int id, double amount, LocalDate date,
                  String description, String source) {

        super(id, amount, date, description);
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getType() {
        return "Income";
    }
}

/* =========================================================
   EXPENSE CLASS
   ========================================================= */
class Expense extends Transaction {

    private String category;

    public Expense(int id, double amount, LocalDate date,
                   String description, String category) {

        super(id, amount, date, description);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String getType() {
        return "Expense";
    }
}

/* =========================================================
   BUDGET MANAGER CLASS
   ========================================================= */
class BudgetManager {

    private ArrayList<Transaction> transactionList;

    public BudgetManager() {
        transactionList = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactionList.add(transaction);
    }

    public void removeTransaction(int id) {

        transactionList.removeIf(t -> t.getId() == id);
    }

    public double calculateTotalIncome() {

        double total = 0;

        for (Transaction t : transactionList) {
            if (t instanceof Income) {
                total += t.getAmount();
            }
        }

        return total;
    }

    public double calculateTotalExpenses() {

        double total = 0;

        for (Transaction t : transactionList) {
            if (t instanceof Expense) {
                total += t.getAmount();
            }
        }

        return total;
    }

    public double calculateBalance() {
        return calculateTotalIncome() - calculateTotalExpenses();
    }

    public ArrayList<Transaction> getTransactionList() {
        return transactionList;
    }
}

/* =========================================================
   MAIN APPLICATION CLASS
   ========================================================= */
public class MainApp extends Application {

    private BudgetManager manager = new BudgetManager();

    private ObservableList<Transaction> data =
            FXCollections.observableArrayList();

    private int nextId = 1;

    @Override
    public void start(Stage primaryStage) {

        /* =========================
           INPUT CONTROLS
           ========================= */

        Label titleLabel = new Label("Personal Budget Tracker");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter Amount");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter Description");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Income", "Expense");
        typeBox.setPromptText("Select Type");

        TextField extraField = new TextField();
        extraField.setPromptText("Source or Category");

        Button addButton = new Button("Add Transaction");
        Button deleteButton = new Button("Delete Selected");
        Button calculateButton = new Button("Update Summary");

        /* =========================
           TABLE VIEW
           ========================= */

        TableView<Transaction> table = new TableView<>();

        TableColumn<Transaction, String> idCol =
                new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.valueOf(cell.getValue().getId())
                ));

        TableColumn<Transaction, String> typeCol =
                new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getType()
                ));

        TableColumn<Transaction, String> descCol =
                new TableColumn<>("Description");
        descCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getDescription()
                ));

        TableColumn<Transaction, String> amountCol =
                new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.format("$%.2f",
                                cell.getValue().getAmount())
                ));

        TableColumn<Transaction, String> dateCol =
                new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getDate().toString()
                ));

        table.getColumns().addAll(
                idCol,
                typeCol,
                descCol,
                amountCol,
                dateCol
        );

        table.setItems(data);
        table.setPrefWidth(500);

        /* =========================
           SUMMARY LABELS
           ========================= */

        Label incomeLabel = new Label("Total Income: $0.00");
        Label expenseLabel = new Label("Total Expenses: $0.00");
        Label balanceLabel = new Label("Balance: $0.00");

        /* =========================
           PIE CHART
           ========================= */

        PieChart chart = new PieChart();
        chart.setTitle("Budget Breakdown");

        /* =========================
           EVENT: ADD TRANSACTION
           ========================= */

        addButton.setOnAction(e -> {

            try {

                double amount =
                        Double.parseDouble(amountField.getText());

                String description =
                        descriptionField.getText().trim();

                String type =
                        typeBox.getValue();

                String extra =
                        extraField.getText().trim();

                // Validation
                if (amount <= 0) {
                    showAlert("Amount must be greater than zero.");
                    return;
                }

                if (description.isEmpty() || extra.isEmpty()) {
                    showAlert("Please fill in all fields.");
                    return;
                }

                if (type == null) {
                    showAlert("Please select Income or Expense.");
                    return;
                }

                Transaction transaction;

                if (type.equals("Income")) {

                    transaction = new Income(
                            nextId++,
                            amount,
                            LocalDate.now(),
                            description,
                            extra
                    );

                } else {

                    transaction = new Expense(
                            nextId++,
                            amount,
                            LocalDate.now(),
                            description,
                            extra
                    );
                }

                manager.addTransaction(transaction);
                data.add(transaction);

                showInfo("Transaction added successfully.");

                // Clear fields
                amountField.clear();
                descriptionField.clear();
                extraField.clear();
                typeBox.setValue(null);

            } catch (NumberFormatException ex) {

                showAlert("Please enter a valid numeric amount.");
            }
        });

        /* =========================
           EVENT: UPDATE SUMMARY
           ========================= */

        calculateButton.setOnAction(e -> {

            double income =
                    manager.calculateTotalIncome();

            double expenses =
                    manager.calculateTotalExpenses();

            double balance =
                    manager.calculateBalance();

            incomeLabel.setText(
                    String.format("Total Income: $%.2f", income)
            );

            expenseLabel.setText(
                    String.format("Total Expenses: $%.2f", expenses)
            );

            balanceLabel.setText(
                    String.format("Balance: $%.2f", balance)
            );

            // Update Pie Chart
            ObservableList<PieChart.Data> chartData =
                    FXCollections.observableArrayList(
                            new PieChart.Data("Income", income),
                            new PieChart.Data("Expenses", expenses)
                    );

            chart.setData(chartData);
        });

        /* =========================
           EVENT: DELETE TRANSACTION
           ========================= */

        deleteButton.setOnAction(e -> {

            Transaction selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected != null) {

                manager.removeTransaction(selected.getId());
                data.remove(selected);

                showInfo("Transaction deleted.");

            } else {

                showAlert("Please select a transaction to delete.");
            }
        });

        /* =========================
           LAYOUT
           ========================= */

        VBox inputBox = new VBox(
                10,
                titleLabel,
                amountField,
                descriptionField,
                typeBox,
                extraField,
                addButton,
                deleteButton,
                calculateButton
        );

        inputBox.setPadding(new Insets(15));
        inputBox.setPrefWidth(250);

        VBox summaryBox = new VBox(
                10,
                incomeLabel,
                expenseLabel,
                balanceLabel,
                chart
        );

        summaryBox.setPadding(new Insets(15));

        BorderPane root = new BorderPane();

        root.setLeft(inputBox);
        root.setCenter(table);
        root.setRight(summaryBox);

        Scene scene = new Scene(root, 1100, 500);

        primaryStage.setTitle("Personal Budget Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /* =========================================================
       ERROR ALERT METHOD
       ========================================================= */

    private void showAlert(String message) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /* =========================================================
       INFORMATION ALERT METHOD
       ========================================================= */

    private void showInfo(String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /* =========================================================
       MAIN METHOD
       ========================================================= */

    public static void main(String[] args) {
        launch(args);
    }
}