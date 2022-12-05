package bzl.consumeRequest;

import java.util.concurrent.Callable;

public class ConsumeT implements Callable<Boolean> {
    private String msg;

    ConsumeT(String receivedMessage)
    {
        this.msg = receivedMessage;
    }

    @Override
    public Boolean call() throws Exception {
        // I do nice things
        return true;
    }
}
