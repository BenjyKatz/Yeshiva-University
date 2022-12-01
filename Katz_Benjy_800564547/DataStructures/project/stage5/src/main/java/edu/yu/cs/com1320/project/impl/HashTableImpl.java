package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key, Value>  implements HashTable<Key, Value> {
	class Entry<Key, Value>{
		Value value;
		Key key;
		Entry(Key k, Value v){
			if(k==null){
				throw new IllegalArgumentException();
			}
			else{
				this.key = k;
				this.value = v;
			}
		}
	}

	class ListNode<Key, Value>{
		public Entry data;
		public ListNode next;
		public ListNode(Entry data){
			this(data, null);
		}
		public ListNode(Entry data, ListNode next){
			this.data = data;
			this.next = next;
		}
	}

	private ListNode[] table;
	private int arraySize;
	private int tableOccupancy;

	public HashTableImpl(){
		this.tableOccupancy = 0;
		this.arraySize = 5;
		this.table = new ListNode[this.arraySize];//I want this to be an array of list nodes
		
	}

	private int hashFunction(Key k){
		return((k.hashCode()& 0x7fffffff)%this.arraySize);
	}

	public Value put(Key k, Value v){
		this.tableOccupancy++;
		if(this.tableOccupancy> this.arraySize*4) this.arrayDouble();
		return this.put(k, v, this.table);
	}
	private Value put(Key k, Value v, ListNode[] arrayTable){
		int index = this.hashFunction(k);
		Value valToReturn = null;
		if(arrayTable[index] == null){
			//construct a new list
			ListNode list = new ListNode(null);
			list.next = new ListNode(new Entry(k,v));//added data as null
			arrayTable[index]=list;
			return null;	
		}
		ListNode current = arrayTable[index];

		while(current.next != null){	
			//if the keys are the same return the previous value
			if(/*current.next!= null &&*/ current.next.data.key.hashCode()==k.hashCode()){
				valToReturn = (Value)current.next.data.value;		
				//remove the Entry
				current.next = current.next.next;
			}
			else{
			
				current = current.next;
			}
		}
		if(v!=null){
			
			current.next = new ListNode(new Entry(k, v));
		}
		return valToReturn;		
	}

	public Value get(Key k){
		int index = this.hashFunction(k);
		ListNode current = this.table[index];
		if(current == null) return null;
		while(current.next != null  && current.next.data.key.hashCode()!=k.hashCode()){
			current = current.next;
		}
		if(current.next!=null){
			return (Value)current.next.data.value;
		}

		return null;
	}
	private void arrayDouble(){
		this.arraySize = this.arraySize*2;
		ListNode[] tempTable = new ListNode[this.arraySize];
		for(int i = 0; i<this.table.length; i++){
			//i is the index of the old array
			if(this.table[i]==null) continue;//skip the rest of this itteration
			ListNode current = this.table[i];
			while(current.next!=null){
				this.put((Key)current.next.data.key, (Value)current.next.data.value, tempTable);
				current = current.next;
			}
		}
		this.table = tempTable;

	}
	
}


