package edu.yu.cs.com1320.project.stage2.impl;
import edu.yu.cs.com1320.project.stage2.*;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.Stack;
import java.net.URI;
import java.io.*;
import edu.yu.cs.com1320.project.Command;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore{

	
    private HashTable<URI, Document> documentHashTable;
    private Stack<Command> commandStack;
    private boolean undoing = false;
    

	public DocumentStoreImpl(){
		this.documentHashTable = new HashTableImpl<URI, Document>();
        this.commandStack = new StackImpl<Command>();
        
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
      if(prevDoc!= null)System.out.println("before function prevDoc: "+ prevDoc.getDocumentTxt());
        Function<URI, Boolean> remove = (URI uriToRemove)->{ 
            if(prevDoc!= null)System.out.println("prevDoc: "+ prevDoc.getDocumentTxt());
            this.documentHashTable.put(uriToRemove, prevDoc);
            return true;
        };
            if(undoing == false){
                System.out.println("adding to stack commandStack size: "+this.commandStack.size());
    		  this.commandStack.push(new Command(uri, remove));
            }
    		if(prevDoc==null){                
                return 0;
            }
    		else{
                return prevDoc.hashCode();
            }    		
    }
    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri){
    	return this.documentHashTable.get(uri);

    }
     /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    public void undo() throws IllegalStateException{
        System.out.println("Pre undo() ComandStack size :"+commandStack.size());
        if(this.commandStack.size()==0){
            throw new IllegalStateException();
        }
        this.undoing = true;
        Command commandToUndo = this.commandStack.pop();

        commandToUndo.undo();
        this.undoing = false;
        System.out.println("ComandStack size :"+commandStack.size());

    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException{
        System.out.println("Pre undo(uri) ComandStack size :"+commandStack.size());
        Stack tempStack = new StackImpl<Command>();
        Command commandBeingLookedAt;
        this.undoing = true;
        while(commandStack.size()>0){

            commandBeingLookedAt = this.commandStack.pop();
            if(commandBeingLookedAt.getUri() == uri){ //if we found it
                //undo it 
                commandBeingLookedAt.undo();
                //add the temp back into the main stack
                while(tempStack.size()>0){
                    commandStack.push((Command)tempStack.pop());
                }
                this.undoing = false;
                System.out.println("ComandStack size :"+commandStack.size());
                return;
            }
            tempStack.push(commandBeingLookedAt);
        }
        //in case we dont find anything refill the stack
        while(tempStack.size()>0){
            commandStack.push((Command)tempStack.pop());
        }
        this.undoing = false;
        System.out.println("it never gets here");
        throw new IllegalStateException();

    }
    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean deleteDocument(URI uri){     
        System.out.println("commandStack size: "+this.commandStack.size());
        System.out.println("uri: "+ uri);
        Document docAboutToBeDeleted = this.documentHashTable.get(uri);
        Function<URI, Boolean> add = (URI uriToAdd)->{
            System.out.println("uriToAdd: "+ uriToAdd);
            if(docAboutToBeDeleted!=null)System.out.println("docAboutToBeDeleted text: "+docAboutToBeDeleted.getDocumentTxt());
            this.documentHashTable.put(uriToAdd, docAboutToBeDeleted);
            return true;
        };

            if(this.documentHashTable.put(uri, null) == null){
                Function<URI, Boolean> uselessFunction = (URI uriToAdd)->{return true;};
                this.commandStack.push(new Command(uri, uselessFunction));
                return false;
            }
    		else{
                if(undoing == false){
                     System.out.println("adding to stack commandStack size: "+this.commandStack.size());
                    this.commandStack.push(new Command(uri, add));
                }
                return true;
            }
    }

}

