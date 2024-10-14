package se.lu.ics.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Supplier {
    private String supplierId;
    private String name;
    private String email;
    private String address;

    public ObservableList<Warehouse> warehouses = FXCollections.observableArrayList();
    public ObservableList<Product> products = FXCollections.observableArrayList();

    // constructors
    public Supplier() { // behövs denna? delete if possible

    }

    public Supplier(String supplierId, String name, String email, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    // method to print the String (name) of the instance object instead of the
    // filecode to the instance object
    @Override
    public String toString() {
        return this.getName();
    }

    

    // // add and remove from warehouses
    // public void addWarehouse(Warehouse warehouse) {
    //     this.warehouses.add(warehouse);
    // }

    // public void removeWarehouse(Warehouse warehouse) {
    //     this.warehouses.remove(warehouse);
    // }

    // getters and setters
    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    // public ObservableList<Warehouse> getWarehouses() {
    //     return warehouses;
    // }

    // public void setWarehouses(ObservableList<Warehouse> warehouses) {
    //     this.warehouses = warehouses;
    // }

}
