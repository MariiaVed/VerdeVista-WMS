package se.lu.ics.models;

public class Stock {
    private Product product;
    private Warehouse warehouse;
    private int stockNumber;

    // constructors
    public Stock(Product product, Warehouse warehouse, int stockNumber) {
        this.product = product;
        this.warehouse = warehouse;
        this.stockNumber = stockNumber;
        warehouse.addStock(this);
        product.addStock(this);
    }

    // getters and setters
    public String getProductName(){
        return product.getProductName();
    }

    public String getProductId(){
        return product.getProductId();
    }

    public String getSupplierName(){
        return product.getSupplier().getName();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public int getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(int stockNumber) {
        this.stockNumber = stockNumber;
    }
}
