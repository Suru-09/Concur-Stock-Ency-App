package entity;

public class Client extends BaseEntity<Long>{
    private boolean awaitResponse;

    public Client(){
        this.awaitResponse = false;
    }

    private boolean getAwaitResponse(){
        return this.awaitResponse;
    }

    private void setAwaitResponse(boolean awaitResponse){
        this.awaitResponse = awaitResponse;
    }

    @Override
    public String toString() {
        return "Pending requests : " + awaitResponse;
    }
}
