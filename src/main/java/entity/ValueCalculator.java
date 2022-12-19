package entity;

import exceptions.RepoException;
import repo.GSONRepo;

import java.util.*;

public class ValueCalculator {

    //this is a map that remembers the history of best buy & best sell prices per company
    private Map<String,Float[]> bestHistory;

    public ValueCalculator(GSONRepo repo){
        this.bestHistory = new HashMap<String, Float[]>();
        for(Company c : repo.getAll()){
            // initialise history map
            Float[] best ={c.getStock().getPrice(), (float)(c.getStock().getPrice() + 0.01), (float)(0), (float)(0)};
            this.bestHistory.put(c.getCompanyName(), best);
        }
    }

    public void updatePrice(GSONRepo repo, Company c, Request r){
        float current = c.getStock().getPrice(); // get present price of company stock
        float bestBuyBack = this.bestHistory.get(c.getCompanyName())[0]; // get best price we bought back from client
        float bestSell = this.bestHistory.get(c.getCompanyName())[1]; // get best price we ever sold to client

        float fluctuation;
        float newPrice;
        boolean reset = false;

        if(bestBuyBack >= bestSell){
            // if we end up buying back from client with the same price we sell to client or
            // even bigger price, it means company's value has decreased => we lower price
            fluctuation = (bestSell - (bestBuyBack + current/50))/2/100;
            reset = true;
        }
        else {

            //we increase the price
            fluctuation = (bestSell - bestBuyBack)/2/100;
        }

        newPrice = current + current * fluctuation; // compute new price

        if(newPrice < 0){
            // if buy any possibility (unlikely) the price computed
            // ends up to be negative due to wrong mathematical computations
            // we do not update the price
            newPrice = current;
        }

        c.getStock().setPrice(newPrice); // update price in company stock
        try{
            repo.update(c); // update company in repo
        }
        catch (RepoException e){
            e.printStackTrace();
        }

        if(!reset){
            // if we didn't decrease the price
            if((r.getRequestType() == Request.RequestType.SELL && r.getPrice() < bestBuyBack) || (this.bestHistory.get(c.getCompanyName())[2] >= 10)){
                // update best buy back price if request was of type SELL (user wants to sell to us)
                Float[] prices = {(float)r.getPrice() , this.bestHistory.get(c.getCompanyName())[1], (float)(0), (this.bestHistory.get(c.getCompanyName())[3] + 1 )};
                this.bestHistory.replace(c.getCompanyName(),prices);
            }
            else if((r.getRequestType() == Request.RequestType.BUY && r.getPrice() > bestSell) || (this.bestHistory.get(c.getCompanyName())[3] >= 10)){
                // update best sell price if request was of type BUY (user wants to buy from us)
                Float[] prices = {this.bestHistory.get(c.getCompanyName())[0] , (float)r.getPrice() , (this.bestHistory.get(c.getCompanyName())[2] + 1 ),  (float)(0)};
                this.bestHistory.replace(c.getCompanyName(),prices);
            }
        }
        else{
            //in case we had to set the price lower we have to reset best buy and best sell to avoid wrong computation
            Float[] prices = {newPrice , (float)(newPrice + 0.01), (float)(0), (float)(0) };
            this.bestHistory.replace(c.getCompanyName(),prices);
        }
        

    }
}
