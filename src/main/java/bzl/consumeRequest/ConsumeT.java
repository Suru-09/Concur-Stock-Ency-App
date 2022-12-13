package bzl.consumeRequest;

import bzl.processRequest.ProcessRequest;

import java.util.concurrent.Callable;

public class ConsumeT implements Callable<Boolean> {
    private String msg;

    ConsumeT(String receivedMessage)
    {
        this.msg = receivedMessage;
    }

    @Override
    public Boolean call() throws Exception {
        ProcessRequest bzl = new ProcessRequest(msg);
        bzl.buyStocks();
        return true;
    }
}
