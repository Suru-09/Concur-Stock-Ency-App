package entity;

public class RequestResponse {
    private boolean isSuccessful;
    private Long  companyId;
    private Long clientId;

    public RequestResponse(boolean isSuccessful, Long companyId, Long clientId) {
        this.isSuccessful = isSuccessful;
        this.companyId = companyId;
        this.clientId = clientId;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Long getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "RequestResponse{" +
                "isSuccessful=" + isSuccessful +
                ", companyId=" + companyId +
                ", clientId=" + clientId +
                '}';
    }
}
