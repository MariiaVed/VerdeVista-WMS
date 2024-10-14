package se.lu.ics.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;

import java.nio.file.FileSystemException;
import java.sql.SQLException;
import javafx.beans.property.ReadOnlyStringWrapper;
import se.lu.ics.data.ExcelHandler;
import se.lu.ics.data.MetadataDAO;


public class MetadataController {

    @FXML
    private ComboBox<String> comboBoxMetadataSelect;
    @FXML
    private TableView<String> tableViewMetadata;
    @FXML
    private TableColumn<String, String> tableColumnMetadata;

    public void initialize(){

        comboBoxMetadataSelect.getItems().clear();
        comboBoxMetadataSelect.getItems().addAll("All tables", "Primary key", "Foreign key", "Product Columns", "Table with most rows");

        tableViewMetadata.getItems().clear();
        tableColumnMetadata.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
        
        comboBoxMetadataSelect.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            try {
                if (newSelection != null) {
                    if (newSelection.equals("All tables")) {
                        tableViewMetadata.setItems(MetadataDAO.getTables());
                    } else if (newSelection.equals("Primary key")) {
                        tableViewMetadata.setItems(MetadataDAO.getPrimaryKeys());
                    } else if (newSelection.equals("Foreign key")) {
                        tableViewMetadata.setItems(MetadataDAO.getForeignKeys());
                    } else if (newSelection.equals("Product Columns")) {
                        tableViewMetadata.setItems(MetadataDAO.getColumns());
                    } else if (newSelection.equals("Table with most rows")) {
                        tableViewMetadata.setItems(MetadataDAO.getTableWithMostRows());
                    }
    
                }
            } catch (SQLException e) {
                MainController.connectionErrorTerminateApp();
            }
        });

    }


    public void buttonOpenExcel(){

        try {
            ExcelHandler.openExcelFile();
        } catch (FileSystemException e) {
            Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("File Error");
                alert.setContentText(
                        "Excel file is already opened on this computer. Please close the file.");
                alert.showAndWait();
        } catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File Error");
            alert.setContentText(
                    "Excel file colud not be found.");
            alert.showAndWait();
            
        }
    }


}