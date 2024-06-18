package edu.yu.cs.com3800.stage1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ClientImpl implements Client{
    URL url;
    HttpURLConnection connection;
    public ClientImpl(String hostName, int hostPort) throws MalformedURLException{
        url = new URL("http://"+hostName+":"+hostPort+"/compileandrun");
    }
    public void sendCompileAndRunRequest(String src) throws IOException{
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-type", "text/x-java-source");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(src.getBytes());
        }
    }
    public Response getResponse() throws IOException{
        
        int responseCode = connection.getResponseCode();
        StringBuilder responseBody = new StringBuilder();
        if(responseCode == 200){
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            }
        }
        else{
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            }
        }
        return new Response(responseCode, responseBody.toString());

    }
}
