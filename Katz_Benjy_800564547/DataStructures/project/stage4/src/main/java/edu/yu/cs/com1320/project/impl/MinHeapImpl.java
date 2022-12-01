package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.MinHeap;
public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E>{
	public MinHeapImpl(){
		super.elements = (E[])new Comparable[10];
		super.count = 0;

	}
	@Override
	public void reHeapify(E element){
		super.upHeap(this.getArrayIndex(element));
		super.downHeap(this.getArrayIndex(element));
	}
	@Override
	protected int getArrayIndex(E element){

	//	System.out.println("0th in array: "+ super.elements[0]);
		System.out.println("super.count: "+ super.count);
		for(int i = 1; i<super.count+1; i++){
			if(super.elements[i] != null && super.elements[i].equals(element)){
				return i;
			}

		}
		System.out.println("Could not find index");
		return 0;
	}
	@Override
	protected void doubleArraySize(){
		E[] doubledArray = (E[])new Comparable[(super.elements.length)*2];
		for(int i =0; i< super.elements.length; i++){
			doubledArray[i] = super.elements[i];
		}
		super.elements = doubledArray;

	}
}