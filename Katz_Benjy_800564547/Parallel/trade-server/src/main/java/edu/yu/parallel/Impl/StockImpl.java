package edu.yu.parallel.Impl;

import edu.yu.parallel.Stock;

public class StockImpl implements Stock {

    /**
     * Consructor
     * 
     * @param symbol
     * @param name
     * @param price
     * @param shares
     */
    String symbol;
    String name;
    double price;
    int shares; 
    public StockImpl(String symbol, String name, double price, int shares) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.shares = shares;

    }

    @Override
    public String getSymbol() {
        return this.symbol;

    }

    @Override
    public String getName() {
        return this.name;

    }

    @Override
    public int getShares() {
        return this.shares;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

}
