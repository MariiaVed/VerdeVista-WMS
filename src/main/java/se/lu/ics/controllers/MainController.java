package se.lu.ics.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

public class MainController {

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab productTab;
    @FXML
    private Tab warehouseTab;
    @FXML
    private Tab supplierTab;
    @FXML
    private Tab categoryTab;
    @FXML
    private Tab metadataTab;

    @FXML
    private AnchorPane root;

    @FXML
    private ProductController productController;
    @FXML
    private WarehouseController warehouseController;
    @FXML
    private SupplierController supplierController;
    @FXML
    private CategoryController categoryController;
    @FXML
    private MetadataController metadataController;
    

    public void initialize() {
    }

    public void changedTab() throws IOException {
        
        if (productTab.isSelected() == true) {
            productController.initialize();

        } else if (warehouseTab.isSelected()) {
            warehouseController.initialize();

        } else if (supplierTab.isSelected()) {
            supplierController.initialize();

        } else if (categoryTab.isSelected()) {
            categoryController.initialize();

        } else if (metadataTab.isSelected()) {
            metadataController.initialize();

        }
    }

    public static void connectionErrorTerminateApp() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Connection Error");
        alert.setHeaderText("Connection Error");
        alert.setContentText("Could not connect to the database. Please check your internet connection and try again.");
        alert.showAndWait();
        System.exit(0);
    }

    public static void credentialsErrorTerminateApp() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Connection Error");
        alert.setHeaderText("Connection Error");
        alert.setContentText(
                "Could not find the credentials used to connect to the database. Please ensure they are in the correct location and try again.");
        alert.showAndWait();
        System.exit(0);
    }

}