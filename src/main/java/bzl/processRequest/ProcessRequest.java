package bzl.processRequest;

import entity.Company;
import entity.Request;
import entity.RequestDeserializer;
import entity.ValueCalculator;
import entity.RequestResponse;
import exceptions.RepoException;
import repo.GSONRepo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ProcessRequest implements Callable<RequestResponse> {

    private Request request;
    private ValueCalculator valCalc;
    private GSONRepo repo;

    public ProcessRequest(Request request, GSONRepo repo, ValueCalculator valCalc)
    {
        this.repo = repo;
        this.request = request;
        this.valCalc = valCalc;
    }


    public RequestResponse sellStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Long clientId = request.getClientId();
        Company comp = repo.get(companyId);
        Boolean isSuccessful = false;

        // TO DO: dumb matching we have to improve here
        if ( request.getPrice() < comp.getStock().getPrice() )
        {
            valCalc.updatePrice(this.repo, this.repo.get(companyId), this.request);
            comp.setStockCount(comp.getStockCount() + request.getNoOfStocks());
            var companyStock = comp.getStock();
            companyStock.setPrice((float) (companyStock.getPrice() - 1.15));
            comp.setStock(companyStock);
            repo.update(comp);
            isSuccessful = true;
        }
        System.out.println(comp);
        return new RequestResponse(isSuccessful, companyId, clientId);
    }


    public RequestResponse buyStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Long clientId = request.getClientId();
        Company comp = repo.get(companyId);
        Boolean isSuccessful = false;

        System.out.println("IN BUY STOCKS");

        // TO DO: dumb matching we have to improve here
        if ( request.getNoOfStocks() < comp.getStockCount() &&
            request.getPrice() < comp.getStock().getPrice() )
        {
            valCalc.updatePrice(this.repo, this.repo.get(companyId), this.request);
        }
        System.out.println(comp);
        return new RequestResponse(isSuccessful, companyId, clientId);
    }

    @Override
    public RequestResponse call() {
        RequestResponse response = new RequestResponse(false, -1L, -1L);
        if (request.getRequestType() == Request.RequestType.BUY) {
            try {
                System.out.println("BUY STOCK");
                response = buyStocks();
            }
            catch(RepoException e)
            {
                e.printStackTrace();
            }

        }
        else if(request.getRequestType() == Request.RequestType.SELL)
        {
            try {
                System.out.println("SELL STOCK");
                response = sellStocks();
            }
            catch(RepoException e)
            {
                e.printStackTrace();
            }
        }
        return response;
    }
}
