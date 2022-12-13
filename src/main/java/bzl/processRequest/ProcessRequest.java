package bzl.processRequest;

import entity.Company;
import entity.Request;
import entity.RequestDeserializer;
import exceptions.RepoException;
import repo.GSONRepo;

public class ProcessRequest {

    private GSONRepo repo;
    private Request request;

    public ProcessRequest(String request)
    {
        try
        {
            repo = GSONRepo.getInstance();
        }
        catch( RepoException e)
        {
            e.printStackTrace();
        }
        this.request = RequestDeserializer.toRequest(request);
        System.out.println(this.request);
    }


    public boolean sellStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Company comp = repo.get(companyId);

        synchronized (this)
        {
            // TO DO: dumb matching we have to improve here
            if ( request.getPrice() < comp.getStock().getPrice() )
            {
                comp.setStockCount(comp.getStockCount() + request.getNoOfStocks());
                var companyStock = comp.getStock();
                companyStock.setPrice((float) (companyStock.getPrice() - 1.15));
                comp.setStock(companyStock);
                repo.update(comp);
            }
        }

        return true;
    }


    public boolean buyStocks() throws RepoException {
        Long companyId = request.getCompanyId();
        Company comp = repo.get(companyId);

        synchronized (this)
        {
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
        }

        return true;
    }

    public void updateCompany()
    {

    }
}
