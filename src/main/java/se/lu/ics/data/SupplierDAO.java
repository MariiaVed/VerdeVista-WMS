package se.lu.ics.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.controllers.MainController;
import se.lu.ics.models.Product;
import se.lu.ics.models.Supplier;

public class SupplierDAO {

    private static ObservableList<Supplier> suppliers = FXCollections.observableArrayList();

    static {
        try {
            updateSuppliersFromDatabase();
        } catch (SQLException ex) {
            MainController.connectionErrorTerminateApp();
        }
    }

    public static void updateSuppliersFromDatabase() throws SQLException {

        String updateQuery = "SELECT * FROM Supplier";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            suppliers.clear();
            while (resultSet.next()) {
                String supplierId = resultSet.getString("SupplierId");
                String name = resultSet.getString("Name");
                String email = resultSet.getString("Email");
                String address = resultSet.getString("Address");

                Supplier supplier = new Supplier(supplierId, name, email, address);
                suppliers.add(supplier);
            }

        } catch (SQLException ex) {
            throw ex;

        }

    }

    public static void addSupplier(String supplierName, String supplierEmail, String supplierAddress)
            throws SQLException {

        String addQuery = "INSERT INTO Supplier (SupplierId, Name, Email, Address) VALUES (?, ?, ?, ?)";
        String idQuery = "SELECT TOP 1 CAST(SUBSTRING(SupplierId, 2, LEN(SupplierId)) AS INT) AS HighestId FROM Supplier ORDER BY HighestId DESC";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement idPreparedStatement = connection.prepareStatement(idQuery);
            ResultSet idResultSet = idPreparedStatement.executeQuery();
            idResultSet.next();
            int highestId = idResultSet.getInt("HighestId") + 1;
            String idNumber = "S" + highestId;

            PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
            preparedStatement.setString(1, idNumber);
            preparedStatement.setString(2, supplierName);
            preparedStatement.setString(3, supplierEmail);
            preparedStatement.setString(4, supplierAddress);
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                Supplier supplier = new Supplier(idNumber, supplierName, supplierEmail, supplierAddress);
                suppliers.add(supplier);
            }

        } catch (SQLException ex) {
            throw ex;
        }

    }

    // //update supplier function
    public static void updateSupplier(String supplierId, String supplierName,
            String supplierEmail, String supplierAddress) throws SQLException {

        String query = "UPDATE Supplier SET Name = ?, Email = ?, Address = ? WHERE SupplierId = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, supplierName);
            preparedStatement.setString(2, supplierEmail);
            preparedStatement.setString(3, supplierAddress);
            preparedStatement.setString(4, supplierId);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                Supplier supplier = getSupplierById(supplierId);
                supplier.setName(supplierName);
                supplier.setEmail(supplierEmail);
                supplier.setAddress(supplierAddress);

            }
        } catch (SQLException ex) {
            throw ex;

        }
    }

    public static void removeSupplier(String supplierId) throws SQLException {
        String query = "DELETE FROM Supplier WHERE SupplierId = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, supplierId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                Supplier supplier = getSupplierById(supplierId);
                suppliers.remove(supplier);
            }
        } catch (SQLException ex) {
            throw ex;

        }
    }

    public static Supplier getSupplierById(String id) {
        for (Supplier supplier : suppliers) {
            if (id.equals(supplier.getSupplierId())) {
                return supplier;
            }

        }
        return null;

    }

    public static ObservableList<Supplier> searchSupplierId(String id) {
        ObservableList<Supplier> searchSupplierId = FXCollections.observableArrayList();
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierId().toLowerCase().contains(id.toLowerCase())
                    || supplier.getSupplierId().toUpperCase().contains(id.toUpperCase())) {
                searchSupplierId.add(supplier);
            }
        }
        return searchSupplierId;
    }

    public static ObservableList<Supplier> getSuppliers() {
        return suppliers;
    }

    public static void removeAllProducts(Supplier supplier) throws SQLException {
        ObservableList<Product> products = FXCollections.observableArrayList();
        products.addAll(supplier.getProducts());

        for (Product product : products) {
            ProductDAO.removeAllStock(product);
            ProductDAO.removeProduct(product.getProductId());

        }
    }
}
