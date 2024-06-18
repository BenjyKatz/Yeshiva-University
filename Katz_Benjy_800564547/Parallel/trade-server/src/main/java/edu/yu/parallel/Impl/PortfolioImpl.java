package edu.yu.parallel.Impl;

import java.util.HashMap;
import java.util.Map;

import edu.yu.parallel.Portfolio;

final public class PortfolioImpl implements Portfolio {

    /**
     * Contstructor
     * 
     * @param clientId
     * @param accountBalance
     * @param stockPositions
     */
    HashMap<String, Integer> positions;
    double accountBalance;
    String clientId;
    public PortfolioImpl(String clientId, double accountBalance, Map<String, Integer> stockPositions) {
        positions = new HashMap<String, Integer>();
        positions.putAll(stockPositions);
        this.accountBalance = accountBalance;
        this.clientId = clientId;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public double getBalance() {
        return this.accountBalance;

    }

    @Override
    public Map<String, Integer> getStockPositions() {
        return this.positions;
    }

}
