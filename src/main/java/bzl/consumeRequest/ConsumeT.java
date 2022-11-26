package bzl.consumeRequest;

import java.util.concurrent.Callable;

public class ConsumeT implements Callable<Boolean> {

    ConsumeT(String receivedMessage)
    {
        System.out.println(receivedMessage);
    }

    @Override
    public Boolean call() throws Exception {
        return true;
    }
}
