package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RequestGenerator {

    private List<Client> clientList;
    private List<Company> companyList;
    public ArrayList<Request> reqList;

    public RequestGenerator(Company[] companies, int nr_clients){
        this.clientList = new ArrayList<>();
        initClientList(nr_clients);
        this.companyList = new ArrayList<Company>(Arrays.asList(companies));
        this.reqList = new ArrayList<>();
    }

    private void initClientList(int nr){
        for(int i=1; i<=nr; ++i){
            this.clientList.add(new Client());
        }
    }

    public ArrayList<Request> RequestPullGenerator(int nrOfRequests){

        Random rand = new Random();

        for(int i=1; i<=nrOfRequests; ++i){

            int clintIndex = rand.nextInt(clientList.size());
            int companyIndex = rand.nextInt(companyList.size());
            int nrOfStocks = rand.nextInt(20);
            int price = rand.nextInt(100);

            Request req = new Request(price, clientList.get(clintIndex).getId(), companyList.get(companyIndex).getId(), Request.RequestType.BUY, nrOfStocks);
            reqList.add(req);
        }

        return reqList;
    }

}
