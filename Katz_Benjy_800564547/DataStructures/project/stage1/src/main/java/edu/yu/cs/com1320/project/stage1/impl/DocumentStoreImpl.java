package edu.yu.cs.com1320.project.stage1.impl;
import edu.yu.cs.com1320.project.stage1.*;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import java.net.URI;
import java.io.*;

public class DocumentStoreImpl implements DocumentStore{

	
    HashTable<URI, Document> documentHashTable;
	public DocumentStoreImpl(){

		this.documentHashTable = new HashTableImpl<URI, Document>();
	}
	
	/**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     */
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException{
    	
    	byte[] data = null;
    	Document document = null;
    	if(input!=null){
	    	data = input.readAllBytes();
	    	
	    	
	    	if(format == DocumentFormat.TXT){
	    		String stringContent = new String(data);
	    		document = new DocumentImpl(uri, stringContent);
	    	}
	    	else{
	    		byte[] byteContent = data;
	    		document = new DocumentImpl(uri, byteContent);
	    	}
    	}	
    		Document prevDoc = this.documentHashTable.put(uri, document);
    		if(prevDoc==null) return 0;
    		
    		else return prevDoc.hashCode();	
    		

    }
    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri){
    	return this.documentHashTable.get(uri);

    }
    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean deleteDocument(URI uri){
    	try{
    		if(this.putDocument(null, uri, DocumentFormat.TXT) == 0) return false;
    		else return true;
    	}
    	catch(IOException e){
    		return false;
    	}

    }

}

