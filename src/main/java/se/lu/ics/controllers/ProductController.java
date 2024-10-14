package se.lu.ics.controllers;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import se.lu.ics.data.CategoryDAO;
import se.lu.ics.data.ProductDAO;
import se.lu.ics.data.StockDAO;
import se.lu.ics.data.SupplierDAO;
import se.lu.ics.data.WarehouseDAO;
import se.lu.ics.models.Category;
import se.lu.ics.models.Product;
import se.lu.ics.models.Stock;
import se.lu.ics.models.Supplier;
import se.lu.ics.models.Warehouse;

public class ProductController {
    // product
    @FXML
    private TableView<Product> tableViewProduct;
    @FXML
    private TableColumn<Product, String> tableColumnProductId;
    @FXML
    private TableColumn<Product, String> tableColumnProductName;
    @FXML
    private TableColumn<Product, Category> tableColumnProductCategory;
    @FXML
    private TableColumn<Product, Supplier> tableColumnProductSupplier;
    @FXML
    private TableColumn<Product, Integer> tableColumnProductTotalStock;
    @FXML
    private Text textTotalAmountProducts;
    @FXML
    private Text textTotalAmountProductsSum;

    // product - add
    @FXML
    private AnchorPane anchorPaneProductAdd;
    @FXML
    private TextField textFieldProductAddName;
    @FXML
    private ComboBox<Category> comboBoxProductAddCategory;
    @FXML
    private ComboBox<Supplier> comboBoxProductAddSupplier;
    @FXML
    private ComboBox<Warehouse> comboBoxProductWarehouse;
    @FXML
    private TextField textFieldProductStockAmount;
    @FXML
    private RadioButton radioButtonProductAddYes;
    @FXML
    private RadioButton radioButtonProductAddNo;
    @FXML
    private Text textProductAddWarehouse;
    @FXML
    private Text textProductAddStock;
    @FXML
    private Button buttonProductAdd;
    @FXML
    private Text textProductAddProductError;
    @FXML
    private Text textProductAddFilledCapacity;

    // product - update
    @FXML
    private AnchorPane anchorPaneProductUpdate;
    @FXML
    private TextField textFieldProductUpdateName;
    @FXML
    private ComboBox<Category> comboBoxProductUpdateCategory;
    @FXML
    private ComboBox<Supplier> comboBoxProductUpdateSupplier;
    @FXML
    private Button buttonProductUpdate;
    @FXML
    private Button buttonConfirmUpdateProduct;
    @FXML
    private Text textProductUpdateProductError;
    // product - remove
    @FXML
    private Button buttonProductRemove;
    // product - search
    @FXML
    private TextField textFieldProductSearch;
    // product - stock under 50
    @FXML
    private CheckBox checkBoxProductStockBellow50;

    // product - stock table
    @FXML
    private TableView<Stock> tableViewProductStock;
    @FXML
    private TableColumn<Stock, String> tableColumnProductStockWarehouse;
    @FXML
    private TableColumn<Stock, Integer> tableColumnProductStockAmount;

    // product - stock - add
    @FXML
    private AnchorPane anchorPaneProductStockAdd;
    @FXML
    private TextField textFieldProductStockAddAmount;
    @FXML
    private ComboBox<Warehouse> comboBoxProductStockAddWarehouse;
    @FXML
    private Text textProductStockAddError;
    @FXML
    private Button buttonProductAddToWarehouse;
    @FXML
    private Text textProductStockAddFilledCapacity;

    // product - stock - update
    @FXML
    private AnchorPane anchorPaneProductStockUpdate;
    @FXML
    private TextField textFieldProductStockUpdateAmount;
    @FXML
    private Text textProductStockUpdateError;
    @FXML
    private Button buttonProductUpdateStock;
    @FXML
    private Button buttonProductRemoveStock;
    @FXML
    private Text textProductStockUpdateFilledCapacity;

    public void initialize() {
        // hide pop ups
        anchorPaneProductAdd.setVisible(false);
        anchorPaneProductUpdate.setVisible(false);
        anchorPaneProductStockAdd.setVisible(false);
        anchorPaneProductStockUpdate.setVisible(false);

        

        // conntect TableColumn - Product
        tableColumnProductId.setCellValueFactory(new PropertyValueFactory<Product, String>("productId"));
        tableColumnProductName.setCellValueFactory(new PropertyValueFactory<Product, String>("productName"));
        tableColumnProductCategory.setCellValueFactory(new PropertyValueFactory<Product, Category>("category"));
        tableColumnProductSupplier.setCellValueFactory(new PropertyValueFactory<Product, Supplier>("supplier"));
        tableColumnProductTotalStock.setCellValueFactory(new PropertyValueFactory<Product, Integer>("totalStock"));

        // conntect TableColumn - Stock
        tableColumnProductStockWarehouse.setCellValueFactory(new PropertyValueFactory<Stock, String>("warehouse"));
        tableColumnProductStockAmount.setCellValueFactory(new PropertyValueFactory<Stock, Integer>("stockNumber"));

        // add data to tableView
        ProductDAO.sortList();
        tableViewProduct.getItems().clear();
        tableViewProduct.getItems().addAll(ProductDAO.getProducts());

        // clear tableViewProductStock so it's empty when tab is reloaded
        tableViewProductStock.getItems().clear();

        // trigger StockDAO to run static method
        StockDAO.doNothing();

        // tableViewProduct - set up listener
        tableViewProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                buttonProductAdd.disableProperty().set(false);
                buttonProductUpdate.disableProperty().set(false);
                buttonProductRemove.disableProperty().set(false);
                buttonProductAddToWarehouse.disableProperty().set(false);

                // populate stock table
                tableViewProductStock.getItems().clear();
                tableViewProductStock.getItems().addAll(newSelection.getStockList());

                setDefaultButtons();
            }
        });

        // tableViewProductStock - set up listener
        tableViewProductStock.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        buttonProductUpdateStock.disableProperty().set(false);
                        buttonProductRemoveStock.disableProperty().set(false);
                    }
                });

        // set total amount of products
        textTotalAmountProductsSum.setText(String.valueOf(ProductDAO.getProducts().size()));

        refreshAllComboBoxes();
        setDefaultButtons();
    }

    private void disableButtonsAndSearch() {
        buttonProductAdd.disableProperty().set(true);
        buttonProductRemove.disableProperty().set(true);
        buttonProductUpdate.disableProperty().set(true);
        buttonProductAddToWarehouse.disableProperty().set(true);
        buttonProductUpdateStock.disableProperty().set(true);
        buttonProductRemoveStock.disableProperty().set(true);
        textFieldProductSearch.disableProperty().set(true);
        tableViewProduct.disableProperty().set(true);
        tableViewProductStock.disableProperty().set(true);
        checkBoxProductStockBellow50.disableProperty().set(true);

    }

    private void setDefaultButtons() {
        buttonProductAdd.disableProperty().set(false);
        buttonProductRemove.disableProperty().set(true);
        buttonProductUpdate.disableProperty().set(true);
        buttonProductAddToWarehouse.disableProperty().set(true);
        textFieldProductSearch.disableProperty().set(false);
        tableViewProduct.disableProperty().set(false);
        tableViewProductStock.disableProperty().set(false);
        checkBoxProductStockBellow50.disableProperty().set(false);

        if (!tableViewProduct.getSelectionModel().isEmpty()) {
            buttonProductRemove.disableProperty().set(false);
            buttonProductUpdate.disableProperty().set(false);
            buttonProductAddToWarehouse.disableProperty().set(false);
        } else {
            buttonProductRemove.disableProperty().set(true);
            buttonProductUpdate.disableProperty().set(true);
            buttonProductAddToWarehouse.disableProperty().set(true);
        }

        if (!tableViewProductStock.getSelectionModel().isEmpty()) {
            buttonProductUpdateStock.disableProperty().set(false);
            buttonProductRemoveStock.disableProperty().set(false);
        } else {
            buttonProductUpdateStock.disableProperty().set(true);
            buttonProductRemoveStock.disableProperty().set(true);
        }

        comboBoxProductWarehouse.disableProperty().set(true);
        textFieldProductStockAmount.disableProperty().set(true);
        textProductAddWarehouse.setFill(Color.web("#9a9a9a"));
        textProductAddStock.setFill(Color.web("#9a9a9a"));

        // product add - set default
        textFieldProductAddName.clear();
        comboBoxProductAddCategory.setValue(null);
        comboBoxProductAddCategory.setPromptText("Select Category");
        comboBoxProductAddSupplier.setValue(null);
        comboBoxProductAddSupplier.setPromptText("Select Supplier");
        comboBoxProductWarehouse.setValue(null);
        comboBoxProductWarehouse.setPromptText("Select Warehouse");
        textFieldProductStockAmount.clear();
        textFieldProductStockAmount.setPromptText("Stock Amount");
        radioButtonProductAddNo.setSelected(true);
        textProductAddProductError.setText("");
        textProductAddFilledCapacity.setText("Filled Capacity:\n- / -");
        textProductAddFilledCapacity.setFill(Color.web("#9a9a9a"));

        // product update - set default
        textFieldProductUpdateName.clear();
        comboBoxProductUpdateCategory.setValue(null);
        comboBoxProductUpdateSupplier.setValue(null);
        textProductUpdateProductError.setText("");

        // product stock add - set default
        textFieldProductStockAddAmount.clear();
        textProductStockAddError.setText("");
        textProductStockAddFilledCapacity.setText("Filled Capacity:\n- / -");

        comboBoxProductStockAddWarehouse.setPromptText("Select warehouse");

        // product stock update - set default
        textProductStockUpdateError.setText("");
        textFieldProductStockUpdateAmount.clear();
        textProductStockUpdateFilledCapacity.setText("Filled Capacity:\n- / -");

    }

    // refresh all comboboxes
    public void refreshAllComboBoxes() {
        comboBoxProductAddCategory.getItems().clear();
        comboBoxProductAddCategory.getItems().addAll(CategoryDAO.getCategories());
        comboBoxProductAddSupplier.getItems().clear();
        comboBoxProductAddSupplier.getItems().addAll(SupplierDAO.getSuppliers());
        comboBoxProductWarehouse.getItems().clear();
        comboBoxProductWarehouse.getItems().addAll(WarehouseDAO.getWarehouses());
        comboBoxProductUpdateCategory.getItems().clear();
        comboBoxProductUpdateCategory.getItems().addAll(CategoryDAO.getCategories());
        comboBoxProductUpdateSupplier.getItems().clear();
        comboBoxProductUpdateSupplier.getItems().addAll(SupplierDAO.getSuppliers());
        comboBoxProductStockAddWarehouse.getItems().clear();
        comboBoxProductStockAddWarehouse.getItems().addAll(WarehouseDAO.getWarehouses());

    }

    // refresh tableViewProducts
    public void refreshTableViewProducts() {
        Product product = tableViewProduct.getSelectionModel().getSelectedItem();
        tableViewProduct.getItems().clear();
        tableViewProduct.getItems().addAll(ProductDAO.getProducts());

        try {
            tableViewProduct.getSelectionModel().select(product);
        } catch (NullPointerException e) {
            // do nothing
        }
        // update total amount of products
        textTotalAmountProductsSum.setText(String.valueOf(ProductDAO.getProducts().size()));
    }

    public void showAlert(int errorCode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database error");
        alert.setHeaderText("An error occurred!");
        switch (errorCode) {
            case 0:
                alert.setContentText("Unable to access database. Please check your connection.");
                break;
            case 547:
                alert.setContentText("This product is in use. Please remove all stock before deleting.");
                break;
            default:
                alert.setContentText("A database error occurred. Please try again.\nError code: " + errorCode);
                break;
        }
        alert.showAndWait();

    }

    // product
    public void buttonProductAddClicked() {
        anchorPaneProductAdd.setVisible(true);
        disableButtonsAndSearch();
        refreshAllComboBoxes();
    }

    public void radioButtonClicked() {
        // yes selected (add to warehouse)
        if (radioButtonProductAddYes.isSelected() == true) {
            comboBoxProductWarehouse.disableProperty().set(false);
            textFieldProductStockAmount.disableProperty().set(false);
            textProductAddWarehouse.setFill(Color.BLACK);
            textProductAddStock.setFill(Color.BLACK);
            textProductAddFilledCapacity.setFill(Color.BLACK);
        }
        // no selected (add to warehouse)
        else {
            comboBoxProductWarehouse.disableProperty().set(true);
            textFieldProductStockAmount.disableProperty().set(true);
            textProductAddWarehouse.setFill(Color.web("#9a9a9a"));
            textProductAddStock.setFill(Color.web("#9a9a9a"));
        }
    }

    // add product
    public void buttonProductAddConfirmClicked() {
        // make sure fields are filled in
        if (textFieldProductAddName.getText().isEmpty() || comboBoxProductAddCategory.getValue() == null
                || comboBoxProductAddSupplier.getValue() == null) {
            textProductAddProductError.setText("Please fill in all fields.");
            return;
        }

        try {
            // yes selected (add to warehouse)
            if (radioButtonProductAddYes.isSelected() == true) {
                // make sure warehouse is selected
                if (comboBoxProductWarehouse.getValue() == null) {
                    textProductAddProductError.setText("Please select warehouse.");
                }

                // make sure stock is entered
                else if (textFieldProductStockAmount.getText().equals("")) {
                    textProductAddProductError.setText("Please enter stock.");
                }

                // make sure stock isn't negative
                else if (Integer.parseInt(textFieldProductStockAmount.getText()) < 0) {
                    textProductAddProductError.setText("Stock cannot be negative.");
                }

                // add product and stock
                else {
                    // add product
                    ProductDAO.addProduct(textFieldProductAddName.getText(), comboBoxProductAddSupplier.getValue(),
                            comboBoxProductAddCategory.getValue());

                    // add stock (and connect to warehouse + product, done via constructor)
                    StockDAO.addStock(ProductDAO.getLatestAddedProduct(), comboBoxProductWarehouse.getValue(),
                            Integer.parseInt(textFieldProductStockAmount.getText()));

                    // hide anchorpane
                    anchorPaneProductAdd.setVisible(false);
                    setDefaultButtons();
                    refreshTableViewProducts();

                }
            }
            // no selected (add to warehouse)
            else {
                // add product
                ProductDAO.addProduct(textFieldProductAddName.getText(), comboBoxProductAddSupplier.getValue(),
                        comboBoxProductAddCategory.getValue());

                // hide anchorpane
                anchorPaneProductAdd.setVisible(false);
                setDefaultButtons();
                refreshTableViewProducts();

            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    textProductAddProductError.setText("Unable to access database. Please check your connection.");
                    break;
                case 547:
                    if (e.getSQLState().contains("FK")) {
                        textProductAddProductError.setText(
                                "This product is trying to reference a supplier or category that does not exist. It may have been deleted. Please try again.");
                    } else {
                        textProductAddProductError
                                .setText("Stock exceeds warehouse capacity, please try again." + "\n"
                                        + "Product has been added");
                        refreshTableViewProducts();
                    }
                    break;
                case 2627:
                    textProductAddProductError.setText(
                            "A product with this ID already exists. This issue should be solved by trying again.");
                    break;
                default:
                    textProductAddProductError
                            .setText("A database error occurred. Please try again. \nError code: "
                                    + e.getErrorCode());
            }

        } catch (NumberFormatException e) {
            textProductAddProductError.setText("Please enter a number.");
        } catch (NullPointerException e) {
            textProductAddProductError.setText("Please fill in all fields.");
        }
    }

    public void keyEnteredTextFieldProductStockAmount() {
        textProductAddProductError.setText("");
        textProductAddFilledCapacity.setText("Filled Capacity:\n- / -");
        textProductStockUpdateFilledCapacity.setFill(Color.web("#ff0000"));

        try {
            int newStock = Integer.parseInt(textFieldProductStockAmount.getText());
            Warehouse selectedWarehouse = comboBoxProductWarehouse.getSelectionModel().getSelectedItem();

            if (!textFieldProductStockAmount.getText().isEmpty()) {
                textProductAddFilledCapacity.setText("Filled Capacity:\n" +
                        (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse)) + " / "
                        + selectedWarehouse.getCapacity());

                if(newStock < 0){
                    textProductAddProductError.setText("Stock can not be negative.");
                    textProductAddProductError.setFill(Color.web("#ff0000"));
                }
                // make sure it doesn't exceed warehouse capacity
                else if (newStock + StockDAO
                        .getTotalStockByWarehouse(selectedWarehouse) > selectedWarehouse.getCapacity()) {
                    textProductAddProductError.setText("Total stock will exceed warehouse capacity.");
                    textProductAddProductError.setFill(Color.web("#ff0000"));
                }

                // make sure it doesn't exceed 80% of warehouse capacity
                else if (newStock + StockDAO
                        .getTotalStockByWarehouse(selectedWarehouse) > 0.8
                                * selectedWarehouse.getCapacity()) {
                    textProductAddProductError.setText("WARNING: Total stock will exceed 80% of warehouse capacity.");
                    textProductAddProductError.setFill(Color.BLACK);
                }
                textProductAddFilledCapacity.setText(
                        "Filled Capacity:\n" + (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse)) + " / "
                                + selectedWarehouse.getCapacity());
            }
        } catch (NumberFormatException e) {
            if (!textFieldProductStockAmount.getText().isEmpty()) {
                textProductAddProductError.setText("Please enter a number.");
            }
        }
    }

    public void buttonProductAddCancelClicked() {
        anchorPaneProductAdd.setVisible(false);
        setDefaultButtons();
    }

    public void keyEnteredSearchProductId() {
        tableViewProductStock.getItems().clear();
        setDefaultButtons();
        tableViewProduct.getItems().clear();
        tableViewProduct.getItems().addAll(ProductDAO.searchProductId(textFieldProductSearch.getText()));

        // so that no weird selection errors occur:
        tableViewProductStock.getItems().clear();
    }

    // update product
    public void buttonProductUpdateClicked() {
        Product product = tableViewProduct.getSelectionModel().getSelectedItem();
        anchorPaneProductUpdate.setVisible(true);
        disableButtonsAndSearch();

        textFieldProductUpdateName.setText(product.getProductName());

        refreshAllComboBoxes();
        comboBoxProductUpdateCategory.setValue(product.getCategory());
        comboBoxProductUpdateSupplier.setValue(product.getSupplier());
    }

    public void buttonProductUpdateConfirmClicked() {
        if (textFieldProductUpdateName.getText().isBlank()) {
            textProductUpdateProductError.setText("Please fill in a name.");
        }

        try {
            ProductDAO.updateProduct(tableViewProduct.getSelectionModel().getSelectedItem().getProductId(),
                    textFieldProductUpdateName.getText(), comboBoxProductUpdateSupplier.getValue(),
                    comboBoxProductUpdateCategory.getValue());
            anchorPaneProductUpdate.setVisible(false);

            refreshTableViewProducts();
            setDefaultButtons();
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    textProductUpdateProductError.setText("Unable to access database. Please check your connection.");
                    break;
                case 547:
                    textProductUpdateProductError.setText(
                            "This product is trying to reference a supplier or category that does not exist. It may have been deleted. Please try again.");
                    break;
                case 2628:
                    textProductUpdateProductError.setText("The name of the product is too long. Please try again.");
                    break;
                default:
                    textProductUpdateProductError
                            .setText("A database error occurred. Please try again. \nError code: " + e.getErrorCode());
            }
        } catch (NullPointerException e) {
            textProductUpdateProductError.setText("Please fill in all fields.");
        }
    }

    public void buttonProductUpdateCancelClicked() {
        anchorPaneProductUpdate.setVisible(false);
        setDefaultButtons();
    }

    // remove product
    public void buttonProductRemoveClicked() {
        try {
            // no stock
            if (tableViewProduct.getSelectionModel().getSelectedItem().getStockList().size() == 0) {
                ProductDAO.removeProduct(tableViewProduct.getSelectionModel().getSelectedItem().getProductId());
            }

            // has stock
            else {
                disableButtonsAndSearch();
                Alert alert = new Alert(AlertType.CONFIRMATION,
                        "Are you sure you want to remove this product? All stock will be removed as well.",
                        ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    ProductDAO.removeAllStock(tableViewProduct.getSelectionModel().getSelectedItem());
                    ProductDAO.removeProduct(tableViewProduct.getSelectionModel().getSelectedItem().getProductId());
                }
            }
        } catch (SQLException e) {
            showAlert(e.getErrorCode());
        } catch (NullPointerException e) {
            textProductUpdateProductError.setText("Please select a product.");

        }

        refreshTableViewProducts();
        tableViewProductStock.getItems().clear();
        setDefaultButtons();

    }

    // stock under 50
    public void checkBoxProductStockBellow50Clicked() {
        if (checkBoxProductStockBellow50.isSelected()) {
            tableViewProduct.getItems().clear();
            tableViewProduct.getItems().addAll(ProductDAO.productsBelow50());
            tableViewProductStock.getItems().clear();
            setDefaultButtons();
        } else {
            refreshTableViewProducts();
        }
    }

    // Product - Add stock to warehouse
    // add stock to warehouse
    public void buttonProductStockAddClicked() {
        anchorPaneProductStockAdd.setVisible(true);
        comboBoxProductStockAddWarehouse.setValue(null);
        // refreshAllComboBoxes();
        disableButtonsAndSearch();
    }

    public void comboBoxProductStockAddWarehouseSelected() {
        if (comboBoxProductStockAddWarehouse.getValue() == null) {
            textProductStockAddError.setText("");

        } else {
            keyEnteredTextFieldProductStockAddAmount();
        }
    }

    // add key entered warning to stock amount
    public void keyEnteredTextFieldProductStockAddAmount() {
        textProductStockAddError.setText("");
        textProductStockAddFilledCapacity.setText("Filled Capacity:\n- / -");

        try {

            Warehouse selectedWarehouse = comboBoxProductStockAddWarehouse.getValue();

            // check if they're adding product to warehouse that already has product
            for (Stock stock : selectedWarehouse.getStockList()) {
                if (stock.getProduct() == tableViewProduct.getSelectionModel().getSelectedItem()) {
                    textProductStockAddError.setText("Stock of this product already exists in warehouse "
                            + selectedWarehouse.getWarehouseId()
                            + ".\nNew quantity will be added to existing stock.");
                    textProductStockAddError.setFill(Color.BLACK);
                    break;
                }
            }

            int newStock = Integer.parseInt(textFieldProductStockAddAmount.getText());

            if (textFieldProductStockAddAmount.getText().isEmpty() == false) {
                if(newStock < 0){
                    textProductStockAddError.setText("Stock can not be negative.");
                    textProductStockAddError.setFill(Color.web("#ff0000"));

                } else if (newStock
                        + StockDAO.getTotalStockByWarehouse(
                                selectedWarehouse) > selectedWarehouse.getCapacity()) {
                    textProductStockAddError.setText("Total stock will exceed warehouse capacity.");
                    textProductStockAddError.setFill(Color.web("#ff0000"));
                } else if (Integer.parseInt(textFieldProductStockAddAmount.getText())
                        + StockDAO.getTotalStockByWarehouse(
                                selectedWarehouse) > 0.8
                                        * selectedWarehouse.getCapacity()) {
                    textProductStockAddError.setText("WARNING: Total stock will exceed 80% of warehouse capacity.");
                    textProductStockAddError.setFill(Color.BLACK);
                }
            }
            textProductStockAddFilledCapacity
                    .setText("Filled Capacity:\n" + (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse))
                            + " / " + selectedWarehouse.getCapacity());
        } catch (NumberFormatException e) {
            if (!textFieldProductStockAddAmount.getText().isEmpty()) {
                textProductStockAddError.setText("Please enter a number.");
            }
        } catch (NullPointerException ex) {
            textProductStockAddError.setText("Please fill in all fields.");
        }

    }

    // confirm button for stock add
    public void buttonProductStockAddConfirmClicked() {
        if (comboBoxProductStockAddWarehouse.getValue() == null || textFieldProductStockAddAmount.getText().isEmpty()) {
            textProductStockAddError.setText("Please fill in all fields.");
        } else {

            // Check whether selected product is already in warehouse, if so update stock
            try {
                if (Integer.parseInt(textFieldProductStockAddAmount.getText()) < 0) {
                    textProductStockAddError.setText("Stock cannot be negative.");
                } else {
                    boolean productExists = false;

                    for (Stock stock : comboBoxProductStockAddWarehouse.getValue().getStockList()) {
                        if (stock.getProduct().equals(tableViewProduct.getSelectionModel().getSelectedItem())) {
                            StockDAO.updateStock(stock,
                                    stock.getStockNumber()
                                            + Integer.parseInt(textFieldProductStockAddAmount.getText()));
                            productExists = true;
                            break;
                        }
                    }

                    if (!productExists) {
                        StockDAO.addStock(tableViewProduct.getSelectionModel().getSelectedItem(),
                                comboBoxProductStockAddWarehouse.getValue(),
                                Integer.parseInt(textFieldProductStockAddAmount.getText()));

                    }

                    anchorPaneProductStockAdd.setVisible(false);
                    tableViewProductStock.getItems().clear();
                    tableViewProductStock.getItems()
                            .addAll(tableViewProduct.getSelectionModel().getSelectedItem().getStockList());

                    refreshTableViewProducts();
                    setDefaultButtons();
                }
            } catch (SQLException e) {
                switch (e.getErrorCode()) {
                    case 0:
                        textProductStockAddError.setText("Unable to access database. Please check your connection.");
                        break;
                    case 547:
                        if (e.getSQLState().contains("FK")) {
                            textProductStockAddError.setText(
                                    "This stock is trying to reference a product or warehouse that does not exist. It may have been deleted. Please try again.");
                        } else {
                            textProductStockAddError.setText("Stock exceeds warehouse capacity. Please try again.");
                            // refreshTableViewProducts();
                        }
                        break;
                    case 2627:
                        textProductStockAddError.setText(
                                "A product with this ID already exists. This issue should be solved by trying again.");
                        break;
                    default:
                        textProductStockAddError.setText(
                                "A database error occurred. Please try again. \nError code: " + e.getErrorCode());
                }
            } catch (NumberFormatException e) {
                textProductStockAddError.setText("Please enter a number.");
            } catch (NullPointerException e) {
                textProductStockAddError.setText("Please fill in all fields.");
                e.printStackTrace();
            }

        }
    }

    // cancel button for stock add
    public void buttonProductStockAddCancelClicked() {
        anchorPaneProductStockAdd.setVisible(false);
        setDefaultButtons();
    }

    // Product - Update stock

    // update stock
    public void buttonProductStockUpdateClicked() {
        anchorPaneProductStockUpdate.setVisible(true);
    }

    // add key entered warning to stock amount
    public void keyEnteredTextFieldProductStockUpdateAmount() {
        textProductStockUpdateError.setText("");
        textProductStockUpdateFilledCapacity.setText("Filled Capacity:\n- / -");
        textProductStockUpdateFilledCapacity.setFill(Color.BLACK);

        try {
            int newStock = Integer.parseInt(textFieldProductStockUpdateAmount.getText());
            int oldStock = tableViewProductStock.getSelectionModel().getSelectedItem().getStockNumber();
            Warehouse selectedWarehouse = tableViewProductStock.getSelectionModel().getSelectedItem().getWarehouse();
            if (!textFieldProductStockUpdateAmount.getText().isEmpty()) {

                if(newStock < 0){
                    textProductStockUpdateError.setText("Stock can not be negative.");
                    textProductStockUpdateError.setFill(Color.web("#ff0000"));
                } else if (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse) - oldStock > selectedWarehouse
                        .getCapacity()) {
                    textProductStockUpdateError.setText("Total stock will exceed warehouse capacity.");
                    textProductStockUpdateError.setFill(Color.web("#ff0000"));
                } else if (newStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse) - oldStock > 0.8
                        * selectedWarehouse.getCapacity()) {
                    textProductStockUpdateError.setText("WARNING: Total stock will exceed 80% of warehouse capacity.");
                    textProductStockUpdateError.setFill(Color.BLACK);
                }
            }
            textProductStockUpdateFilledCapacity.setText(
                    "Filled Capacity:\n" + (newStock - oldStock + StockDAO.getTotalStockByWarehouse(selectedWarehouse))
                            + " / " + selectedWarehouse.getCapacity());
        } catch (NumberFormatException e) {
            if (!textFieldProductStockUpdateAmount.getText().isEmpty()) {
                textProductStockUpdateError.setText("Please enter a number.");
            }
        }
    }

    // confirm button for stock update
    public void buttonProductStockUpdateConfirmClicked() {
        textProductStockUpdateError.setText("");

        try {
            int newStock = Integer.parseInt(textFieldProductStockUpdateAmount.getText());
            Stock selectedStock = tableViewProductStock.getSelectionModel().getSelectedItem();

            // make sure fields are not empty
            if (textFieldProductStockUpdateAmount.getText().isEmpty()) {
                textProductStockUpdateError.setText("Please fill in all fields.");
            }

            else {
                // make sure stock is not negative
                if (newStock < 0) {
                    textProductStockUpdateError.setText("Stock cannot be negative.");
                }

                // make sure it doesn't exceed max capacity
                else if (newStock > selectedStock.getWarehouse().getCapacity()) {
                    textProductStockUpdateError.setText("Total stock exceeds warehouse capacity.");
                }
                // update
                else {
                    // update stock
                    StockDAO.updateStock(selectedStock, Integer.parseInt(textFieldProductStockUpdateAmount.getText()));

                    // hide anchorpane
                    anchorPaneProductStockUpdate.setVisible(false);

                    // update tableView
                    tableViewProductStock.getItems().clear();
                    tableViewProductStock.getItems()
                            .addAll(tableViewProduct.getSelectionModel().getSelectedItem().getStockList());

                    refreshTableViewProducts();
                    setDefaultButtons();
                }

            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    textProductStockUpdateError.setText("Unable to access database. Please check your connection.");
                    break;
                case 547:
                    textProductStockUpdateError.setText("Stock exceeds warehouse capacity. Please try again.");
                    break;
                default:
                    textProductStockUpdateError
                            .setText("A database error occurred. Please try again. \nError code: "
                                    + e.getErrorCode());
            }
        } catch (NumberFormatException e) {
            if (!textFieldProductStockUpdateAmount.getText().isEmpty()) {

                textProductStockUpdateError.setText("Please enter a number.");
            }
        } catch (NullPointerException e) {
            textProductStockUpdateError.setText("Please fill in all fields.");
        }
    }

    // cancel button for stock update
    public void buttonProductStockUpdateCancelClicked() {
        anchorPaneProductStockUpdate.setVisible(false);
        setDefaultButtons();
    }

    // Product - Remove stock
    public void buttonProductStockRemoveClicked() {
        try {
            StockDAO.deleteStock(tableViewProductStock.getSelectionModel().getSelectedItem());
            tableViewProductStock.getItems().clear();
            tableViewProductStock.getItems()
                    .addAll(tableViewProduct.getSelectionModel().getSelectedItem().getStockList());
            refreshTableViewProducts();
            setDefaultButtons();
        } catch (SQLException e) {
            showAlert(e.getErrorCode());
        }
    }
}