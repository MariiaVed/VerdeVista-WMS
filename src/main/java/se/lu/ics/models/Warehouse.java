package se.lu.ics.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Warehouse {
    private String warehouseId;
    private String address;
    private int capacity;
    private ObservableList<Stock> stockList = FXCollections.observableArrayList();
    public ObservableList<Supplier> suppliers = FXCollections.observableArrayList();

    // constructors
    public Warehouse() {

    }

    public Warehouse(String warehouseId, String address, int capacity) {
        this.warehouseId = warehouseId;
        this.address = address;
        this.capacity = capacity;
    }

    // method to print the String (name) of the instance object instead of the
    // filecode to the instance object
    @Override
    public String toString() {
        return this.getWarehouseId();
    }

    // add and remove from suppliers
    public void addSupplier(Supplier supplier) {
        this.suppliers.add(supplier);
    }

    public void removeSuppliers(Supplier supplier) {
        this.suppliers.remove(supplier);
    }

    // getters and setters
    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ObservableList<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(ObservableList<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public void addStock(Stock stock) {
        this.stockList.add(stock);
    }

    public void removeStock(Stock stock) {
        this.stockList.remove(stock);
    }

    public ObservableList<Stock> getStockList() {
        return stockList;
    }
}
