package edu.yu.cs.practice;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
public class Professor implements User{
	String name;
	int idNumber;
	Set<Course> coursesTaught;
	public Professor(String name, int idNumber, Set<Course> coursesTaught){
		this.name = name;
		this.idNumber = idNumber;
		this.coursesTaught = new HashSet<Course>();
	}
	public String getName(){
		return this.name;
	}
	public int getAccess(){
		return 2;
	}
	public void addCourse(Course c, User u){
		try{this.validate(u, 3);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.coursesTaught.add(c);
	}
	public Set getCoursesTaught(User u){
		try{this.validate(u, 2);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		Set<Course> tempSet = new HashSet<Course>();
		tempSet.addAll(this.coursesTaught);
		return tempSet;
	}
	private void validate(User u, int accessLevelRequired) throws IllegalAccessException{
		if(u.getAccess()<accessLevelRequired){
			throw new IllegalAccessError();
		}
	}
}