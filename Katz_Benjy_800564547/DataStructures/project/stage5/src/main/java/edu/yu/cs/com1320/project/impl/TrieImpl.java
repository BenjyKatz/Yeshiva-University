package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.Trie;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.Comparator;

public class TrieImpl<Value> implements Trie<Value>{
	private static final int alphabetSize = 256; // extended ASCII
    private Node root; // root of trie

    public static class Node<Value>
    {
        protected Set<Value> setOfVals = new HashSet();
        protected Node[] links = new Node[TrieImpl.alphabetSize];
    }

	/**
     * add the given value at the given key
     * @param key
     * @param val
     */
    public void put(String key, Value val){
    	if(key == null) throw new IllegalArgumentException();
    	if(val!= null){
    		this.root = this.put(this.root,  key.toLowerCase(),  val,  0);
    	}
    }
    private Node put(Node nodeWeAreUpTo, String key, Value val, int letterInKeyWeAreUpTo){
    	//if(val == null) return;
    	if(nodeWeAreUpTo == null){
    		
    		nodeWeAreUpTo = new Node<Value>();
    		
    	}
    	if(letterInKeyWeAreUpTo == key.length()){
            System.out.println("added: "+val);
    		nodeWeAreUpTo.setOfVals.add(val);
    		return nodeWeAreUpTo;
    	}
    	char c = key.charAt(letterInKeyWeAreUpTo);
    	nodeWeAreUpTo.links[c] = this.put(nodeWeAreUpTo.links[c], key, val, letterInKeyWeAreUpTo + 1);
        return nodeWeAreUpTo;
    }
    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    public List<Value> getAllSorted(String key, Comparator<Value> comparator){
    	if(key == null|| comparator ==null) throw new IllegalArgumentException();
    	Node nodeWithKey = this.get(this.root, key.toLowerCase(), 0);
        if(nodeWithKey == null) return new LinkedList<Value>();//newly added
    	Set<Value> thisSetOfVals = nodeWithKey.setOfVals;
    	LinkedList<Value> listOfVals = new LinkedList<>();
    	for(Value v: thisSetOfVals){
    		listOfVals.add(v);
    	}
    	return this.sort(listOfVals, comparator);
    }
    private List<Value> sort(LinkedList<Value> collection, Comparator<Value> comparator){
    	ArrayList<Value> arrayOfVals = new ArrayList<>();
    	ArrayList<Value> largeToSmallArrayOfVals = new ArrayList<>();
    	/*Value[] arrayOfVals = new Value[collection.size()];
    	int index = 0;
    	for(Value val: collection){
    		arrayOfVals[index] = val;
    		index++;
    	}
    	for(int i = collection.size()-1; i>=0; i--){
    		int p = i;
    		while(comparator.compareTo(arrayOfVals[p], arrayOfVals[p-1])>0){
    			//swap them
    			Value temp = arrayOfVals[p];
    			arrayOfVals[p] = arrayOfVals[p-1];
    			arrayOfVals[p-1] = temp;
    			p--;
    		}
    	}
    	return Arrays.asList(arrayOfVals);*/
    	arrayOfVals.addAll(collection);
    	arrayOfVals.sort(comparator);

    	// int lastIndex = arrayOfVals.size()-1;
    	// //reverse order
    	// for(int i = lastIndex; i>=0; i--){
    	// 	largeToSmallArrayOfVals.add(arrayOfVals.get(i));
    	// }

    	// return largeToSmallArrayOfVals;
        return arrayOfVals;//newly added and the previos was newly deleted
    }
    private Node get(Node nodeWeAreUpTo, String key, int charWeAreUpTo){

    	if(nodeWeAreUpTo == null){
    		System.out.println("its null");
    		return null;
    	}
    	if(charWeAreUpTo == key.length()){
    		return nodeWeAreUpTo;
    	}
    	char c = key.charAt(charWeAreUpTo);
    	return this.get(nodeWeAreUpTo.links[c], key, charWeAreUpTo+1);
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        System.out.println("The root when calling getAllWithPrefixSorted: "+ this.root);
    	if(prefix == null|| comparator == null) throw new IllegalArgumentException();
    	Set<Value> values = new HashSet<Value>();
    	LinkedList<Value> listOfVals = new LinkedList<Value>();
    	Node x = this.get(this.root, prefix.toLowerCase(), 0);
    
    	if(x!=null){
    	
    		this.collect(x, new StringBuilder(prefix.toLowerCase()),values);
    	}
    	for(Value v: values){
    		listOfVals.add(v);
    	}
    	return(this.sort(listOfVals,comparator));
    }
    private void collect(Node x, StringBuilder prefix, Set<Value> values){
    	values.addAll(x.setOfVals);
    	for(char c = 0; c<TrieImpl.alphabetSize; c++){
    		if(x.links[c]!=null){
    			prefix.append(c);
    			this.collect(x.links[c], prefix, values);
    			prefix.deleteCharAt(prefix.length()-1);
    		}
    	}
    }
	/**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAll(String key){
    	if(key == null) throw new IllegalArgumentException();
    	Set<Value> setToReturn = new HashSet<>();
    	if(this.get(this.root, key.toLowerCase(), 0)== null) return null;
    	setToReturn.addAll(this.get(this.root, key.toLowerCase(), 0).setOfVals);

    	this.get(this.root, key.toLowerCase(), 0).setOfVals.clear();
    	return setToReturn;
    }
    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    public Value delete(String key, Value val){
    	if(key == null|| val == null) throw new IllegalArgumentException();
    	if(this.get(root, key.toLowerCase(), 0) == null) return null;
    	if(this.get(root, key.toLowerCase(), 0).setOfVals.remove(val)){
    		return val;
    	}
    	else return null;
    }
    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAllWithPrefix(String prefix){
    	if(prefix == null) throw new IllegalArgumentException();
    	Set<Value> toBeDeleted = new HashSet<Value>();
    	Set<Value> setToBeDeleted = new HashSet<Value>();
    	if(this.get(this.root, prefix.toLowerCase(), 0)==null) return null;
    	this.collect(this.get(this.root, prefix.toLowerCase(), 0), new StringBuilder(prefix.toLowerCase()), toBeDeleted);
    	Node nodeToDelete = this.get(this.root, prefix.toLowerCase(), 0);
    	for(int i = 0; i<TrieImpl.alphabetSize; i++){
    		nodeToDelete.setOfVals.clear();
    		nodeToDelete.links[i] = null;
    	}
    	for(Value v: toBeDeleted){
    		setToBeDeleted.add(v);
    	}
    	return setToBeDeleted;
    }

}


