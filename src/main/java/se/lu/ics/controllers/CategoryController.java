package se.lu.ics.controllers;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.lu.ics.data.CategoryDAO;
import se.lu.ics.models.Category;
import se.lu.ics.models.Product;

public class CategoryController {
    @FXML
    private TableColumn<Product, String> tableColumnCategoryProdId;
    @FXML
    private TableColumn<Product, String> tableColumnCategoryProdName;
    @FXML
    private TableView<Category> tableViewCategory;
    @FXML
    private TableColumn<Category, String> tableColumnCategoryName;
    @FXML
    private TableColumn<Category, String> tableColumnCategory;
    @FXML
    private TableView<Product> tableViewCategoryProduct;
    @FXML
    private TableColumn<Product, String> tableColumnCategoryProductId;
    @FXML
    private TableColumn<Product, String> tableColumnCategoryProductName;
    @FXML
    private TableColumn<Product, Integer> tableColumnCategoryProductStock;

    // category - add
    @FXML
    private AnchorPane anchorPaneCategoryAdd;
    @FXML
    private TextField textFieldCategoryName;
    @FXML
    private Text textCategoryAddError;
    @FXML
    private Button buttonCategoryAdd;

    // category - stock
    @FXML
    private Text textCategoryTotalStock;
    @FXML
    private Text textCategoryTotalStockSum;

    public void initialize() {
        anchorPaneCategoryAdd.setVisible(false);
        // conntect TableColumn - Category
        tableColumnCategoryName.setCellValueFactory(new PropertyValueFactory<Category, String>("name"));

        // conntect TableColumn - CategoryProduct
        tableColumnCategoryProductId.setCellValueFactory(new PropertyValueFactory<Product, String>("productId"));
        tableColumnCategoryProductName.setCellValueFactory(new PropertyValueFactory<Product, String>("productName"));
        tableColumnCategoryProductStock.setCellValueFactory(new PropertyValueFactory<Product, Integer>("totalStock"));
        tableColumnCategoryName.setCellValueFactory(new PropertyValueFactory<Category, String>("name"));
        
        // add data to TableViews
        tableViewCategory.getItems().clear();
        tableViewCategory.getItems().addAll(CategoryDAO.getCategories());

        tableViewCategoryProduct.getItems().clear();

        enableBackground();


        // tableViewCategory - set up listener
        tableViewCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tableViewCategoryProduct.getItems().clear();
                tableViewCategoryProduct.getItems().addAll(newSelection.getProductList());

                // calculate stock
                int categoryStock = 0;
                for (Product product : newSelection.getProductList()) {
                    categoryStock += product.getTotalStock();
                }
                textCategoryTotalStock.setText("Total Stock For " + newSelection.getName() + ":");
                textCategoryTotalStockSum.setText(Integer.toString(categoryStock));

            }
        });

    }

    private void disableBackground() {
        tableViewCategory.setDisable(true);
        tableViewCategoryProduct.setDisable(true);
        buttonCategoryAdd.setDisable(true);

    }

    private void enableBackground() {
        tableViewCategory.setDisable(false);
        tableViewCategoryProduct.setDisable(false);
        textCategoryAddError.setText("");
        buttonCategoryAdd.setDisable(false);
        textFieldCategoryName.setText("");
    }

    // Add category
    public void buttonCategoryAddClicked() {
        anchorPaneCategoryAdd.setVisible(true);
        disableBackground();
    }

    public void buttonCategoryAddConfirmClicked() {
        if (textFieldCategoryName.getText().isBlank()) {
            textCategoryAddError.setText("Please fill a name.");
            return;
        }
        try {
            CategoryDAO.addCategory(textFieldCategoryName.getText());
            anchorPaneCategoryAdd.setVisible(false);
            tableViewCategory.getItems().clear();
            tableViewCategory.getItems().addAll(CategoryDAO.getCategories());
            enableBackground();
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    textCategoryAddError.setText("Unable to access database. Please check your connection.");
                    break;
                case 2627:
                    textCategoryAddError.setText("This category already exists. Please try again with a new name");
                    break;
                case 2628:
                    textCategoryAddError
                            .setText("The category name is too long. Please try again with a shorter name.");
                    break;
                default:
                    textCategoryAddError.setText(
                            "A database error occurred. Please try again." + "\nError code: " + e.getErrorCode());
            }
        }
    }

    public void buttonCategoryAddCancelClicked() {
        anchorPaneCategoryAdd.setVisible(false);
        enableBackground();
    }

}