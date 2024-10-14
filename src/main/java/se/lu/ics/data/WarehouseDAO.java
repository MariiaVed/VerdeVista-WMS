package se.lu.ics.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.controllers.MainController;
import se.lu.ics.models.Warehouse;

public class WarehouseDAO {
    public static ObservableList<Warehouse> warehouses = FXCollections.observableArrayList();

    static {
        try {
            updateWarehousesFromDatabase();
        } catch (SQLException e) {
            MainController.connectionErrorTerminateApp();
        }

    }

    public static void updateWarehousesFromDatabase() throws SQLException {
        String query = "SELECT * FROM Warehouse";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            warehouses.clear();
            while (resultSet.next()) {
                String warehouseId = resultSet.getString("WarehouseId");
                String warehouseAddress = resultSet.getString("Address");
                int warehouseCapacity = resultSet.getInt("Capacity");

                Warehouse warehouse = new Warehouse(warehouseId, warehouseAddress, warehouseCapacity);
                warehouses.add(warehouse);
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public static Warehouse getWarehouseById(String warehouseId) {
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getWarehouseId().equals(warehouseId)) {
                return warehouse;
            }
        }
        return null;
    }

    public static void addWarehouse(String warehouseAddress, int warehouseCapacity) throws SQLException {

        String addQuery = "INSERT INTO Warehouse (WarehouseId, Address, Capacity) VALUES (?, ?, ?)";
        String idQuery = "SELECT TOP 1 CAST(SUBSTRING(WarehouseId, 2, LEN(WarehouseId)) AS INT) AS HighestId FROM Warehouse ORDER BY HighestId DESC";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement idPreparedStatement = connection.prepareStatement(idQuery);
            ResultSet idResultSet = idPreparedStatement.executeQuery();
            idResultSet.next();
            int highestId = idResultSet.getInt("HighestId") + 1;
            String idNumber = "W" + highestId;

            PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
            preparedStatement.setString(1, idNumber);
            preparedStatement.setString(2, warehouseAddress);
            preparedStatement.setInt(3, warehouseCapacity);
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                Warehouse warehouse = new Warehouse(idNumber, warehouseAddress, warehouseCapacity);
                warehouses.add(warehouse);
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static ObservableList<Warehouse> warehouseHasAllProducts() {
        ObservableList<Warehouse> warehousesWithAllProducts = FXCollections.observableArrayList();
        int productCounter = 0;

        for (Warehouse warehouse : warehouses) {
            productCounter = 0;
            for (int i = 1; i <= warehouse.getStockList().size(); i++){
                productCounter++;
            }

            if (productCounter == ProductDAO.getProducts().size()) {
                warehousesWithAllProducts.add(warehouse);
            }
        }
        return warehousesWithAllProducts;
    }

    public static ObservableList<Warehouse> getWarehouses() {
        return warehouses;
    }

    public static void setWarehouses(ObservableList<Warehouse> warehouses) {
        WarehouseDAO.warehouses = warehouses;
    }

}