package edu.yu.parallel.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.text.Position;

import edu.yu.parallel.Brokerage;
import edu.yu.parallel.Portfolio;
import edu.yu.parallel.Stock;
import edu.yu.parallel.StockPrices;

final public class BrokerageImpl implements Brokerage {

    //concurent hashmap of stock symbols to a blocking queue of selling bids.
    ConcurrentHashMap<String, Integer> stocksForSale;
    ConcurrentHashMap<String, Stock> stocksAvaliable;
    ConcurrentHashMap<String, Portfolio> clients;
    List<Stock> stocks;
    StockPrices stockPrices;
    //at a buy, lock the two traders/clients and complete the trade

    /**
     * Constructor
     * 
     * @param stocks      list of Stock objects
     * @param stockPrices self-updating object with the current price of each stock
     */
    public BrokerageImpl(List<Stock> stocks, StockPrices stockPrices) {
        stocksForSale = new ConcurrentHashMap<String, Integer>();
        clients = new ConcurrentHashMap<String, Portfolio>();
        this.stocks = stocks;
        stocksAvaliable = new ConcurrentHashMap<String, Stock>();
        for(Stock s: stocks){
            stocksAvaliable.put(s.getSymbol(), s);
        }
        this.stockPrices = stockPrices;
        
    }

    /**
     * Register a client with the brokerage
     * 
     * @param clientId       the id of the client
     * @param initialBalance the initial cash balance of the client
     * @return the client's updated portfolio information
     * @throws IllegalArgumentException if the client is already registered
     */
    @Override
    public Portfolio registerClient(String clientId, double initialBalance) {
        if(clients.keySet().contains(clientId)){
            throw new IllegalArgumentException();
        }
        System.out.println("creating client");
        Portfolio p = new PortfolioImpl(clientId, initialBalance, new HashMap<String, Integer>());
        clients.put(clientId, p);
        return p;
    }


    /**
     * Buy shares of a stock for a client
     * 
     * @param clientId the id of the client
     * @param symbol   the symbol of the stock
     * @param shares   the number of shares to buy
     * @return the client's updated portfolio information
     * @throws IllegalArgumentException    if the client is not registered, or the
     *                                     symbol is invalid, or the shares is not
     *                                     positive
     * @throws InsufficientSharesException if there are not enough shares available
     * @throws InsufficientFundsException  if the client does not have enough funds
     */
    @Override
    public Portfolio buyShares(String clientId, String symbol, int shares) {
        System.out.println("buying shares");
        Portfolio updatedClient;
        Portfolio client = clients.get(clientId);
        synchronized(client){
            client = clients.get(clientId);
            System.out.println("in client lock");
            Stock stockToBuy = stocksAvaliable.get(symbol);
            synchronized(stockToBuy){
                stockToBuy = stocksAvaliable.get(symbol);
                System.out.println("in stock lock");
                double budget = client.getBalance();
        

                Map<String, Integer> positions = client.getStockPositions();
  

                double price = shares*(getStockPrices().get(symbol));
          
              
                if(price>budget){
                    System.out.println("InsufficientFundsException");
                    throw new InsufficientFundsException();
                }
                if(stockToBuy.getShares()<shares){
                    System.out.println("InsufficientSharesException");
                    throw new InsufficientSharesException();
                }
          
                //stocksForSale.compute(symbol, (k, v) -> (v == null) ? shares : v - shares);
                stockToBuy = new StockImpl(symbol, stockToBuy.getName(), price, stockToBuy.getShares() - shares);
        
                System.out.println(stockToBuy.getShares());
                int perviousSharesOfStock = 0;
                if(!(client.getStockPositions().get(symbol) == null)){
                    perviousSharesOfStock = client.getStockPositions().get(symbol);
                }
                
                positions.put(symbol, perviousSharesOfStock+shares);
                

      
                updatedClient = new PortfolioImpl(clientId, budget-price, positions);
                client = updatedClient;
            
                clients.put(clientId, updatedClient);
                stocksAvaliable.put(symbol, stockToBuy);

                System.out.println("end of stock lock");
            }
            System.out.println("end of client lock");
            
        }
        System.out.println("finished buying shares");
        return updatedClient;

    }

     /**
     * Sell shares of a stock for a client
     *
     * @param clientId the client id of the client
     * @param symbol   the symbol of the stock to sell
     * @return the client's updated portfolio information
     * @throws IllegalArgumentException    if the client is not registered, or the
     *                                     symbol is invalid, pr the shares is not
     *                                     positive
     * @throws InsufficientSharesException if the client does not have enough shares
     *                                     to sell
     */
    @Override
    public Portfolio sellShares(String clientId, String symbol, int shares) {
        Portfolio updatedClient;
        Portfolio client = clients.get(clientId);
        synchronized(client){
            client = clients.get(clientId);
            Stock stockToSell = stocksAvaliable.get(symbol);

            int sharesOwned = client.getStockPositions().get(symbol);
            Map<String, Integer> positions = client.getStockPositions();
            if(sharesOwned<shares){
                throw new InsufficientSharesException();
            }
            sharesOwned = sharesOwned-shares;
            double revenue = shares*(getStockPrices().get(symbol));

            //stocksForSale.compute(symbol, (k, v) -> (v == null) ? shares : v + shares);
            stocksAvaliable.put(symbol, new StockImpl(symbol, stockToSell.getName(), getStockPrices().get(symbol), stockToSell.getShares()+shares));

            positions.put(symbol, sharesOwned);
            updatedClient = new PortfolioImpl(clientId, client.getBalance()+revenue, positions);
            clients.put(clientId, updatedClient);
        }
        return updatedClient;
    }

    /**
     * Get the current portfolio of a client
     * 
     * @param clientId the id of the client
     * @return the client's updated portfolio information
     * @throws IllegalArgumentException if the client is not registered
     */
    @Override
    public Portfolio getClientPortfolio(String clientId) {
        Portfolio p = clients.get(clientId);
        if(p == null){
            throw new IllegalArgumentException();
        }
        return p;
    }

    @Override
    public Map<String, Double> getStockPrices() {
        System.out.println("In the getStockPrice");
        //System.out.println(StockPrices.getInstance(this.stocks).getStockPrices().toString());
        //return StockPrices.getInstance(this.stocks).getStockPrices();
        System.out.println(this.stockPrices.getStockPrices());
        return this.stockPrices.getStockPrices();
        
    }
}
