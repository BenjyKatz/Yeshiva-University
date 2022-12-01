 package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.*;
import com.google.gson.stream.*;
import java.lang.reflect.*;

import com.google.gson.stream.JsonWriter;
import javax.xml.bind.DatatypeConverter;
import java.nio.file.*;

//peer tutor Jonah 8596358646 3:00 monday
/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    //private File directory;
    private boolean useDefault = false;
    private String baseDirectory;
    public DocumentPersistenceManager(File baseDir){
        if(baseDir == null){
            this.useDefault = true;
            this.baseDirectory = "user.dir";
        }
        else{
        //  this.directory = baseDir;
            useDefault = false;
            //this.baseDirectory = baseDir.getName();

            this.baseDirectory = baseDir.getPath();//new may 13th
            System.out.println("BaseDir name: "+ this.baseDirectory);
        } 
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        //write as file
        System.out.println("Serializing "+ uri);
       
         Path path = Paths.get(this.baseDirectory,uri.getHost()+uri.getPath());
         Path directory = Files.createDirectories(path.getParent());
         Gson gson = new GsonBuilder().registerTypeAdapter(val.getClass(), new JsonSerializer<Document>(){ 
            @Override
            public JsonElement serialize(Document doc, Type typeOfSrc, JsonSerializationContext context) {     
                Gson gsonSer = new Gson(); 
                //GsonBuilder builder = new GsonBuilder();

                JsonObject jsonDocument = new JsonObject();

                if(val.getDocumentTxt() != null){
                    String docText = val.getDocumentTxt();
                    jsonDocument.addProperty("text", docText);
                }
                else if(val.getDocumentBinaryData() != null){
                    String base64Encoded = DatatypeConverter.printBase64Binary(val.getDocumentBinaryData());
                    jsonDocument.addProperty("binaryData", base64Encoded);
                }
                JsonElement uriSerialized = gsonSer.toJsonTree(uri);
                System.out.println("uriSerialized "+uriSerialized);
                jsonDocument.add("uri", uriSerialized);
                JsonElement wordMap = gsonSer.toJsonTree(val.getWordMap(), HashMap.class);
                
                jsonDocument.add("wordMap", wordMap);
                JsonElement jsonElement = gsonSer.fromJson(jsonDocument.toString(), JsonElement.class);
                return jsonElement;
            }}).create();
        
         String fileName = path.getFileName().toFile().toString()+".json";
         File fileForJson = new File(directory.toString(), fileName);
         FileWriter fileWriter = new FileWriter(fileForJson);
         //Gson gson = new Gson();
 
         JsonWriter jsonWriter = new JsonWriter(fileWriter);
         gson.toJson(val, val.getClass(), jsonWriter);
         fileWriter.close();
         jsonWriter.close();
         System.out.println("it wrote");
         
        //Deserialize just for example to see if it will compile
       // Document doc = gson.fromJson(, Document.class);
        //System.out.println(doc);

    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        System.out.println("Deserializing "+ uri);
        Path path = Paths.get(this.baseDirectory,uri.getHost()+uri.getPath());
        Path directory = Files.createDirectories(path.getParent());
        String fileName = path.getFileName().toFile().toString()+".json";
        System.out.println("Directory "+directory.toString());
        System.out.println("file name "+fileName);
        File fileForJson = new File(directory.toString(), fileName);
        
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new JsonDeserializer<Document>(){ 
            @Override
            public Document deserialize(JsonElement element, Type typeOfSrc, JsonDeserializationContext context) {     
                Gson gsonDes = new Gson(); 
                //GsonBuilder builder = new GsonBuilder();


                JsonObject jsonObject = element.getAsJsonObject();
                JsonElement textEl = jsonObject.get("text");
                boolean isTextDoc = false;
                String text = "";
                byte[] binaryData = {}; 
                if(textEl != null){
                    text = textEl.toString();
                    isTextDoc = true;
                }
                else{
                    JsonElement binaryEl = jsonObject.get("binaryData");
                    String binaryString = binaryEl.toString();
                    byte[] base64Decoded = DatatypeConverter.parseBase64Binary(binaryString);
                    binaryData = base64Decoded;
                    isTextDoc = false;
                }

                JsonElement wordMapEl = jsonObject.get("wordMap");

                HashMap/*<String, Integer>*/ wordMap = gsonDes.fromJson(wordMapEl, HashMap.class);
                System.out.println("It gets here");

                if(isTextDoc){
                    System.out.println("decoded stuff "+ uri + text);
                    DocumentImpl docImpl = new DocumentImpl(uri, text);
                    docImpl.setWordMap(wordMap);
                    docImpl.setLastUseTime(System.nanoTime());
                    return docImpl;
                }
                else{
                    DocumentImpl docImpl = new DocumentImpl(uri, binaryData);
                    docImpl.setWordMap(wordMap);
                    docImpl.setLastUseTime(System.nanoTime());
                    return docImpl;
                }
                
            }}).create();
      //  Gson gson = new Gson();
        System.out.println("File reader "+fileForJson);
        JsonReader jsonReader = new JsonReader(new FileReader(fileForJson));
        System.out.println("did it make it this far");
        DocumentImpl document = gson.fromJson(jsonReader, DocumentImpl.class);
        fileForJson.delete();
        System.out.println("did it make it this far");
        return document;

    }
    /**
     * delete the file stored on disk that corresponds to the given key
     * @param key
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    @Override
    public boolean delete(URI uri) throws IOException {
        System.out.println("calling delete in DPM");
        try{
            Document doc = deserialize(uri);
            doc = null;
            return true;
        }
        catch(FileNotFoundException e){
            return false;
        }
    }
    
}

