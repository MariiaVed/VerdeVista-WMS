package se.lu.ics.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Category {
    private String name;

    public ObservableList<Product> productList = FXCollections.observableArrayList();

    // method to print the String (name) of the instance object instead of the filecode to the instance object
    @Override
    public String toString() {
        return this.getName();
    }

    // constructors
    public Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, Product product) {
        this.name = name;
        this.productList.add(product);
    }

    // add and remove from Products
    public void addProduct(Product product) {
        this.productList.add(product);
    }

    public void removeProduct(Product product) {
        this.productList.remove(product);
    }

    // getters and setters
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ObservableList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ObservableList<Product> productList) {
        this.productList = productList;
    }

}
