package bzl.simulateRequest;

import entity.Request;


public class WorkerThread  implements Runnable {

    private Request request;

    public WorkerThread(Request request)
    {
        this.request = request;
    }

    @Override
    public void run()
    {
        System.out.println(Thread.currentThread().getName() + ": ");
        Boolean res = SimulateRequestMultithreaded.sendRequest(request);
        // add some delay to see the actual effect of the threads
        processCommand();
        System.out.println("Thread [" + Thread.currentThread().getName() + "] ended");
    }

    private void processCommand()
    {
        try {
            Thread.sleep(350);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
