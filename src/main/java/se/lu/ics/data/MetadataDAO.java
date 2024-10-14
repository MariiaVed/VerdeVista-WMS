package se.lu.ics.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;  
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;  


public class MetadataDAO {

    //Retrieve the names of all tables in the database
    public static ObservableList<String> getTables() throws SQLException{
        String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<String> tables = FXCollections.observableArrayList();
            while (resultSet.next()) {
                tables.add(resultSet.getString("TABLE_NAME")); 
            }
            return tables;
        } catch (SQLException e) {
            throw e;
            
        }
    }
 

    //Retrieve the names of all primary key in the database
    public static ObservableList<String> getPrimaryKeys() throws SQLException {
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE CONSTRAINT_NAME LIKE 'PK%'";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<String> primaryKeys = FXCollections.observableArrayList();
            while (resultSet.next()) {
                primaryKeys.add(resultSet.getString("COLUMN_NAME")); 
            }
            return primaryKeys;
        } catch (SQLException e) {
            throw e;
        }
    }


    //Retrieve the names of all foreign keys in the database
    public static ObservableList<String> getForeignKeys() throws SQLException{
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE CONSTRAINT_NAME LIKE 'FK%'";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<String> foreignKeys = FXCollections.observableArrayList();
            while (resultSet.next()) {
                foreignKeys.add(resultSet.getString("COLUMN_NAME")); 
            }
            return foreignKeys;
        } catch (SQLException e) {
            throw e;
        }
    }
   
    //retrieve names of all columns in the product table of the database
    public static ObservableList<String> getColumns() throws SQLException{
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Product'";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<String> columns = FXCollections.observableArrayList();
            while (resultSet.next()) {
                columns.add(resultSet.getString("COLUMN_NAME")); 
            }
            return columns;
        } catch (SQLException e) {
            throw e;
        }
    }
    // Retrieve the name and number of rows of the table in your database containing the highest number of rows
    public static ObservableList<String> getTableWithMostRows() throws SQLException{
        String query = "SELECT TOP 1 t.NAME AS TABLE_NAME, SUM(p.rows) AS NumberOfRows FROM sys.tables t JOIN sys.partitions p ON t.object_id = p.object_id WHERE p.index_id IN(0, 1) GROUP BY t.NAME ORDER BY NumberOfRows DESC";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<String> tableWithMostRows = FXCollections.observableArrayList();
            while (resultSet.next()) {
                tableWithMostRows.add(resultSet.getString("TABLE_NAME") + " (" + resultSet.getString("NumberOfRows") + " rows)"); 
            }
            return tableWithMostRows;
        } catch (SQLException e) {
            throw e;
        }
    }
    
}
