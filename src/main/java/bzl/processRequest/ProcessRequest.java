package bzl.processRequest;

import entity.Company;
import entity.Request;
import entity.RequestDeserializer;
import entity.ValueCalculator;
import exceptions.RepoException;
import repo.GSONRepo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ProcessRequest implements Callable<Boolean> {

    private GSONRepo repo;
    private Request request;
    private ValueCalculator valCalc;

    public ProcessRequest(Request request, GSONRepo repo, ValueCalculator valCalc)
    {
        this.repo = repo;
        this.request = request;
        this.valCalc = valCalc;
    }


    public boolean sellStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Company comp = repo.get(companyId);

        // TO DO: dumb matching we have to improve here
        if ( request.getPrice() < comp.getStock().getPrice() )
        {
            valCalc.updatePrice(this.repo, this.repo.get(companyId), this.request);
        }
        System.out.println(comp);
        return true;
    }


    public boolean buyStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Company comp = repo.get(companyId);

        // TO DO: dumb matching we have to improve here
        if ( request.getNoOfStocks() < comp.getStockCount() &&
            request.getPrice() < comp.getStock().getPrice() )
        {
            valCalc.updatePrice(this.repo, this.repo.get(companyId), this.request);
        }
        System.out.println(comp);

        return true;
    }

    @Override
    public Boolean call() {
//        try {
//            Thread.sleep(500);
//        }
//        catch(InterruptedException e)
//        {
//            e.printStackTrace();
//        }

        boolean result = false;
        if (request.getRequestType() == Request.RequestType.BUY) {
            try {
                result = buyStocks();
            }
            catch(RepoException e)
            {
                e.printStackTrace();
            }

        }
        else if(request.getRequestType() == Request.RequestType.SELL)
        {
            try {
                result = sellStocks();
            }
            catch(RepoException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }
}
