package edu.yu.cs.practice;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
public class Student implements User{
	String name; 
	int idNumber;
	int yearOnCampus;
	Set<Course> schedule;
	HashMap<Course, Integer> transcript;

	public Student(String name, int idNumber, int yearOnCampus){
		this.name = name;
		this.idNumber = idNumber;
		this.yearOnCampus = yearOnCampus;
		this.schedule  = new HashSet<Course>();
		this.transcript = new HashMap<Course, Integer>();
	}
	public String getName(){
		return this.name;
	}
	public boolean register(Course c, User u){
		try{
		this.validate(this, u, 3);
		}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.addToSchedule(c);
		return c.addStudent(this, this);
	}
	public void dropCourse(Course c, User u){
		try{this.validate(this, u, 3);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.schedule.remove(c);
		 c.removeStudent(this, this);
	}
	protected void addToSchedule(Course c){
		this.schedule.add(c);
	}
	public Set<Course> getSchedule(User u){
		try{this.validate(this, u, 3);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		Set<Course> copyOfSchedule = new HashSet<Course>();
		copyOfSchedule.addAll(this.schedule);
		return copyOfSchedule;
	}
	public HashMap<Course, Integer> getTranscript(User u){
		try{this.validate(this, u, 3);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		HashMap<Course, Integer> copyOfTranscript = new HashMap<Course, Integer>();
		copyOfTranscript.putAll(this.transcript);
		return copyOfTranscript;

	}
	public int getAccess(){
		return 1;
	}

	private void validate(Student s, User u, int accessLevelRequired) throws IllegalAccessException{ 
		if((u instanceof Student && !s.equals((Student)u)) && u.getAccess()<accessLevelRequired){
			throw new IllegalAccessException();
		}
	}
	


}