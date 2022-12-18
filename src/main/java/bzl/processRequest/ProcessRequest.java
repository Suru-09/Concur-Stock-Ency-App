package bzl.processRequest;

import entity.Company;
import entity.Request;
import entity.Matcher;
import entity.RequestDeserializer;
import exceptions.RepoException;
import repo.GSONRepo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ProcessRequest implements Callable<Boolean> {

    private GSONRepo repo;
    private Request request;

    public ProcessRequest(Request request, GSONRepo repo)
    {
        this.repo = repo;
        this.request = request;
    }


    public boolean sellStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Company comp = repo.get(companyId);

        if ( Matcher.priceCompare(comp, request) )
        {
            comp.setStockCount(comp.getStockCount() + request.getNoOfStocks());
            var companyStock = comp.getStock();
            companyStock.setPrice((float) (companyStock.getPrice() - Matcher.calculatePrice(request)));
            comp.setStock(companyStock);
            repo.update(comp);
        }
        System.out.println(comp);
        return true;
    }


    public boolean buyStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Company comp = repo.get(companyId);

        if ( Matcher.priceCompare(comp, request) && request.getNoOfStocks() < comp.getStockCount() )
        {
            comp.setStockCount(comp.getStockCount() - request.getNoOfStocks());
            var companyStock = comp.getStock();
            companyStock.setPrice((float) (companyStock.getPrice() + Matcher.calculatePrice(request)));
            comp.setStock(companyStock);
            repo.update(comp);
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
