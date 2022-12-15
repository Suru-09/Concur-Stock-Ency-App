package bzl.processRequest;

import entity.Company;
import entity.Request;
import entity.RequestDeserializer;
import exceptions.RepoException;
import repo.GSONRepo;

import java.util.concurrent.Callable;

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

        // TO DO: dumb matching we have to improve here
        if ( request.getPrice() < comp.getStock().getPrice() )
        {
            comp.setStockCount(comp.getStockCount() + request.getNoOfStocks());
            var companyStock = comp.getStock();
            companyStock.setPrice((float) (companyStock.getPrice() - 1.15));
            comp.setStock(companyStock);
            repo.update(comp);
        }
        return true;
    }


    public boolean buyStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Company comp = repo.get(companyId);

        // TO DO: dumb matching we have to improve here
        if ( request.getNoOfStocks() < comp.getStockCount() &&
            request.getPrice() < comp.getStock().getPrice() )
        {
            comp.setStockCount(comp.getStockCount() - request.getNoOfStocks());
            var companyStock = comp.getStock();
            companyStock.setPrice((float) (companyStock.getPrice() + 1.15));
            comp.setStock(companyStock);
            repo.update(comp);
        }

        return true;
    }

    @Override
    public Boolean call() {
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
