package bzl.processRequest;

import entity.Request;
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
    }

    public boolean sellStocks()
    {

        return true;
    }

    public boolean buyStocks()
    {

        return true;
    }

    public void updateCompany()
    {

    }
}
