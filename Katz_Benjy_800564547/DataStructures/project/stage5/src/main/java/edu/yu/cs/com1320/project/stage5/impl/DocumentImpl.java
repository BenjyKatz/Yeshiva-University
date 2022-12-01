package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.stage5.Document;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
//import edu.yu.cs.com1320.project.impl.HashTableImpl;
	


public class DocumentImpl implements Document{
	private long lastAccessed;
	private URI uri;
	private String text;
	private byte[] binaryData;
	private HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
	public DocumentImpl(URI uri, String txt) {
		if(uri == null || txt == null || uri.toString().equals("")||txt.equals("")){
			throw new IllegalArgumentException();
		}
		this.uri = uri;
		this.text = txt;
		this.binaryData = null;
		//this.setLastUseTime(System.nanoTime());
		this.addTextToWordMap();

	}
	public DocumentImpl(URI uri, byte[] binaryData){
		if(uri == null || binaryData == null || uri.toString().equals("")||binaryData.length==0){
			throw new IllegalArgumentException();
		}
		this.uri = uri;
		this.binaryData = binaryData;
		this.text = null;
		//this.setLastUseTime(System.nanoTime());
	}
	private void addTextToWordMap(){
		//delete non letters and numbers
		/*
		String textString = getDocumentTxt();
		for(int i = 0; i<128; i++){
			if(i == 48) i = 58;
			if(i == 65) i = 91;
			if(i == 97) i = 123;
			textString = textString.replaceAll(""+(char)i, "");
		}
		textString = textString.toLowerCase();
		String[] wordArray = textString.split(" ");	
		*/
		String textString = getDocumentTxt();
		textString = textString.toLowerCase();
		char[] charArray = textString.toCharArray();
		String newString = "";
		for(int i =0; i<charArray.length; i++){
			if(charArray[i]<48&&charArray[i]!=32) charArray[i] = 0;
			else if(charArray[i]>57 && charArray[i]<97) charArray[i] = 0;
			else if(charArray[i]>122) charArray[i] = 0;
			else{
				newString= newString+charArray[i];
			}
		}
		textString = newString;
		System.out.println(textString);

		String[] wordArray = textString.split(" ");
		HashSet<String> wordSet = new HashSet<String>();
		for(int i =0; i<wordArray.length; i++){
			wordSet.add(wordArray[i]);
		}		
		//count the words
		//for(int i =0; i<wordArray.size(); i++){
		for(String word: wordSet){
			int wordCount = 0;
		//	System.out.println(wordArray[i]);
			while(!textString.equalsIgnoreCase(textString.replaceFirst(word.toLowerCase(), ""))){
				textString = textString.replaceFirst(word.toLowerCase(), "");
				wordCount++;
				//System.out.println(wordCount);
			}
			if(!word.equals(""))System.out.println(word + wordCount);
			if(!word.equals(""))this.wordMap.put(word, wordCount);
		}
	}
	public String getDocumentTxt(){
	//	this.setLastUseTime(System.nanoTime());
		//if(this.text == null){
		//	return "";
		//}
		/*else*/ return this.text;
	}
	public byte[] getDocumentBinaryData(){
	//	this.setLastUseTime(System.nanoTime());
		/*if(binaryData == null){
			return this.text.getBytes();
		}
		else*/ return this.binaryData;
	}
	public URI getKey(){
	//	this.setLastUseTime(System.nanoTime());
		return this.uri;
	}
	/**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    public int wordCount(String word){
   // 	this.setLastUseTime(System.nanoTime());
    	if(this.getDocumentTxt() == null) return 0;
    	if(this.wordMap.get(word.toLowerCase()) == null) return 0;
    	return (int)this.wordMap.get(word.toLowerCase());

    }
    /**
     * @return all the words that appear in the document
     */
    public Set<String> getWords(){
    //	this.setLastUseTime(System.nanoTime());
    	return this.wordMap.keySet();
    }
    public long getLastUseTime(){
    	return this.lastAccessed;
    }
    public void setLastUseTime(long timeInNanoseconds){
    	this.lastAccessed = timeInNanoseconds;

    }
	@Override
	public int hashCode() {
	//	this.setLastUseTime(System.nanoTime());
		int result = uri.hashCode();
		result = 31 * result + (text != null ? text.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(binaryData);
		return result;
	}
	@Override
	public boolean equals(Object other){
	//	this.setLastUseTime(System.nanoTime());
		if(other ==null) return false;
		if(!(other instanceof Document)) return false;
		if(other==this)return true;
		if(this.hashCode()==other.hashCode()) return true;
		return false;

	}
	@Override
	public int compareTo(Document other){
	//	this.setLastUseTime(System.nanoTime());
		return (int)(this.getLastUseTime()-other.getLastUseTime());
	}
	/**
     * @return a copy of the word to count map so it can be serialized
     */
    public Map<String,Integer> getWordMap(){
    //	if(this.text != null){
    	Map<String, Integer> wordMapToreturn = new HashMap<String, Integer>();
    	wordMapToreturn.putAll(this.wordMap);
    	return wordMapToreturn;
    //	}
    	//else return new HashMap<String, Integer>();
    }

    /**
     * This must set the word to count map during deserialization
     * @param wordMap
     */
    public void setWordMap(Map<String,Integer> wordMap){
    	this.wordMap.clear();
    	this.wordMap.putAll(wordMap);

    }
	
	
}