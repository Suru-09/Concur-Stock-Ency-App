package entity;

public class Request {
    public enum RequestType {
        BUY,
        SELL
    }

    private int price;
    private int noOfStocks;
    private Long  companyId;
    private RequestType requestType;

    public Request(int price, Long companyId, RequestType requestType, int noOfStocks) {
        this.price = price;
        this.companyId = companyId;
        this.requestType = requestType;
        this.noOfStocks = noOfStocks;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyName(Long companyId) {
        this.companyId = companyId;
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
        return "{" +
                "\n price: " + price +
                ",\n noOfStocks: " + noOfStocks +
                ",\n companyId: " + companyId +
                ",\n requestType: " + requestType +
                "\n}";
    }
}
