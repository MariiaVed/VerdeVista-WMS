package se.lu.ics.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Product {
    private String productId;
    private String productName; 
    private Category category;
    private Supplier supplier;

    private ObservableList<Stock> stockList = FXCollections.observableArrayList();

    // constructors
    public Product() {
        
    }

    public Product(String productId, String productName, Category category, Supplier supplier) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.supplier = supplier;
        supplier.addProduct(this);
        category.addProduct(this);
    }

    // method to print the String (name) of the instance object instead of the filecode to the instance object
    @Override
    public String toString() {
        return this.getProductName();
    }

    // getters and setters
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category.removeProduct(this);
        this.category = category;
    }
    
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId){
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }   

    public void setSupplier(Supplier supplier) {
        this.supplier.removeProduct(this);
        this.supplier = supplier;
    }
    public Supplier getSupplier() {
        return supplier;
    }

    public void addStock(Stock stock){
        this.stockList.add(stock);
    }

    public void removeStock(Stock stock){
        this.stockList.remove(stock);
    }

    public ObservableList<Stock> getStockList(){
        return stockList;
    }

    public int getTotalStock(){
        int totalStock = 0;
        for (Stock stock : stockList) {
            totalStock += stock.getStockNumber();
        }
        return totalStock;
    }


}
