package se.lu.ics.controllers;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.lu.ics.data.ProductDAO;
import se.lu.ics.data.SupplierDAO;
import se.lu.ics.models.Category;
import se.lu.ics.models.Product;
import se.lu.ics.models.Supplier;

public class SupplierController {
    // supplier
    @FXML
    private TableView<Supplier> tableViewSupplier;
    @FXML
    private TableColumn<Supplier, String> tableColumnSupplierId;
    @FXML
    private TableColumn<Supplier, String> tableColumnSupplierName;
    @FXML
    private TableColumn<Supplier, String> tableColumnSupplierAddress;
    @FXML
    private TableColumn<Supplier, String> tableColumnSupplierEmail;

    // supplier - add
    @FXML
    private TextField textFieldSupplierAddName;
    @FXML
    private TextField textFieldSupplierAddEmail;
    @FXML
    private TextField textFieldSupplierAddAddress;
    @FXML
    private AnchorPane anchorPaneSupplierAdd;
    @FXML
    private Text textSupplierAddSupplierError;
    @FXML
    private Button buttonSupplierAdd;

    // supplier - update
    @FXML
    private AnchorPane anchorPaneSupplierUpdate;
    @FXML
    private TextField textFieldSupplierUpdateName;
    @FXML
    private TextField textFieldSupplierUpdateEmail;
    @FXML
    private TextField textFieldSupplierUpdateAddress;
    @FXML
    private Button buttonSupplierUpdate;
    @FXML
    private Text textSupplierUpdateSupplierError;

    // supplier - remove
    @FXML
    private Button buttonSupplierRemove;

    // supplier - show products
    @FXML
    private TableView<Product> tableViewSupplierShowProducts;
    @FXML
    private TableColumn<Product, String> tableColumnSupplierShowProductsId;
    @FXML
    private TableColumn<Product, String> tableColumnSupplierShowProductsName;
    @FXML
    private TableColumn<Product, Category> tableColumnSupplierShowProductsCategory;

    // supplier - search
    @FXML
    private TextField textFieldSupplierSearch;

    public void initialize() {

        // hide pop ups
        anchorPaneSupplierAdd.setVisible(false);
        anchorPaneSupplierUpdate.setVisible(false);

        // disable buttons
        buttonSupplierUpdate.disableProperty().set(true);
        buttonSupplierRemove.disableProperty().set(true);

        // conntect TableColumns - Supplier
        tableColumnSupplierId.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierId"));
        tableColumnSupplierName.setCellValueFactory(new PropertyValueFactory<Supplier, String>("name"));
        tableColumnSupplierAddress.setCellValueFactory(new PropertyValueFactory<Supplier, String>("address"));
        tableColumnSupplierEmail.setCellValueFactory(new PropertyValueFactory<Supplier, String>("email"));

        // connect TableColumns - SupplierShowProducts
        tableColumnSupplierShowProductsId.setCellValueFactory(new PropertyValueFactory<Product, String>("productId"));
        tableColumnSupplierShowProductsName
                .setCellValueFactory(new PropertyValueFactory<Product, String>("productName"));
        tableColumnSupplierShowProductsCategory
                .setCellValueFactory(new PropertyValueFactory<Product, Category>("category"));

        // add data to tableView
        tableViewSupplier.getItems().clear();
        tableViewSupplier.getItems().addAll(SupplierDAO.getSuppliers());

        tableViewSupplierShowProducts.getItems().clear();

        setDefaultState();

        // tableViewSupplier - set up listener
        tableViewSupplier.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                buttonSupplierUpdate.disableProperty().set(false);
                buttonSupplierRemove.disableProperty().set(false);

                tableViewSupplierShowProducts.getItems().clear();

                for (Product product : ProductDAO.getProducts()) {
                    if (product.getSupplier() == tableViewSupplier.getSelectionModel().selectedItemProperty().get()) {
                        tableViewSupplierShowProducts.getItems().add(product);
                    }
                }
            }
        });
    }

    private void disableButtonsAndSearch() {
        buttonSupplierAdd.disableProperty().set(true);
        buttonSupplierUpdate.disableProperty().set(true);
        buttonSupplierRemove.disableProperty().set(true);
        textFieldSupplierSearch.disableProperty().set(true);
        tableViewSupplier.disableProperty().set(true);
        tableViewSupplierShowProducts.disableProperty().set(true);
    }

    private void setDefaultState() {
        tableViewSupplier.disableProperty().set(false);
        tableViewSupplierShowProducts.disableProperty().set(false);
        buttonSupplierAdd.disableProperty().set(false);

        if (tableViewSupplier.getSelectionModel().isEmpty()) {
            buttonSupplierUpdate.disableProperty().set(true);
            buttonSupplierRemove.disableProperty().set(true);
        } else {
            buttonSupplierUpdate.disableProperty().set(false);
            buttonSupplierRemove.disableProperty().set(false);
        }
        textFieldSupplierSearch.disableProperty().set(false);
        textFieldSupplierAddName.clear();
        textFieldSupplierAddEmail.clear();
        textFieldSupplierAddAddress.clear();
        textSupplierAddSupplierError.setText("");
        textFieldSupplierUpdateName.clear();
        textFieldSupplierUpdateEmail.clear();
        textFieldSupplierUpdateAddress.clear();
        textSupplierUpdateSupplierError.setText("");

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
                alert.setContentText("This supplier is in use. Please delete all associated products first.");
                break;
        }
        alert.showAndWait();

    }

    // supplier
    public void buttonSupplierAddClicked() {
        anchorPaneSupplierAdd.setVisible(true);
        disableButtonsAndSearch();
    }

    public void buttonSupplierAddCancelClicked() {
        anchorPaneSupplierAdd.setVisible(false);
        setDefaultState();
    }

    public void buttonSupplierAddConfirmClicked() {
        if (textFieldSupplierAddName.getText().isBlank() || textFieldSupplierAddEmail.getText().isBlank()
                || textFieldSupplierAddAddress.getText().isBlank()) {
            textSupplierAddSupplierError.setText("Please fill in all fields");
            return;
        }

        try {
            SupplierDAO.addSupplier(textFieldSupplierAddName.getText(), textFieldSupplierAddEmail.getText(),
                    textFieldSupplierAddAddress.getText());

            anchorPaneSupplierAdd.setVisible(false);
            // update tableview
            tableViewSupplier.getItems().clear();
            tableViewSupplier.getItems().addAll(SupplierDAO.getSuppliers());

            setDefaultState();
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    textSupplierAddSupplierError.setText("Unable to access database. Please check your connection.");
                    break;
                case 2627:
                    textSupplierAddSupplierError.setText(
                            "A supplier with this ID already exists. This issue should be solved by trying again.");
                    break;
                case 2628:
                    textSupplierAddSupplierError
                            .setText("The name of the supplier, address or email is too long. Please try again.");
                    break;
                default:
                    textSupplierAddSupplierError
                            .setText("A database error occurred. Please try again. \nError code: " + e.getErrorCode());
            }

        }
    }

    // update supplier
    public void buttonSupplierUpdateClicked() {
        anchorPaneSupplierUpdate.setVisible(true);
        Supplier supplier = tableViewSupplier.getSelectionModel().getSelectedItem();
        textFieldSupplierUpdateName.setText(supplier.getName());
        textFieldSupplierUpdateEmail.setText(supplier.getEmail());
        textFieldSupplierUpdateAddress.setText(supplier.getAddress());
        disableButtonsAndSearch();
    }

    public void buttonSupplierUpdateConfirmClicked() {
        if (textFieldSupplierUpdateName.getText().isBlank() || textFieldSupplierUpdateEmail.getText().isBlank()
                || textFieldSupplierUpdateAddress.getText().isBlank()) {
            textSupplierUpdateSupplierError.setText("Please fill in all fields");
            return;
        }
        try {
            SupplierDAO.updateSupplier(tableViewSupplier.getSelectionModel().getSelectedItem().getSupplierId(),
                    textFieldSupplierUpdateName.getText(), textFieldSupplierUpdateEmail.getText(),
                    textFieldSupplierUpdateAddress.getText());
            anchorPaneSupplierUpdate.setVisible(false);
            // update tableview
            tableViewSupplier.getItems().clear();
            tableViewSupplier.getItems().addAll(SupplierDAO.getSuppliers());

            setDefaultState();
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    textSupplierUpdateSupplierError.setText("Unable to access database. Please check your connection.");
                    break;
                case 2628:
                    textSupplierUpdateSupplierError
                            .setText("The name of the supplier, address or email is too long. Please try again.");
                    break;
                default:
                    textSupplierUpdateSupplierError
                            .setText("A database error occurred. Please try again. \nError code: " + e.getErrorCode());
            }
        }
    }

    public void buttonSupplierUpdateCancelClicked() {
        anchorPaneSupplierUpdate.setVisible(false);
        // clear error text
        textSupplierUpdateSupplierError.setText("");
        setDefaultState();
    }

    // remove supplier
    public void buttonSupplierRemoveClicked() {

        try {
            if (tableViewSupplier.getSelectionModel().getSelectedItem().getProducts().size() == 0) {
                SupplierDAO.removeSupplier(tableViewSupplier.getSelectionModel().getSelectedItem().getSupplierId());

            } else {
                Alert alert = new Alert(AlertType.CONFIRMATION,
                        "Are you sure you want to remove this supplier? All products from this supplier will be removed as well.",
                        ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    SupplierDAO.removeAllProducts(tableViewSupplier.getSelectionModel().getSelectedItem());
                    SupplierDAO.removeSupplier(tableViewSupplier.getSelectionModel().getSelectedItem().getSupplierId());

                }
            }
            tableViewSupplier.getItems().clear();
            tableViewSupplier.getItems().addAll(SupplierDAO.getSuppliers());
            tableViewSupplierShowProducts.getItems().clear();
            setDefaultState();
        } catch (SQLException e) {
            showAlert(e.getErrorCode());
        }
    }

    // search supplier
    public void keyEnteredSearchSupplierId() {
        tableViewSupplier.getItems().clear();
        tableViewSupplier.getItems().addAll(SupplierDAO.searchSupplierId(textFieldSupplierSearch.getText()));

        tableViewSupplierShowProducts.getItems().clear();
    }

    // update tableView
    public void updateTableView() {
        tableViewSupplier.getItems().clear();
        tableViewSupplier.getItems().addAll(SupplierDAO.getSuppliers());
    }

}