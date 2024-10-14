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
import se.lu.ics.models.Category;
import se.lu.ics.models.Product;
import se.lu.ics.models.Stock;
import se.lu.ics.models.Supplier;

public class ProductDAO {

    private static ObservableList<Product> products = FXCollections.observableArrayList();

    static{
        
        try {
            updateProductsFromDatabase();
        } catch (SQLException e) {
            MainController.connectionErrorTerminateApp();
        }
        
    }

    public static void updateProductsFromDatabase() throws SQLException{
        String query = "SELECT * FROM Product";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            products.clear();
            while (resultSet.next()) {
                String productId = resultSet.getString("ProductId");
                String productName = resultSet.getString("Name");
                Category category = CategoryDAO.getCategoryByName(resultSet.getString("CategoryName"));
                Supplier supplier = SupplierDAO.getSupplierById(resultSet.getString("SupplierId"));

                Product product = new Product(productId, productName, category, supplier);
                products.add(product);
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public static void addProduct(String name, Supplier supplier, Category category) throws SQLException{

        String addQuery = "INSERT INTO Product (ProductId, Name, SupplierId, CategoryName) VALUES (?, ?, ?, ?)";
        String idQuery = "SELECT TOP 1 CAST(SUBSTRING(ProductId, 2, LEN(ProductId)) AS INT) AS HighestId FROM Product ORDER BY HighestId DESC";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement idPreparedStatement = connection.prepareStatement(idQuery);
            ResultSet idResultSet = idPreparedStatement.executeQuery();
            idResultSet.next();
            int highestId = idResultSet.getInt("HighestId") + 1;
            String idNumber = "P" + highestId;

            PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
            preparedStatement.setString(1, idNumber);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, supplier.getSupplierId());
            preparedStatement.setString(4, category.getName());
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                Product product = new Product(idNumber, name, category, supplier);
                products.add(product);
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static void updateProduct(String productId, String name, Supplier supplier, Category category) throws SQLException{

        String updateQuery = "UPDATE Product SET Name = ?, SupplierId = ?, CategoryName = ? WHERE ProductId = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, supplier.getSupplierId());
            preparedStatement.setString(3, category.getName());
            preparedStatement.setString(4, productId);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                Product product = getProductById(productId);
                product.setProductName(name);
                product.setSupplier(supplier);
                product.setCategory(category);
                category.addProduct(product);
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static void removeProduct(String productId) throws SQLException{

        String deleteQuery = "DELETE FROM Product WHERE ProductId = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1, productId);
            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                Product product = getProductById(productId);
                products.remove(product);
                Category category = product.getCategory();
                category.removeProduct(product);
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static Product getProductById(String ProductId) {
        for (Product product : products) {
            if (product.getProductId().equals(ProductId)) {
                return product;
            }
        }
        return null;
    }

    public static ObservableList<Product> searchProductId(String id) {
        ObservableList<Product> searchProductId = FXCollections.observableArrayList();
        for (Product product : products) {
            if (product.getProductId().toLowerCase().contains(id)
                    || product.getProductId().toUpperCase().contains(id)) {
                searchProductId.add(product);
            }
        }
        return searchProductId;
    }

    public static Product getLatestAddedProduct() {
        return products.get(products.size() - 1);
    }

    public static ObservableList<Product> productsBelow50() {
        ObservableList<Product> productsBelow50 = FXCollections.observableArrayList();
        for (Product product : products) {
            if (product.getTotalStock() < 50) {
                productsBelow50.add(product);
            }
        }
        return productsBelow50;
    }

    public static ObservableList<Product> getProducts() {
        return products;
    }

    public static void setProducts(ObservableList<Product> products) {
        ProductDAO.products = products;
    }

    // sort list
    public static void sortList() {
        ObservableList<Product> sortedProductId = FXCollections.observableArrayList();
        sortedProductId.addAll(ProductDAO.getProducts());
        Collections.sort(sortedProductId,
                Comparator.comparingInt(o -> Integer.parseInt(o.getProductId().substring(1))));

        ProductDAO.setProducts(sortedProductId);
    }

    public static void removeAllStock(Product product) throws SQLException{

        ObservableList<Stock> stockList = FXCollections.observableArrayList();
        stockList.addAll(product.getStockList());
    
        for(Stock stock : stockList){
            StockDAO.deleteStock(stock);
            
        }
    }


}