package se.lu.ics.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.controllers.MainController;
import se.lu.ics.models.Category;
 
public class CategoryDAO {

    private static ObservableList<Category> categories = FXCollections.observableArrayList();

    static {
        try{
            updateCategoriesFromDatabase();
        } catch(SQLException e){
            MainController.connectionErrorTerminateApp();
        }
        
    }

    public static void updateCategoriesFromDatabase() throws SQLException{
        String query = "SELECT * FROM Category";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            categories.clear();
            while (resultSet.next()) {
                String categoryName = resultSet.getString("Name");
                Category category = new Category(categoryName);
                categories.add(category);
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public static void addCategory(String name) throws SQLException{
        String addQuery = "INSERT INTO Category (Name) VALUES (?)";
        try (Connection connection = ConnectionHandler.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
            preparedStatement.setString(1, name);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                Category category = new Category(name);
                categories.add(category);
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public static ObservableList<Category> getCategories() {
        return categories;
    }

    public static Category getCategoryByName(String name) {
        for (Category category : categories) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    public static void setCategories(ObservableList<Category> categories) {
        CategoryDAO.categories = categories;
    }

    public static void addCategories(Category category) {
        categories.add(category);
    }

    public static void removeCategories(Category category) {
        categories.remove(category);
    }

}