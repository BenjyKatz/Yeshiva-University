package edu.yu.cs.com1320.project.stage4.impl;
import edu.yu.cs.com1320.project.stage4.*;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.Stack;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.*;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.Undoable;
import java.util.function.Function;
import java.util.Comparator;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.Trie;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

public class DocumentStoreImpl implements DocumentStore{

	
    private HashTable<URI, Document> documentHashTable;
    private Stack<Undoable> commandStack;
    private boolean undoing = false;
    private Trie<Document> documentTrie;
    private MinHeap<Document> heap;
    private boolean isDocLimit;
    private boolean isByteLimit;
    private int docLimit;
    private int byteLimit;
    private int totalSavedDocs;
    private int totalSavedBytes;
   // private long startTime;
    

	public DocumentStoreImpl(){
		this.documentHashTable = new HashTableImpl<URI, Document>();
        this.commandStack = new StackImpl<Undoable>();
        this.documentTrie = new TrieImpl<Document>();
        this.heap = new MinHeapImpl<Document>();
        this.isByteLimit = false;
        this.isDocLimit = false;
        this.totalSavedBytes = 0;
        this.totalSavedDocs = 0;
    //    this.startTime = System.nanoTime();
        
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
        if(format == null) throw new IllegalArgumentException();
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
            document.setLastUseTime(System.nanoTime()/*-this.startTime*/);

    	}
          
          Document prevDoc = this.addDoc(uri, document);
          
          this.putDocumentInTrie(document);

          if(prevDoc!=null){
            Set<String> setOfWords = prevDoc.getWords();
            for(String word: setOfWords){
                this.documentTrie.delete(word.toLowerCase(), prevDoc);
            }
          }
          
      
        Function<URI, Boolean> remove = (URI uriToRemove)->{ 
            System.out.println("prevDoc: "+prevDoc);
            Set<String> setOfWords = getDocument(uriToRemove).getWords();
            for(String word: setOfWords){
                this.documentTrie.delete(word.toLowerCase(), getDocument(uriToRemove));
            }
            //newly added
            this.removeFromHeap(getDocument(uriToRemove));
            this.addDoc(uriToRemove, prevDoc);

            if(prevDoc!=null){
                this.putDocumentInTrie(prevDoc);   
            }

            return true;
        };
            if(undoing == false){
    		  this.commandStack.push(new GenericCommand<URI>(uri, remove));
            }
    		if(prevDoc==null){                
                return 0;
            }
    		else{
                return prevDoc.hashCode();
            }    		
    }

    private void putDocumentInTrie(Document document){
        Set<String> setOfWords = document.getWords();
        for(String word: setOfWords){
            System.out.println("Adding doc to trie: "+ word);
            this.documentTrie.put(word.toLowerCase(), document);
        }

    }
    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri){
        Document docToReturn = this.documentHashTable.get(uri);
        if(docToReturn!=null){
            docToReturn.setLastUseTime(System.nanoTime()/*-this.startTime*/);
        }
    	return /*this.documentHashTable.get(uri)*/ docToReturn;

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
        Undoable commandToUndo = this.commandStack.pop();

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
        Stack tempStack = new StackImpl<Undoable>();
        Undoable commandBeingLookedAt;
        this.undoing = true;
        while(commandStack.size()>0){

            commandBeingLookedAt = this.commandStack.pop();
            if((commandBeingLookedAt instanceof GenericCommand) && ((GenericCommand)commandBeingLookedAt).getTarget() == uri){ //if we found it
                //undo it 
                commandBeingLookedAt.undo();
                //add the temp back into the main stack
                while(tempStack.size()>0){
                    commandStack.push((Undoable)tempStack.pop());
                }
                this.undoing = false;
                System.out.println("ComandStack size :"+commandStack.size());
                return;
            }
            else if(commandBeingLookedAt instanceof CommandSet && ((CommandSet)commandBeingLookedAt).containsTarget(uri)){//if we found it and it is in a CommandSet
                ((CommandSet)(commandBeingLookedAt)).undo(uri);//undo it
                CommandSet<URI> commandSetAfterUndo = new CommandSet<URI>();
                Iterator<GenericCommand<URI>> setIterator = ((CommandSet)commandBeingLookedAt).iterator();
                while(setIterator.hasNext()){
                    GenericCommand g = setIterator.next();
                    if(g.getTarget() != uri){
                        commandSetAfterUndo.addCommand(g);
                    }
                }
                if(commandSetAfterUndo.size() != 0){
                    commandStack.push((Undoable)commandSetAfterUndo);
                }
                while(tempStack.size()>0){
                    commandStack.push((Undoable)tempStack.pop());
                }
                this.undoing = false;
                return;
            }
            tempStack.push(commandBeingLookedAt);
        }
        //in case we dont find anything refill the stack
        while(tempStack.size()>0){
            commandStack.push((Undoable)tempStack.pop());
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
        //System.out.println("commandStack size: "+this.commandStack.size());
        System.out.println("uri: "+ uri);
        Document docAboutToBeDeleted = this.documentHashTable.get(uri);
        if(docAboutToBeDeleted == null) return false;//newly added
        Function<URI, Boolean> add = (URI uriToAdd)->{
            if(docAboutToBeDeleted!=null){
                System.out.println("docAboutToBeDeleted text: "+docAboutToBeDeleted.getDocumentTxt());
                docAboutToBeDeleted.setLastUseTime(System.nanoTime()/*-this.startTime*/);
            }
            this.addDoc(uriToAdd, docAboutToBeDeleted);
            this.putDocumentInTrie(docAboutToBeDeleted);
            return true;
        };
            Set<String> setOfWords = docAboutToBeDeleted.getWords();
            for(String word: setOfWords){
                this.documentTrie.delete(word.toLowerCase(), docAboutToBeDeleted);
            }
            this.removeFromHeap(docAboutToBeDeleted);
            
            if(this.addDoc(uri, null) == null){
                if(undoing == false){
                    Function<URI, Boolean> uselessFunction = (URI uriToAdd)->{return true;};
                    this.commandStack.push(new GenericCommand<URI>(uri, uselessFunction));
                }
                return false;
            }
    		else{
                if(undoing == false){
                    // System.out.println("adding to stack commandStack size: "+this.commandStack.size());
                    this.commandStack.push(new GenericCommand<URI>(uri, add));
                }
                return true;
            }
    }
    private void removeFromHeap(Document d){
        //for some reason the document is not in the heap
        System.out.println("Trying to remove from heap: "+ d);
        d.setLastUseTime(0);
        System.out.println("ds last time in use: " + d.getLastUseTime());
        this.heap.reHeapify(d);
        this.heap.remove();
        this.totalSavedDocs--;
        System.out.println("these bytes are being subtracted from total saved bytes "+d.getDocumentTxt());
        this.totalSavedBytes = this.totalSavedBytes - this.getAllBytes(d);
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> search(String keyword){
       // System.out.println("search begun");
        /*try{
            while(true){
                Document d = this.heap.remove();
                System.out.println("all elements currently in heap: "+ d.getDocumentTxt()+" "+ d.getLastUseTime());
            }
        }
        catch(Exception e){
            System.out.println("heap empty");
        }*/
        List<Document> searchResults;
        Comparator<Document> comparator = new Comparator<Document>(){
            @Override
            public int compare(Document firstDoc, Document secondDoc){
                return (firstDoc.wordCount(keyword))-(secondDoc.wordCount(keyword));
            }
        };
        searchResults = this.documentTrie.getAllSorted(keyword, comparator);
        /*try{
            while(true){
                Document d = this.heap.remove();
                System.out.println("all elements currently in heap: "+ d.getDocumentTxt()+" "+ d.getLastUseTime());
            }
        }
        catch(Exception e){
            System.out.println("heap empty");
        }*/
        for(Document d: searchResults){
            
                d.setLastUseTime(System.nanoTime()/*-this.startTime*/);
                this.heap.reHeapify(d);
            
        }
        
       
        return searchResults;
    }
    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> searchByPrefix(String keywordPrefix){
        List<Document> searchResults;
        Comparator<Document> comparator = new Comparator<Document>(){
            @Override
            public int compare(Document firstDoc, Document secondDoc){
                return (firstDoc.wordCount(keywordPrefix.toLowerCase()))-(secondDoc.wordCount(keywordPrefix.toLowerCase()));
            }
        };
        searchResults = this.documentTrie.getAllWithPrefixSorted(keywordPrefix.toLowerCase(), comparator);
       System.out.println("searchResults.size "+ searchResults.size());

        for(Document d: searchResults){
            d.setLastUseTime(System.nanoTime()/*-this.startTime*/);
            this.heap.reHeapify(d);

        }
        return searchResults;
    }
    /**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAll(String keyword){
        Comparator<Document> comparator = new Comparator<Document>(){
            @Override
            public int compare(Document firstDoc, Document secondDoc){
                return (firstDoc.wordCount(keyword))-(secondDoc.wordCount(keyword));
            }
        };
        List<Document> docsToDelete = this.documentTrie.getAllSorted(keyword.toLowerCase(), comparator);
        documentTrie.deleteAll(keyword.toLowerCase());
        Set<URI> urisDeleted = new HashSet<URI>();
        Set<GenericCommand> genericCommandSet = new HashSet<GenericCommand>();
        for(Document d: docsToDelete){
            urisDeleted.add(d.getKey());
            this.undoing = true;
            this.deleteDocument(d.getKey());
            this.undoing = false;
            //documentTrie.delete(keyword.toLowerCase(), d);
            
            Function<URI, Boolean> add = (URI uriToAdd)->{
            //    System.out.println("running undoFunction of deleteall on doc: "+ d+ " with uri: "+uriToAdd);
                this.addDoc(uriToAdd, d);
                this.putDocumentInTrie(d);
               // d.setLastUseTime(System.nanoTime());
                return true;
            };
            genericCommandSet.add(new GenericCommand<URI>(d.getKey(), add));

        }
        
        CommandSet<URI> deleteAllCommand = new CommandSet<URI>();
        for(GenericCommand g: genericCommandSet){
            deleteAllCommand.addCommand(g);
        }
        if(undoing == false){
            this.commandStack.push(deleteAllCommand);
        }
        return urisDeleted;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){
         Comparator<Document> comparator = new Comparator<Document>(){
            @Override
            public int compare(Document firstDoc, Document secondDoc){
                return (firstDoc.wordCount(keywordPrefix))-(secondDoc.wordCount(keywordPrefix));
            }
        };
        List<Document> docsToDelete = this.documentTrie.getAllWithPrefixSorted(keywordPrefix.toLowerCase(), comparator);
      
        Set<URI> urisDeleted = new HashSet<URI>();
        Set<GenericCommand> genericCommandSet = new HashSet<GenericCommand>();
        for(Document d: docsToDelete){
            urisDeleted.add(d.getKey());
            this.undoing = true; // ensure these deletes are not added seperately to the stack
            this.deleteDocument(d.getKey());
            this.undoing = false;
            Function<URI, Boolean> add = (URI uriToAdd)->{
                this.addDoc(uriToAdd, d);
                this.putDocumentInTrie(d);
                //d.setLastUseTime(System.nanoTime());
                return true;
            };
            genericCommandSet.add(new GenericCommand<URI>(d.getKey(), add));
        }
        CommandSet<URI> deleteAllCommand = new CommandSet<URI>();
        for(GenericCommand g: genericCommandSet){
            deleteAllCommand.addCommand(g);
        }
        documentTrie.deleteAllWithPrefix(keywordPrefix.toLowerCase());
        if(undoing == false){
            this.commandStack.push(deleteAllCommand);
        }
        return urisDeleted;

    }
    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    public void setMaxDocumentCount(int limit){
        this.isDocLimit = true;
        this.docLimit = limit;
        while(this.docLimit<this.totalSavedDocs){
            
                this.makeSpace();
        }
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    public void setMaxDocumentBytes(int limit){
        this.isByteLimit = true;
        this.byteLimit = limit;
        System.out.println("There is now a "+ this.byteLimit+" limit and we have used "+ this.totalSavedBytes);
        while(this.byteLimit<this.totalSavedBytes){
            this.makeSpace();
        }

    }
    private boolean hasSpace(Document d){
        int docBytes = this.getAllBytes(d);
        System.out.println("isDocLimit"+ this.isDocLimit);
        System.out.println("isByteLimit"+ this.isByteLimit);
        System.out.println("in hasSpace doc limit is "+this.docLimit+" totalsaved docs is "+ this.totalSavedDocs);
        System.out.println("in hasSpace byte limit is "+this.byteLimit+" totalsaved bytes is "+ this.totalSavedBytes + "the amount of bytes in this doc is "+ docBytes);
        if(this.isDocLimit && this.totalSavedDocs>=this.docLimit){
            return false;
        }
        if(this.isByteLimit && this.totalSavedBytes+docBytes> this.byteLimit){
            return false;
        }
        return true;

    }
    private void makeSpace(Document d){
        while(!hasSpace(d)){
            System.out.println("NO SPACE!");
            Document docAboutToRemove = heap.remove();
            this.totalSavedBytes = this.totalSavedBytes - getAllBytes(docAboutToRemove);
            System.out.println("these bytes are being subtracted from total saved bytes "+docAboutToRemove.getDocumentTxt());
            this.totalSavedDocs--; 
            Set<String> setOfWords = docAboutToRemove.getWords();
            for(String word: setOfWords){
                this.documentTrie.delete(word.toLowerCase(), docAboutToRemove);
            }
            this.documentHashTable.put(docAboutToRemove.getKey(), null);
            System.out.println("Document :"+ docAboutToRemove.getDocumentTxt()+" has been removed");
         //   assert this.getDocument(docAboutToRemove.getKey()) == null:"OH NO ITS NOT NULL";
            docAboutToRemove = null;
        }
    }
    private void makeSpace(){
        Document docAboutToRemove = heap.remove();
        this.totalSavedBytes = this.totalSavedBytes - getAllBytes(docAboutToRemove);
        System.out.println("these bytes are being subtracted from total saved bytes "+docAboutToRemove.getDocumentTxt());
        this.totalSavedDocs--; 
        Set<String> setOfWords = docAboutToRemove.getWords();
        for(String word: setOfWords){
            this.documentTrie.delete(word.toLowerCase(), docAboutToRemove);
        }
        this.documentHashTable.put(docAboutToRemove.getKey(), null);
        docAboutToRemove = null;
    }
    private int getAllBytes(Document d){
        int docBytes = 0;
        boolean isTextType = false;
        if(d.getDocumentTxt()!=null){
            isTextType = true;
        }
        if(isTextType){
            docBytes = d.getDocumentTxt().getBytes().length;
        }
        else if(!isTextType){
            docBytes = d.getDocumentBinaryData().length;
        }
        return docBytes;
    }
    private Document addDoc(URI uri, Document d){
        Document docToReturn;
        if(d!=null){
            System.out.println(uri);
            this.makeSpace(d);
            d.setLastUseTime(System.nanoTime()/*-this.startTime*/);
        }
        docToReturn = this.documentHashTable.put(uri, d);
        if(d!=null){
             System.out.println("these bytes are being added to total saved bytes "+d.getDocumentTxt());
            this.totalSavedBytes = this.totalSavedBytes + this.getAllBytes(d);
        }
        if(docToReturn == null && d!=null){//newly added
            this.totalSavedDocs++;
        }
        if(d!=null){
            this.heap.insert(d);
        }
        return docToReturn;
    }
}

