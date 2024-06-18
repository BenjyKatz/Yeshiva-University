package edu.yu.parallel;

public class Stock {

    // Complete this class as necessary
    double previousClose;
    String symbol;
    String name;
    double price;

    public Stock(String symbol, String name, double previousClose) {
        this.symbol = symbol;
        this.name = name;
        this.previousClose = previousClose;
        this.price = previousClose;
    }
    public double getPreviousClose(){
        return this.previousClose;
    }
    public String getSymbol(){
        return this.symbol;
    }
    public String getName(){
        return this.name;
    }
    public void setPrice(double x){
        synchronized(this) {
            this.price = x;
        }
    }
    public double getPrice(){
        return this.price;
    }

}
