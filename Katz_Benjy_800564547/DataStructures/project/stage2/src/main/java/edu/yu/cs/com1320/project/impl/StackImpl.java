package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T>{
	class ListNode<T>{
		public T data;
		public ListNode next;
		public ListNode(T data){
			this(data, null);
		}
		public ListNode(T data, ListNode next){
			this.data = data;
			this.next = next;
		}
	}
	private ListNode header;
	public StackImpl(){
		this.header = new ListNode(null);
		
	}	
	/**
     * @param element object to add to the Stack
     */
    public void push(T element){
    	ListNode topOfStack = new ListNode(element, this.header.next);
    	this.header.next = topOfStack;
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    public T pop(){
    	ListNode elementToReturn = this.header.next;
    	this.header.next = this.header.next.next;
    	return (T)elementToReturn.data;
    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
    public T peek(){
    	ListNode elementToReturn = this.header.next;
    	return (T)elementToReturn.data;
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    public int size(){
    	ListNode current = this.header;
    	int counter = 0;
    	while(current.next!=null){
    		current = current.next;
    		counter++;
    	}
    	return counter;

    }
}