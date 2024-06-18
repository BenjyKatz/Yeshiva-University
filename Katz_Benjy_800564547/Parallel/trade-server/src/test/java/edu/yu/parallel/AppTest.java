
package edu.yu.parallel;

import edu.yu.parallel.Impl.BrokerServiceImpl;
import edu.yu.parallel.Impl.BrokerageImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.CountDownLatch;


import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;


public class AppTest {
    private TradeServer server;
    private BrokerService brokerService;
    private BrokerageImpl brokerage;
    private StockPrices stockPrices;

    @BeforeEach
    public void setup() throws IOException {
        System.out.println("starting server in test");
        List<Stock> stocks = new StockReader("stocks.csv").getStockList(15);
        StockPrices sp = StockPrices.getInstance(stocks);
        sp.startUpdates();
        this.brokerage = new BrokerageImpl(stocks, sp);
        this.brokerService = new BrokerServiceImpl(brokerage);
        this.server = new TradeServer(8080, 4, brokerService);
        this.server.start();
    }

    @AfterEach
    public void teardown() {
        this.server.stop(0);
        // Wait briefly
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Insufficient Funds Test")
    public void insufficientFundsTest() throws IOException, InterruptedException, ExecutionException {
        System.out.println("Insuf funds test");
        // Register a new client
        //this should be 10000
        HttpURLConnection registrationResult = buildURLRegister("Client1", 10000.0);
        // Submit buy requests after registration has completed
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<HttpURLConnection> buy1Result = executorService.submit(() -> buildURLBuy("Client1", "HD", 20));
        Future<HttpURLConnection> buy2Result = executorService.submit(() -> buildURLBuy("Client1", "AAPL", 20));

        // Shutdown the executor and wait for tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);


        // Assert response codes
        int buy1ResponseCode = buy1Result.get().getResponseCode();
        int buy2ResponseCode = buy2Result.get().getResponseCode();
        // Since there was only enough money to complete one of the purchases, one of the requests should have failed with code 400
        assertTrue((buy1ResponseCode == 200 && buy2ResponseCode == 400) || (buy1ResponseCode == 400 && buy2ResponseCode == 200));
        
    }

    @Test
    @DisplayName("Insufficient Shares Test")
    public void insufficientSharesTest() throws IOException, InterruptedException, ExecutionException {
        System.out.println("\n\n\n\n\n\n\n");
        System.out.println("@@@@Insufficient Shares Test");
        registerClients();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit buy requests after registration has completed
        Future<HttpURLConnection> buy1Result = executorService.submit(() -> buildURLBuy("Alpha", "INTC", 600));
        Future<HttpURLConnection> buy2Result = executorService.submit(() -> buildURLBuy("Beta", "INTC", 600));

        // Shutdown the executor and wait for tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // Assert response codes
        int buy1ResponseCode = buy1Result.get().getResponseCode();
        int buy2ResponseCode = buy2Result.get().getResponseCode();

        // Print out the response codes
        System.out.println("Buy 1 Response Code: " + buy1ResponseCode);
        System.out.println("Buy 2 Response Code: " + buy2ResponseCode);

        // Since there was only enough shares to complete one of the purchases, one of the requests should have failed with code 400
        assertTrue((buy1ResponseCode == 200 && buy2ResponseCode == 400) || (buy1ResponseCode == 400 && buy2ResponseCode == 200));
    }

    @Test
    @DisplayName("Selling Shares Test")
    public void sellTest() throws IOException, InterruptedException, ExecutionException{
        buildURLRegister("JL", 1000.0);
        buildURLBuy("JL", "KO", 10);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // Only enough shares for one sale to succeed
        Future<HttpURLConnection> sell1 = executorService.submit(() -> buildURLSell("JL", "KO", 6));
        Future<HttpURLConnection> sell2 = executorService.submit(() -> buildURLSell("JL", "KO", 6));
        int code1 = sell1.get().getResponseCode();
        int code2 = sell2.get().getResponseCode();
        System.out.println("1: " + code1);
        System.out.println("2: " + code2);
        assertTrue((code1 == 200 && code2 == 400) || (code1 == 400 && code2 == 200));
    }

    @Test
    @DisplayName("Insufficient Funds Test - only 1 works")
    public void insufficientFundsTest2() throws IOException, InterruptedException, ExecutionException {
        // Register a new client
        HttpURLConnection registrationResult = buildURLRegister("Client1", 50);
        // Submit buy requests after registration has completed
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<HttpURLConnection> buy1Result = executorService.submit(() -> buildURLBuy("Client1", "AAPL", 20));
        Future<HttpURLConnection> buy2Result = executorService.submit(() -> buildURLBuy("Client1", "INTC", 1));

        // Shutdown the executor and wait for tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);


            // Assert response codes
        int buy1ResponseCode = buy1Result.get().getResponseCode();
        int buy2ResponseCode = buy2Result.get().getResponseCode();


        // Print out the response codes
        System.out.println("Buy 1 Response Code: " + buy1ResponseCode);
        System.out.println("Buy 2 Response Code: " + buy2ResponseCode);

        // Since there was only enough money to complete one of the purchases, one of the requests should have failed with code 400
        assertTrue(buy1ResponseCode == 400 && buy2ResponseCode == 200);
    }

    @Test
    @DisplayName("Market Value test")
    public void getMarketValueTest() throws IOException, InterruptedException, ExecutionException {
        // Register a new client
        HttpURLConnection registrationResult = buildURLRegister("Client1", 10000);
        // Submit buy requests after registration has completed
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<HttpURLConnection> buy1Result = executorService.submit(() -> buildURLBuy("Client1", "AAPL", 20));
        Future<HttpURLConnection> buy2Result = executorService.submit(() -> buildURLBuy("Client1", "INTC", 10));

        // Shutdown the executor and wait for tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        TimeUnit.SECONDS.sleep(6);

        ExecutorService executorService2 = Executors.newFixedThreadPool(1);
        Future<HttpURLConnection> marketValueResult = executorService2.submit(() -> buildURLMarketValue("Client1"));
        executorService2.shutdown();
        executorService2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            // Assert response codes
        int buy1ResponseCode = buy1Result.get().getResponseCode();
        int buy2ResponseCode = buy2Result.get().getResponseCode();

        BufferedReader reader = new BufferedReader(new InputStreamReader(marketValueResult.get().getInputStream()));

        // Step 4: Read and process the response
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        // Print or process the response
        System.out.println("Response from server:\n" + response.toString());

        reader.close();

        var mv = marketValueResult.get();
        System.out.println("\n\n\n\n"+mv.toString());
        // Print out the response codes
        
        System.out.println("Buy 1 Response Code: " + buy1ResponseCode);
        System.out.println("Buy 2 Response Code: " + buy2ResponseCode);

        // Since there was only enough money to complete one of the purchases, one of the requests should have failed with code 400
        //assertTrue(buy1ResponseCode == 400 && buy2ResponseCode == 200);
    }

    @Test
    @DisplayName("Insufficient Shares Test - only 1 works")
    public void insufficientSharesTest2() throws IOException, InterruptedException, ExecutionException {
        registerClients();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit buy requests after registration has completed
        Future<HttpURLConnection> buy1Result = executorService.submit(() -> buildURLBuy("Alpha", "INTC", 1100));
        Future<HttpURLConnection> buy2Result = executorService.submit(() -> buildURLBuy("Beta", "INTC", 600));

        // Shutdown the executor and wait for tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // Assert response codes
        int buy1ResponseCode = buy1Result.get().getResponseCode();
        int buy2ResponseCode = buy2Result.get().getResponseCode();

        // Print out the response codes
        System.out.println("Buy 1 Response Code: " + buy1ResponseCode);
        System.out.println("Buy 2 Response Code: " + buy2ResponseCode);

        // Since there was only enough shares to complete one of the purchases, one of the requests should have failed with code 400
        assertTrue(buy1ResponseCode == 400 && buy2ResponseCode == 200);
    }

    private static HttpURLConnection buildURLRegister(String clientId, double funds) throws IOException {
        URL url = new URL("http://localhost:8080/register?clientId=" + clientId + "&funds=" + funds);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Send the request to the server
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json"); // Make sure content-type is correct
        conn.setDoOutput(true); // enable the program to write to the request body
        System.out.println(conn);
        // Print statements to confirm the HttpURLConnection object is created
        System.out.println(conn.getResponseCode());
        return conn;
    }

    private static HttpURLConnection buildURLMarketValue(String clientId) throws IOException{
        URL url = new URL("http://localhost:8080/marketvalue?clientId="+clientId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Send the request to the server
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json"); // Make sure content-type is correct
        conn.setDoOutput(true); // enable the program to write to the request body
        System.out.println("marketvalue: " + conn.getResponseCode());
        return conn;
    }
    private static HttpURLConnection buildURLBuy(String clientId, String symbol, int shares) throws IOException{
        URL url = new URL("http://localhost:8080/buy?clientId=" + clientId + "&symbol=" + symbol + "&shares=" + shares);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Send the request to the server
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json"); // Make sure content-type is correct
        conn.setDoOutput(true); // enable the program to write to the request body
        System.out.println("buy: " + conn.getResponseCode());
        return conn;
    }

    private static HttpURLConnection buildURLSell(String clientId, String symbol, int shares) throws IOException{
        URL url = new URL("http://localhost:8080/sell?clientId=" + clientId + "&symbol=" + symbol + "&shares=" + shares);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json"); // Make sure content-type is correct
        conn.setDoOutput(true); // enable the program to write to the request body
        return conn;
    }

    private static void registerClients() throws IOException{
        HttpURLConnection a = buildURLRegister("Alpha", 10000000);
        HttpURLConnection b = buildURLRegister("Beta", 10000000);
        System.out.println(a.getResponseCode());
        System.out.println(b.getResponseCode());
        System.out.println("Registered Clients");
    }
}
