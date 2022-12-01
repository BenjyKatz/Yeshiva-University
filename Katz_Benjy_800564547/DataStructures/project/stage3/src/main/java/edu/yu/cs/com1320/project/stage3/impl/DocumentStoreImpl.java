package edu.yu.cs.com1320.project.stage3.impl;
import edu.yu.cs.com1320.project.stage3.*;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.Stack;
import java.net.URI;
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
    

	public DocumentStoreImpl(){
		this.documentHashTable = new HashTableImpl<URI, Document>();
        this.commandStack = new StackImpl<Undoable>();
        this.documentTrie = new TrieImpl<Document>();
        
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
        if(format == null){ throw new IllegalArgumentException();}
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
            this.documentHashTable.put(uriToRemove, prevDoc);

            if(prevDoc!=null)this.putDocumentInTrie(prevDoc);

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
            this.documentTrie.put(word, document);
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
            if(docAboutToBeDeleted!=null)System.out.println("docAboutToBeDeleted text: "+docAboutToBeDeleted.getDocumentTxt());
            this.documentHashTable.put(uriToAdd, docAboutToBeDeleted);
            this.putDocumentInTrie(docAboutToBeDeleted);
            return true;
        };
            Set<String> setOfWords = docAboutToBeDeleted.getWords();
            for(String word: setOfWords){
                this.documentTrie.delete(word.toLowerCase(), docAboutToBeDeleted);
            }
            if(this.documentHashTable.put(uri, null) == null){
                Function<URI, Boolean> uselessFunction = (URI uriToAdd)->{return true;};
                this.commandStack.push(new GenericCommand<URI>(uri, uselessFunction));
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

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> search(String keyword){
        List<Document> searchResults;
        Comparator<Document> comparator = new Comparator<Document>(){
            @Override
            public int compare(Document firstDoc, Document secondDoc){
                return (firstDoc.wordCount(keyword))-(secondDoc.wordCount(keyword));
            }
        };
        searchResults = this.documentTrie.getAllSorted(keyword, comparator);
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
        Set<URI> urisDeleted = new HashSet<URI>();
        Set<GenericCommand> genericCommandSet = new HashSet<GenericCommand>();
        for(Document d: docsToDelete){
            urisDeleted.add(d.getKey());
            this.undoing = true;
            this.deleteDocument(d.getKey());
            this.undoing = false;
            documentTrie.delete(keyword.toLowerCase(), d);
            
            Function<URI, Boolean> add = (URI uriToAdd)->{
                System.out.println("running undoFunction of deleteall");
                this.documentHashTable.put(uriToAdd, d);
                this.putDocumentInTrie(d);
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
                this.documentHashTable.put(uriToAdd, d);
                this.putDocumentInTrie(d);
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
}

