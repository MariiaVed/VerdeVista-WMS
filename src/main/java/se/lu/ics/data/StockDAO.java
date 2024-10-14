package se.lu.ics.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.controllers.MainController;
import se.lu.ics.models.Product;
import se.lu.ics.models.Stock;
import se.lu.ics.models.Warehouse;

public class StockDAO {
    private static ObservableList<Stock> stockList = FXCollections.observableArrayList();

    static {
        try {
            updateStockFromDatabase();
        } catch (SQLException e) {
            MainController.connectionErrorTerminateApp();
        }
    }

    public static void doNothing() {

    }

    public static void updateStockFromDatabase() throws SQLException {
        String query = "SELECT * FROM StoredAt";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            stockList.clear();
            while (resultSet.next()) {
                Product product = ProductDAO.getProductById(resultSet.getString("ProductId"));
                Warehouse warehouse = WarehouseDAO.getWarehouseById(resultSet.getString("WarehouseId"));
                int stockNumber = resultSet.getInt("Stock");

                Stock stock = new Stock(product, warehouse, stockNumber);
                stockList.add(stock);
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public static void addStock(Product product, Warehouse warehouse, int stock) throws SQLException {

        String addQuery = "INSERT INTO StoredAt (ProductId, WarehouseId, Stock) VALUES (?, ?, ?)";

        try (Connection connection = ConnectionHandler.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
            preparedStatement.setString(1, product.getProductId());
            preparedStatement.setString(2, warehouse.getWarehouseId());
            preparedStatement.setInt(3, stock);
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                Stock newStock = new Stock(product, warehouse, stock);
                stockList.add(newStock);
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static void updateStock(Stock stock, int newStock) throws SQLException {

        String updateQuery = "UPDATE StoredAt SET Stock = ? WHERE ProductId = ? AND WarehouseId = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, newStock);
            preparedStatement.setString(2, stock.getProduct().getProductId());
            preparedStatement.setString(3, stock.getWarehouse().getWarehouseId());
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                stock.setStockNumber(newStock); 
            }

        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static void deleteStock(Stock stock) throws SQLException {

        String deleteQuery = "DELETE FROM StoredAt WHERE ProductId = ? AND WarehouseId = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1, stock.getProduct().getProductId());
            preparedStatement.setString(2, stock.getWarehouse().getWarehouseId());
            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                stock.getProduct().removeStock(stock);
                stock.getWarehouse().removeStock(stock);
                stockList.remove(stock);

            }

        } catch (SQLException ex) {
            throw ex;
        }

    }

    public static ObservableList<Stock> getStockByWarehouse(Warehouse warehouse) {
        ObservableList<Stock> stockByWarehouse = FXCollections.observableArrayList();
        for (Stock stock : stockList) {
            if (stock.getWarehouse().equals(warehouse)) {
                stockByWarehouse.add(stock);
            }
        }
        return stockByWarehouse;
    }

    public static ObservableList<Stock> getStockByWarehouseAndCategory(Warehouse warehouse, String categoryName) {
        ObservableList<Stock> stockByWarehouseAndCategory = FXCollections.observableArrayList();
        for (Stock stock : stockList) {
            if (stock.getWarehouse().equals(warehouse)
                    && stock.getProduct().getCategory().getName().equals(categoryName)) {
                stockByWarehouseAndCategory.add(stock);
            }
        }
        return stockByWarehouseAndCategory;
    }

    public static int getTotalStockByWarehouse(Warehouse warehouse) {
        int totalStock = 0;
        for (Stock stock : stockList) {
            if (stock.getWarehouse().equals(warehouse)) {
                totalStock += stock.getStockNumber();
            }
        }
        return totalStock;
    }

    public static ObservableList<Stock> getStockList() {
        return stockList;
    }

    public static void setStockList(ObservableList<Stock> stockList) {
        StockDAO.stockList = stockList;
    }

    // sort list
    public static ObservableList<Stock> sortList(Warehouse warehouse) {
        ObservableList<Stock> sortedProductId = FXCollections.observableArrayList();

        for (Stock stock : warehouse.getStockList()) {
            sortedProductId.addAll((stock));
        }

        Collections.sort(sortedProductId,
                Comparator.comparingInt(o -> Integer.parseInt(o.getProductId().substring(1))));
        StockDAO.setStockList(sortedProductId);
        return sortedProductId;
    }

}