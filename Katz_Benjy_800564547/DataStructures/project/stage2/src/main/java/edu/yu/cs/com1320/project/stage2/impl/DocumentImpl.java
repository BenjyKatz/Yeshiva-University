package edu.yu.cs.com1320.project.stage2.impl;
import edu.yu.cs.com1320.project.stage2.Document;
import java.net.URI;
import java.util.Arrays;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
	


public class DocumentImpl implements Document{
	private URI uri;
	private String text;
	private byte[] binaryData;
	public DocumentImpl(URI uri, String txt) {
		if(uri == null || txt == null || uri.toString().equals("")||txt.equals("")){
			throw new IllegalArgumentException();
		}
		this.uri = uri;
		this.text = txt;
		this.binaryData = null;

	}
	public DocumentImpl(URI uri, byte[] binaryData){
		if(uri == null || binaryData == null || uri.toString().equals("")||binaryData.length==0){
			throw new IllegalArgumentException();
		}
		this.uri = uri;
		this.binaryData = binaryData;
		this.text = null;
	}
	public String getDocumentTxt(){
		//if(this.text == null){
		//	return "";
		//}
		/*else*/ return this.text;
	}
	public byte[] getDocumentBinaryData(){
		/*if(binaryData == null){
			return this.text.getBytes();
		}
		else*/ return this.binaryData;
	}
	public URI getKey(){
		return this.uri;
	}
	@Override
	public int hashCode() {
		int result = uri.hashCode();
		result = 31 * result + (text != null ? text.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(binaryData);
		return result;
	}
	@Override
	public boolean equals(Object other){
		if(other ==null) return false;
		if(!(other instanceof Document)) return false;
		if(other==this)return true;
		if(this.hashCode()==other.hashCode()) return true;
		return false;

	}

	
	
}