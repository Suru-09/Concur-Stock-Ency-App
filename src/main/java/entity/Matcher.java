package entity;

import entity.Company;
import entity.Request;
public class Matcher {
    public static boolean priceCompare(Company company, Request request){
        Float percentOfStocks = (float) (request.getNoOfStocks()/company.getStockCount()*100);
        Float multiFactor;
        if ( percentOfStocks < 0.2F )
            multiFactor = 0.8F;
        else if ( percentOfStocks < 0.5F )
            multiFactor = 0.85F;
        else if ( percentOfStocks < 0.7F )
            multiFactor = 0.9F;
        else
             multiFactor = 0.95F;
        return request.getPrice() < company.getStock().getPrice()*multiFactor;
    }

    public static Float calculatePrice(Request request){
        return (float) (request.getNoOfStocks()*1.03);
    }
}
