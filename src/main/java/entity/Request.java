package entity;

public class Request {
    public enum RequestType {
        BUY,
        SELL
    }

    private int price;
    private int noOfStocks;
    private String companyName;
    private RequestType requestType;

    public Request(int price, String companyName, RequestType requestType, int noOfStocks) {
        this.price = price;
        this.companyName = companyName;
        this.requestType = requestType;
        this.noOfStocks = noOfStocks;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public int getNoOfStocks() {
        return noOfStocks;
    }

    public void setNoOfStocks(int noOfStocks) {
        this.noOfStocks = noOfStocks;
    }

    @Override
    public String toString() {
        return "Request{" +
                "price=" + price +
                ", noOfStocks=" + noOfStocks +
                ", companyName='" + companyName + '\'' +
                ", requestType=" + requestType +
                '}';
    }
}
