package edu.yu.parallel.Impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import edu.yu.parallel.BrokerService;
import edu.yu.parallel.Brokerage;
import edu.yu.parallel.Portfolio;

final public class BrokerServiceImpl implements BrokerService {

    /**
     * Constructor
     * 
     * @param brokerage
     */
    Brokerage brokerage;
    public BrokerServiceImpl(Brokerage brokerage) {
        this.brokerage = brokerage;

    }


    /**
     * Handle a request to get the current stock prices
     * GET /prices
     * Response: a JSON array of JSON objects, each containing the symbol and price of a stock
     * 
     * @param exchange the HttpExchange object
     * @throws IOException
     */
    @Override
    public void handleGetPrices(HttpExchange exchange) throws IOException {
        Map<String, Double> prices = brokerage.getStockPrices();
        
        // Convert the JSON object to a string
        JSONObject jsonResponse = new JSONObject(prices);
        // Set response headers
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.toString().length());

        // Write the JSON response to the output stream
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.toString().getBytes());
        }

    }
     /**
     * Handle a request to register a client
     * POST /register
     * Query Parameters: clientId, funds
     * Response: JSON representation of the client's Portfolio
     * 
     * @param exchange the HttpExchange object
     * @throws IOException
     */
    @Override
    public void handleRegisterClient(HttpExchange exchange) throws IOException {

        URI uri = exchange.getRequestURI();
        HashMap<String, String>paramsMap = getParameters(uri);
        
        String clientId = paramsMap.get("clientId");
        double initialBalance = Double.parseDouble(paramsMap.get("funds"));
        Portfolio p = null;
        try{
            p = brokerage.registerClient(clientId, initialBalance);
        }
        catch(Exception e){
            System.out.println("failed to register client");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, e.toString().length());

            // Write the JSON response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(e.toString().getBytes());
            }
            return;
        }
        
        
        // Convert the JSON object to a string
        JSONObject jsonResponse = portfolioToJSON(p);
        // Set response headers
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.toString().length());

        // Write the JSON response to the output stream
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.toString().getBytes());
        }

    }
    
    /**
     * Handle a request to get a client's portfolio
     * GET /portfolio
     * Query Parameters: clientId
     * Response: JSON representation of the client's Portfolio
     * 
     * @param exchange the HttpExchange object
     * @throws IOException
     */
    @Override
    public void handleGetPortfolio(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        HashMap<String, String>paramsMap = getParameters(uri);
        
        String clientId = paramsMap.get("clientId");
        Portfolio p = null;
        try{
            p = brokerage.getClientPortfolio(clientId); 
        }
        catch(Exception e){
            System.out.println("failed to register client");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, e.toString().length());

            // Write the JSON response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(e.toString().getBytes());
            }
            return;
        }
        
        // Convert the JSON object to a string
        JSONObject jsonResponse = portfolioToJSON(p);
        // Set response headers
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.toString().length());

        // Write the JSON response to the output stream
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.toString().getBytes());
        }

        
    }

    /**
     * Handle a request to get the market value of a client's portfolio
     * GET /marketvalue
     * Query Parameters: clientId
     * Response: JSON object containing the total market value of the client's portfolio
     * {"marketvalue": 123.45"}
     * 
     * @param exchange the HttpExchange object
     * @throws IOException
     */
    @Override
    public void handleGetMarketValue(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        HashMap<String, String>paramsMap = getParameters(uri);
        
        String clientId = paramsMap.get("clientId");
        Portfolio p = null;
        Map<String, Double> prices = brokerage.getStockPrices();
        try{
            p = brokerage.getClientPortfolio(clientId); 
        }
        catch(Exception e){
            System.out.println("failed to register client");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, e.toString().length());

            // Write the JSON response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(e.toString().getBytes());
            }
            return;
        }
        Map<String, Integer> pos = p.getStockPositions();
        double totalMarketValue = 0;
        for (Map.Entry<String, Integer> entry : pos.entrySet()) {
            String symbol = entry.getKey();
            int shares = entry.getValue();
            totalMarketValue = totalMarketValue+(shares*prices.get(symbol));
        }
        totalMarketValue = totalMarketValue+p.getBalance();

        // Convert the JSON object to a string
        JSONObject jsonResponse = new JSONObject().put("marketvalue",totalMarketValue);
        // Set response headers
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.toString().length());

        // Write the JSON response to the output stream
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.toString().getBytes());
        }
    }

    /**
     * Handle a request to buy shares of a stock for a client
     * POST /buy
     * Query Parameters: clientId, symbol, shares
     * Response: JSON representation of the client's Portfolio
     * 
     * @param exchange the HttpExchange object
     * @throws IOException
     */
    @Override
    public void handleBuyRequest(HttpExchange exchange) throws IOException {
        System.out.println("called buy service");
        URI uri = exchange.getRequestURI();
        HashMap<String, String>paramsMap = getParameters(uri); 
        String clientId = paramsMap.get("clientId");
        String symbol = paramsMap.get("symbol");
        int shares = Integer.parseInt(paramsMap.get("shares"));

        Portfolio p;
        try{
            p = brokerage.buyShares(clientId, symbol, shares);
        }
        catch(Exception e){
      
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, e.toString().length());

            // Write the JSON response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(e.toString().getBytes());
            }
            return;
        }
        
        System.out.println("bought shares");
        
        // Convert the JSON object to a string
        JSONObject jsonResponse = portfolioToJSON(p);
        // Set response headers
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.toString().length());

        // Write the JSON response to the output stream
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.toString().getBytes());
        }
        System.out.println("Made buy request");
    }

    /**
     * Handle a request to sell shares of a stock for a client
     * POST /sell
     * Query Parameters: clientId, symbol, shares
     * Response: JSON representation of the client's Portfolio
     * 
     * @param exchange the HttpExchange object
     * @throws IOException
     */
    @Override
    public void handleSellRequest(HttpExchange exchange) throws IOException {
        System.out.println("called sell service");
        URI uri = exchange.getRequestURI();
        HashMap<String, String>paramsMap = getParameters(uri); 
        String clientId = paramsMap.get("clientId");
        String symbol = paramsMap.get("symbol");
        int shares = Integer.parseInt(paramsMap.get("shares"));

        Portfolio p;
        try{
            p = brokerage.sellShares(clientId, symbol, shares);
        }
        catch(Exception e){
            System.out.println("failed to sell shares");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, e.toString().length());

            // Write the JSON response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(e.toString().getBytes());
            }
            return;
        }
        
        System.out.println("sold shares");
        
        // Convert the JSON object to a string
        JSONObject jsonResponse = portfolioToJSON(p);
        // Set response headers
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.toString().length());

        // Write the JSON response to the output stream
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.toString().getBytes());
        }
        System.out.println("Made sell request");
        
    }

    // JSON Utility Methods

    private JSONObject exceptionToJSON(Exception e) {
        JSONObject mainJson = new JSONObject();
        mainJson.put("type", e.getClass().getSimpleName());
        mainJson.put("message", e.getMessage());

        JSONArray stackTraceArray = new JSONArray();
        for (StackTraceElement element : e.getStackTrace()) {
            stackTraceArray.put(element.toString());
        }

        mainJson.put("stacktrace", stackTraceArray);

        return mainJson;
    }

    private JSONObject portfolioToJSON(Portfolio portfolio) {
        // Create the main JSONObject and populate it
        JSONObject mainJson = new JSONObject();
        mainJson.put("clientId", portfolio.getClientId());
        mainJson.put("accountBalance", portfolio.getBalance());
        mainJson.put("stockPositions", new JSONObject(portfolio.getStockPositions()));

        return mainJson;
    }

    private JSONObject messageToJSON(String message) {
        return new JSONObject().put("message", message);
    }
    private HashMap<String, String> getParameters(URI uri){
        String query = uri.getQuery();
        String[] params = query.split("&");
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        for(String param: params){
            String[] keyValue = param.split("=");
            paramsMap.put(keyValue[0], keyValue[1]);
        }
        return paramsMap;
    }

}
