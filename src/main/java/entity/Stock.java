package entity;

public class Stock {
    private String company;
    private Float price;

    public Stock(Float price) {
        this.price = price;
    }

    public Stock(String company, Float price) {
        this.company = company;
        this.price = price;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "company='" + company + '\'' +
                ", price=" + price +
                '}';
    }
}
