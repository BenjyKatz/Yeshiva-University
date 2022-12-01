package edu.yu.cs.practice;

public class Registrarer implements User{
	String name;
	int idNumber;

	public Registrarer(String name, int idNumber, College college){
		this.name = name;
		this.idNumber = idNumber; 
	}
	public String getName(){
		return this.name;
	}
	public int getAccess(){
		return 5;
	}
}