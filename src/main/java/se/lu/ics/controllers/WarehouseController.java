package se.lu.ics.controllers;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import se.lu.ics.data.CategoryDAO;
import se.lu.ics.data.ProductDAO;
import se.lu.ics.data.StockDAO;
import se.lu.ics.data.WarehouseDAO;
import se.lu.ics.models.Category;
import se.lu.ics.models.Product;
import se.lu.ics.models.Stock;
import se.lu.ics.models.Warehouse;

public class WarehouseController {
    // warehouse
    @FXML
    private TableView<Warehouse> tableViewWarehouse;
    @FXML
    private TableColumn<Warehouse, String> tableColumnWarehouseId;
    @FXML
    private TableColumn<Warehouse, String> tableColumnWarehouseAddress;
    @FXML
    private TableColumn<Warehouse, Integer> tableColumnWarehouseCapacity;

    // warehouse - add
    @FXML
    private AnchorPane anchorPaneWarehouseAdd;
    @FXML
    private TextField textFieldWarehouseAddAddress;
    @FXML
    private TextField textFieldWarehouseAddCapacity;
    @FXML
    private Button buttonWarehouseAdd;
    @FXML
    private Text textWarehouseAddWarehouseError;
    // warehouse - add product
    @FXML
    private AnchorPane anchorPaneWarehouseAddProduct;
    @FXML
    private ComboBox<Product> comboBoxWarehouseAddProduct;
    @FXML
    private TextField textFieldWarehouseAddProduct;
    @FXML
    private Text textWarehouseAddProduct;
    @FXML
    private Button buttonWarehouseAddProduct;
    @FXML
    private Text textWarehouseAddProductFilledCapacity;

    // warehouse - update stock
    @FXML
    private AnchorPane anchorPaneWarehouseUpdateStock;
    @FXML
    private TextField textFieldWarehouseUpdateStock;
    @FXML
    private Text textWarehouseUpdateStock;
    @FXML
    private Button buttonWarehouseUpdateStock;
    @FXML
    private Text textWarehouseUpdateProductFilledCapacity;

    // warehouse - delete stock
    @FXML
    private Button buttonWarehouseDeleteStock;

    // stock
    @FXML
    private TableView<Stock> tableViewStock;
    @FXML
    private TableColumn<Stock, String> tableColumnStockProductId;
    @FXML
    private TableColumn<Stock, String> tableColumnStockProductName;
    @FXML
    private TableColumn<Stock, Integer> tableColumnStockProductStock;
    @FXML
    private TableColumn<Stock, String> tableColumnStockProductSupplier;
    @FXML
    private ComboBox<String> comboBoxWarehouseCategory;
    @FXML
    private Text textTotalStock;
    @FXML
    private Text textTotalStockSum;

    // warehouse - has all products
    @FXML
    private CheckBox checkBoxWarehouseHasProducts;

    public void initialize() {
        // hide pop ups
        anchorPaneWarehouseAdd.setVisible(false);
        anchorPaneWarehouseAddProduct.setVisible(false);
        anchorPaneWarehouseUpdateStock.setVisible(false);

        // disable buttons
        buttonWarehouseDeleteStock.disableProperty().set(true);
        buttonWarehouseUpdateStock.disableProperty().set(true);
        buttonWarehouseAddProduct.disableProperty().set(true);

        // conntect TableColumn - Warehouse
        tableColumnWarehouseId.setCellValueFactory(new PropertyValueFactory<Warehouse, String>("warehouseId"));
        tableColumnWarehouseAddress.setCellValueFactory(new PropertyValueFactory<Warehouse, String>("address"));
        tableColumnWarehouseCapacity.setCellValueFactory(new PropertyValueFactory<Warehouse, Integer>("capacity"));

        // conntect TableColumn - Stock
        tableColumnStockProductName.setCellValueFactory(new PropertyValueFactory<Stock, String>("productName"));
        tableColumnStockProductId.setCellValueFactory(new PropertyValueFactory<Stock, String>("productId"));
        tableColumnStockProductStock.setCellValueFactory(new PropertyValueFactory<Stock, Integer>("stockNumber"));
        tableColumnStockProductSupplier.setCellValueFactory(new PropertyValueFactory<Stock, String>("supplierName"));

        // add data to tableView
        tableViewWarehouse.getItems().clear();
        tableViewWarehouse.getItems().addAll(WarehouseDAO.getWarehouses());

        tableViewStock.getItems().clear();
        setDefaultButtons();

        // populate comboBoxWarehouseCategory
        comboBoxWarehouseCategory.getItems().clear();
        comboBoxWarehouseCategory.getItems().add("All");
        for (Category category : CategoryDAO.getCategories()) {
            comboBoxWarehouseCategory.getItems().add(category.getName());
        }

        // tableViewWarehouse - set up listener
        tableViewWarehouse.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                buttonWarehouseAddProduct.disableProperty().set(false);
                comboBoxWarehouseCategory.disableProperty().set(false);

                tableViewStock.getItems().clear();

                // note that the comboBoxCategorySelection function populates the stock
                // tableView
                comboBoxWarehouseCategorySelection();
                buttonWarehouseDeleteStock.disableProperty().set(true);
                buttonWarehouseUpdateStock.disableProperty().set(true);
            }
        });

        // tableViewStock - set up listener
        tableViewStock.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                buttonWarehouseDeleteStock.disableProperty().set(false);
                buttonWarehouseUpdateStock.disableProperty().set(false);
            }
        });
    }

    private void setDefaultButtons() {
        tableViewWarehouse.disableProperty().set(false);
        tableViewStock.disableProperty().set(false);
        buttonWarehouseAdd.disableProperty().set(false);
        checkBoxWarehouseHasProducts.disableProperty().set(false);
        comboBoxWarehouseCategory.setValue(null);
        comboBoxWarehouseCategory.setPromptText("Select Category");
        comboBoxWarehouseAddProduct.setValue(null);
        comboBoxWarehouseAddProduct.setPromptText("Select Product");
        comboBoxWarehouseCategorySelection();

        if (!tableViewWarehouse.getSelectionModel().isEmpty()) {
            buttonWarehouseAddProduct.disableProperty().set(false);
            comboBoxWarehouseCategory.disableProperty().set(false);
        } else {
            buttonWarehouseAddProduct.disableProperty().set(true);
            comboBoxWarehouseCategory.disableProperty().set(true);
        }

        if (!tableViewStock.getSelectionModel().isEmpty()) {
            buttonWarehouseDeleteStock.disableProperty().set(false);
            buttonWarehouseUpdateStock.disableProperty().set(false);
        } else {
            buttonWarehouseDeleteStock.disableProperty().set(true);
            buttonWarehouseUpdateStock.disableProperty().set(true);
        }

        textFieldWarehouseAddAddress.clear();
        textFieldWarehouseAddCapacity.clear();
        textFieldWarehouseAddProduct.clear();
        textFieldWarehouseUpdateStock.clear();
        textWarehouseAddProduct.setText("");
        textWarehouseAddWarehouseError.setText("");
        textWarehouseUpdateStock.setText("");
        textWarehouseAddProductFilledCapacity.setText("Filled Capacity:\n- / -");
        textWarehouseUpdateProductFilledCapacity.setText("Filled Capacity:\n- / -");

    }

    private void disableButtonsAndSearch() {
        buttonWarehouseAdd.disableProperty().set(true);
        buttonWarehouseAddProduct.disableProperty().set(true);
        checkBoxWarehouseHasProducts.disableProperty().set(true);
        comboBoxWarehouseCategory.disableProperty().set(true);
        tableViewWarehouse.disableProperty().set(true);
    }

    private void disableStockButtonsAndTable() {
        buttonWarehouseDeleteStock.disableProperty().set(true);
        buttonWarehouseUpdateStock.disableProperty().set(true);
        tableViewStock.disableProperty().set(true);
    }

    public void checkBoxWarehouseHasProductsClicked() {
        if (checkBoxWarehouseHasProducts.isSelected()) {
            tableViewWarehouse.getItems().clear();
            tableViewWarehouse.getItems().addAll(WarehouseDAO.warehouseHasAllProducts());
            tableViewStock.getItems().clear();
            setDefaultButtons();
        } else {
            tableViewWarehouse.getItems().clear();
            tableViewWarehouse.getItems().addAll(WarehouseDAO.getWarehouses());
        }
    }

    public void showAlert(int errorCode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("An error occurred!");
        switch (errorCode) {
            case 0:
                alert.setContentText("Unable to access database. Please check your connection.");
                break;
            case 2627:
                alert.setContentText("Primary Key violation.");
                break;
        }
        alert.showAndWait();

    }

    // warehouse
    public void buttonWarehouseAddClicked() {
        anchorPaneWarehouseAdd.setVisible(true);
        textWarehouseAddWarehouseError.setText("");
        textFieldWarehouseAddAddress.clear();
        textFieldWarehouseAddCapacity.clear();
        disableButtonsAndSearch();
        disableStockButtonsAndTable();
    }

    public void keyEnteredTextFieldWarehouseAddCapacity() {
        textWarehouseAddWarehouseError.setText("");
        if (!textFieldWarehouseAddCapacity.getText().isEmpty()) {
            try {
                if (Integer.parseInt(textFieldWarehouseAddCapacity.getText()) < 0) {
                    textWarehouseAddWarehouseError.setText("Capacity can not be negative.");
                }
            } catch (NumberFormatException e) {
                if (!textFieldWarehouseAddCapacity.getText().isEmpty()) {
                    textWarehouseAddWarehouseError.setText("Please enter a number.");
                }
            }
        }

    }

    // add warehouse - confirmed
    public void buttonWarehouseAddConfirmClicked() {
        // make sure fields are not empty
        if (textFieldWarehouseAddAddress.getText().isBlank() || textFieldWarehouseAddCapacity.getText().isBlank()) {
            textWarehouseAddWarehouseError.setText("Please fill in all fields.");
        }

        else {
            try {
                // make sure capacity is not negative
                if (Integer.parseInt(textFieldWarehouseAddCapacity.getText()) < 0) {
                    textWarehouseAddWarehouseError.setText("Capacity can not be negative.");
                }
                // add warehouse
                else {
                    WarehouseDAO.addWarehouse(textFieldWarehouseAddAddress.getText(),
                            Integer.parseInt(textFieldWarehouseAddCapacity.getText()));

                    // hide anchorpane
                    anchorPaneWarehouseAdd.setVisible(false);

                    // update tableview
                    tableViewWarehouse.getItems().clear();
                    tableViewWarehouse.getItems().addAll(WarehouseDAO.getWarehouses());
                    setDefaultButtons();
                }
            } catch (SQLException e) {
                switch (e.getErrorCode()) {
                    case 0:
                        textWarehouseAddWarehouseError.setText("Unable to access database. Please check your connection.");
                        break;
                    default:
                        textWarehouseAddWarehouseError
                                .setText("A database error occurred. Please try again. \nError code: " + e.getErrorCode());
                }
            } catch (NumberFormatException e) {
                textWarehouseAddWarehouseError.setText("Please enter a number.");
            }
        }
    }

    public void buttonWarehouseAddCancelClicked() {
        anchorPaneWarehouseAdd.setVisible(false);
        setDefaultButtons();
    }

    public void comboBoxWarehouseCategorySelection() {
        int totalStock = 0;
        // only show stock for selected warehouse
        if (comboBoxWarehouseCategory.getValue() == null || comboBoxWarehouseCategory.getValue().equals("All")) {

            tableViewStock.getItems().clear();
            tableViewStock.getItems()
                    .addAll(StockDAO.getStockByWarehouse(tableViewWarehouse.getSelectionModel().getSelectedItem()));
            for (Stock stock : StockDAO.getStockByWarehouse(tableViewWarehouse.getSelectionModel().getSelectedItem())) {
                totalStock += stock.getStockNumber();
            }
            textTotalStock.setText("Total Warehouse Stock:");
            textTotalStockSum.setText(Integer.toString(totalStock));
        }

        // shows selected warehouse and category
        else {

            tableViewStock.getItems().clear();
            tableViewStock.getItems().addAll(StockDAO.getStockByWarehouseAndCategory(
                    tableViewWarehouse.getSelectionModel().getSelectedItem(), comboBoxWarehouseCategory.getValue()));
            for (Stock stock : StockDAO.getStockByWarehouseAndCategory(
                    tableViewWarehouse.getSelectionModel().getSelectedItem(), comboBoxWarehouseCategory.getValue())) {
                totalStock += stock.getStockNumber();
            }
            textTotalStock.setText("Total Warehouse Stock For " + comboBoxWarehouseCategory.getValue() + ":");
            textTotalStockSum.setText(Integer.toString(totalStock));
        }
    }

    // Warehouse - Add Product to stock
    public void buttonWarehouseAddProduct() {
        anchorPaneWarehouseAddProduct.setVisible(true);
        disableButtonsAndSearch();
        disableStockButtonsAndTable();
        comboBoxWarehouseAddProduct.getItems().clear();
        comboBoxWarehouseAddProduct.getItems().addAll(ProductDAO.getProducts());
        textWarehouseAddProduct.setText("");

        // comboBoxWarehouseAddProduct - set up listener
        comboBoxWarehouseAddProduct.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        // Check whether selected product is already in warehouse, if so add info text
                        textWarehouseAddProduct.setText("");
                        for (Stock stock : tableViewWarehouse.getSelectionModel().getSelectedItem().getStockList()) {
                            if (stock.getProduct().equals(newSelection)) {
                                textWarehouseAddProduct.setText("Stock of this product already exists in warehouse "
                                        + tableViewWarehouse.getSelectionModel().getSelectedItem().getWarehouseId()
                                        + ".\nNew quantity will be added to existing stock.");
                                break;
                            }
                        }

                    }
                });

    }

    // listener - add product capacity warning
    public void keyEnteredTextFieldWarehouseAddProduct() {
        // reset text
        textWarehouseAddProduct.setText("");
        textWarehouseAddProductFilledCapacity.setText("Filled Capacity:\n- / -");

        try {
            int newStock = Integer.parseInt(textFieldWarehouseAddProduct.getText());

            Warehouse selectedWarehouse = tableViewWarehouse.getSelectionModel().getSelectedItem();

            // make sure it isn't empty
            if (!textFieldWarehouseAddProduct.getText().isEmpty()) {
                textWarehouseAddProductFilledCapacity.setText("Filled Capacity:\n" +
                        (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse)) + " / "
                        + selectedWarehouse.getCapacity());

                // make sure it isn't negative
                if (newStock < 0) {
                    textWarehouseAddProduct.setText("Stock can not be negative.");
                    textWarehouseAddProduct.setFill(Color.web("#ff0000"));
                }

                // make sure it doesn't exceed max capacity
                else if (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse) > selectedWarehouse
                        .getCapacity()) {
                    textWarehouseAddProduct.setText("Stock exceeds warehouse capacity.");
                    textWarehouseAddProduct.setFill(Color.web("#ff0000"));
                }
                // warning it it exceeds 80% of max capacity
                else if (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse) > 0.8
                        * selectedWarehouse.getCapacity()) {
                    textWarehouseAddProduct.setText("WARNING: Total stock will exceed 80% of warehouse capacity.");
                    textWarehouseAddProduct.setFill(Color.BLACK);
                }
            }
        } catch (NumberFormatException e) {
            if (!textFieldWarehouseAddProduct.getText().isEmpty()) {
                textWarehouseAddProduct.setText("Please enter a number.");
            }
        }
    }

    // add product - confirm
    public void buttonWarehouseAddProductConfirmClicked() {
        if (textFieldWarehouseAddProduct.getText().isEmpty() || comboBoxWarehouseAddProduct.getValue() == null) {
            textWarehouseAddProduct.setText("Please fill in all fields.");
            return;
        }
        try {
            // make sure stock is not negative
            if (Integer.parseInt(textFieldWarehouseAddProduct.getText()) < 0) {
                textWarehouseAddProduct.setText("Stock can not be negative.");
            } else {
                boolean productExists = false;

                for (Stock stock : tableViewWarehouse.getSelectionModel().getSelectedItem().getStockList()) {

                    if (stock.getProduct().equals(comboBoxWarehouseAddProduct.getValue())) {
                        // make sure stock does not exceed warehouse capacity
                        if (Integer.parseInt(textFieldWarehouseAddProduct.getText()) + stock.getStockNumber()
                                + StockDAO.getTotalStockByWarehouse(
                                        tableViewWarehouse.getSelectionModel().getSelectedItem()) > tableViewWarehouse
                                                .getSelectionModel().getSelectedItem().getCapacity()) {
                            textWarehouseAddProduct.setText("Stock exceeds warehouse capacity. Please try again.");
                            return;
                        } else {
                            StockDAO.updateStock(stock,
                                    stock.getStockNumber() + Integer.parseInt(textFieldWarehouseAddProduct.getText()));
                            productExists = true;
                            break;
                        }
                    }
                }

                if (!productExists) {
                    StockDAO.addStock(comboBoxWarehouseAddProduct.getValue(),
                            tableViewWarehouse.getSelectionModel().getSelectedItem(),
                            Integer.parseInt(textFieldWarehouseAddProduct.getText()));
                }
                anchorPaneWarehouseAddProduct.setVisible(false);
                comboBoxWarehouseCategorySelection();
                setDefaultButtons();
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    textWarehouseAddProduct.setText("Unable to access database. Please check your connection.");
                    break;
                case 547:
                    if (e.getSQLState().contains("FK")) {
                        textWarehouseAddProduct.setText(
                                "This stock is trying to reference a product or warehouse that does not exist. It may have been deleted. Please try again.");
                    } else {
                        textWarehouseAddProduct.setText("Stock exceeds warehouse capacity. Please try again.");
                    }
                    break;
                default:
                    textWarehouseAddProduct
                            .setText("A database error occurred. Please try again. \nError code: " + e.getErrorCode());
            }
        } catch (NumberFormatException e) {
            textWarehouseAddProduct.setText("Please enter a number.");
        } catch (NullPointerException e) {
            textWarehouseAddProduct.setText("Please select a product.");
        }
    }

    public void buttonWarehouseAddProductCancelClicked() {
        anchorPaneWarehouseAddProduct.setVisible(false);
        textFieldWarehouseAddProduct.clear();
        setDefaultButtons();
    }

    // Warehouse - Delete stock
    public void buttonWarehouseDeleteStockClicked() {
        try {
            StockDAO.deleteStock(tableViewStock.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            showAlert(e.getErrorCode());
        }
        comboBoxWarehouseCategorySelection();
        setDefaultButtons();

    }

    // Warehouse - edit stock
    public void buttonWarehouseUpdateStockClicked() {
        // show anchorPane
        anchorPaneWarehouseUpdateStock.setVisible(true);

        // update text
        textFieldWarehouseUpdateStock.clear();
        textWarehouseUpdateStock.setText("");

        // disable background
        disableButtonsAndSearch();
        disableStockButtonsAndTable();
    }

    // listener - edit stock
    public void keyEnteredTextFieldWarehouseUpdateStock() {
        textWarehouseUpdateStock.setText("");
        textWarehouseUpdateProductFilledCapacity.setText("Filled Capacity:\n- / -");

        try {
            int newStock = Integer.parseInt(textFieldWarehouseUpdateStock.getText());
            int oldStock = tableViewStock.getSelectionModel().getSelectedItem().getStockNumber();
            Warehouse selectedWarehouse = tableViewWarehouse.getSelectionModel().getSelectedItem();

            // make sure text isn't empty
            if (!textFieldWarehouseUpdateStock.getText().isEmpty()) {

                // reset filled capacity calculation
                textWarehouseUpdateProductFilledCapacity.setText("Filled Capacity:\n" +
                        (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse) - oldStock) + " / "
                        + selectedWarehouse.getCapacity());

                // make sure it isn't negative
                if (newStock < 0) {
                    textWarehouseUpdateStock.setText("Stock can not be negative.");
                    textWarehouseUpdateStock.setFill(Color.web("#ff0000"));
                }

                // make sure it doesn't exceed max capacity
                else if ((newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse)
                        - oldStock) > selectedWarehouse
                                .getCapacity()) {
                    textWarehouseUpdateStock.setText("Stock exceeds warehouse capacity.");
                    textWarehouseUpdateStock.setFill(Color.web("#ff0000"));

                }
                // warning if it exceeds 80% of max capacity
                else if ((newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse) - oldStock) > 0.8
                        * selectedWarehouse.getCapacity()) {
                    textWarehouseUpdateStock.setText("WARNING: Total stock will exceed 80% of warehouse capacity.");
                    textWarehouseUpdateStock.setFill(Color.BLACK);
                }
            }
        } catch (NumberFormatException e) {
            if (!textFieldWarehouseUpdateStock.getText().isEmpty()) {
                textWarehouseUpdateStock.setText("Please enter a number.");
            }
        }
    }

    public void buttonWarehouseUpdateStockConfirmClicked() {
        if (textFieldWarehouseUpdateStock.getText().equals("")) {
            textWarehouseUpdateStock.setText("Please fill in all fields.");
        } else {
            try {
                int newStock = Integer.parseInt(textFieldWarehouseUpdateStock.getText());
                Warehouse selectedWarehouse = tableViewWarehouse.getSelectionModel().getSelectedItem();
                // make sure stock is not negative
                if (Integer.parseInt(textFieldWarehouseUpdateStock.getText()) < 0) {
                    textWarehouseUpdateStock.setText("Stock can not be negative.");
                }

                // make sure stock does not exceed warehouse capacity
                else if (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse) > selectedWarehouse
                        .getCapacity()) {
                    textWarehouseUpdateStock.setText("Stock exceeds warehouse capacity.");
                } else {
                    // update stock
                    StockDAO.updateStock(tableViewStock.getSelectionModel().getSelectedItem(), newStock);

                    // hide anchorpane
                    anchorPaneWarehouseUpdateStock.setVisible(false);

                    // update tableview
                    tableViewStock.getItems().clear();
                    tableViewStock.getItems().addAll(StockDAO.getStockByWarehouse(selectedWarehouse));
                    comboBoxWarehouseCategorySelection();
                    setDefaultButtons();
                }
            } catch (SQLException e) {
                switch (e.getErrorCode()) {
                    case 0:
                        textWarehouseUpdateStock.setText("Unable to access database. Please check your connection.");
                        break;
                    case 547:
                        if (e.getSQLState().contains("FK")) {
                            textWarehouseAddProduct.setText(
                                    "This stock is trying to reference a product or warehouse that does not exist. It may have been deleted. Please try again.");
                        } else {
                            textWarehouseUpdateStock.setText("Stock exceeds warehouse capacity. Please try again.");
                            break;
                        }
                    default:
                        textWarehouseUpdateStock
                                .setText("A database error occurred. Please try again. \nError code: "
                                        + e.getErrorCode());
                }
            } catch (NumberFormatException e) {
                textWarehouseUpdateStock.setText("Please enter a number.");

            }
        }
    }

    public void buttonWarehouseUpdateStockCancelClicked() {
        anchorPaneWarehouseUpdateStock.setVisible(false);
        setDefaultButtons();

    }

}