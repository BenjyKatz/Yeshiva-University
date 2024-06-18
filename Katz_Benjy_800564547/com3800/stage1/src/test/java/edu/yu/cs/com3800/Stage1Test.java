package edu.yu.cs.com3800;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.yu.cs.com3800.stage1.Client;
import edu.yu.cs.com3800.stage1.ClientImpl;
import edu.yu.cs.com3800.stage1.SimpleServerImpl;

import static org.junit.Assert.assertEquals;
import org.junit.*;
import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Stage1Test {
    static SimpleServer ss;
    @BeforeClass
    public static void setUpBeforeClass() throws IOException{
        //ss = new SimpleServerImpl(8080);
        //ss.start();
    }
    @AfterClass
    public static void endAllAfterClass() throws IOException{
        //ss.stop(); 
    }
    @Test
    public void testServer() throws IOException{
        
        URL url = new URL("http://10.145.10.207:9000/compileandrun");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-type", "text/x-java-source");
        String postData = "public class HelloWorld {public String run() {return (\"Hello, World!\");}}";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes());
        }
        // Read the response
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        // Assert that the response is as expected
        assertEquals(200, responseCode);
        assertEquals("Hello, World!", response.toString());
    }


    @Test
    public void testServerError() throws IOException{

        URL url = new URL("http://10.145.10.207:9000/compileandrun");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-type", "text/x-java-source");

        String postData = "public class HelloWorld {public String run() {retrurn (\"Hello, World!\");}}";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes());
        }
        // Read the response
        int responseCode = connection.getResponseCode();
        System.out.println("responseCode: "+responseCode);
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        

        // Assert that the response is as expected
        assertEquals(400, responseCode);
        System.out.println(response);
    }
    
    @Test
    public void testClient() throws IOException{
        System.out.println("testClient");
        ClientImpl client;
        try{
            client = new ClientImpl("localhost", 8080);
            String postData = "public class HelloWorld {public String run() {return (\"Hello, World!\");}}";
            client.sendCompileAndRunRequest(postData);
            Client.Response r= client.getResponse();
            System.out.println(r.getCode());
            System.out.println(r.getBody());
            assertEquals(200, r.getCode());
            assertEquals("Hello, World!", r.getBody());
        }
        catch(MalformedURLException e){
            System.out.println(e);
        }
        

    }
    @Test
    public void testValidCode() throws IOException {
        Client client = new ClientImpl("localhost", 8080); 
        String postData = "public class HelloWorld {public String run() {return (\"Hello, World!\");}}";
        client.sendCompileAndRunRequest(postData);
        ClientImpl.Response response = client.getResponse();
        Assert.assertEquals("wrong response code", 200, response.getCode());
        Assert.assertTrue("wrong output from running java code", response.getBody().equals("Hello, World!"));
      
    }
    /*@Test
    public void testBadURL() throws IOException {
        try{
            Client client = new ClientImpl("wafflehousehost", 808000); 
            Assert.assertEquals(0,1);
            System.out.println("Hey heres the client: "+ client);
        }
        catch(MalformedURLException e){
            System.out.println("exception jerbnvjewbvibfen: "+e);
            Assert.assertEquals(0,0);
        }

        
    }*/
    @Test
    public void testStage1() throws IOException {
    var server = new SimpleServerImpl(8625);
    var client = new ClientImpl("localhost", 8625);
    String src = """
                    public class HelloWorld {
                    public String run() {
                    return "Hello World";
                    }
                    }
                    """;

    String expected = "Hello World";
    server.start();

    client.sendCompileAndRunRequest(src);
    var response = client.getResponse();
    assertEquals(expected, response.getBody());
    assertEquals(200, response.getCode());

    client = new ClientImpl("localhost", 8625);
    client.sendCompileAndRunRequest(src);
    response = client.getResponse();
    assertEquals(expected, response.getBody());
    assertEquals(200, response.getCode());

    server.stop();
    }
}
