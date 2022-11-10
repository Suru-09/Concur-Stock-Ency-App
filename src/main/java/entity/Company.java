package entity;

public class Company extends BaseEntity<Long> {
    private String name;
    private String address;
    private Stock stock;
    private Integer stockCount;

    public Company(String name, Integer stockCount, String address, Stock stock) {
        this.name = name;
        this.stockCount = stockCount;
        this.address = address;
        this.stock = stock;
    }

    public String getCompanyName() {
        return name;
    }

    public void setCompanyName(String name) {
        this.name = name;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", stock=" + stock +
                ", noOfStocks=" + stockCount +
                '}';
    }
}
