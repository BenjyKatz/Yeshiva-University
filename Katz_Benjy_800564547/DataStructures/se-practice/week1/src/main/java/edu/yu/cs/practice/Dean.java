package edu.yu.cs.practice;

public class Dean implements User{
	String name;
	int idNumber;
	College college;

	public Dean(String name, int idNumber, College college){
		this.name = name;
		this.idNumber = idNumber;
		this.college = college;
	}
	public String getName(){
		return this.name;
	}
	public int getAccess(){
		return 3;
	}
	public College getCollege(){
		return this.college;
	}
}