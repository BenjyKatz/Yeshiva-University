package edu.yu.cs.practice;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
public class Course{
	Set<Student> setOfStudents;
	String nameOfCourse;
	int crn;
	int courseNumber;
	Professor instructor;
	Set<Course> prerex;

	public Course(String name, int courseNumber, int crn, int capacity, Professor instructor, Set<Course> prerex){

	}
	public boolean addStudent(Student s, User u){
		try{this.validate(s, u, 2);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		if(s.getTranscript(u).keySet().containsAll(prerex)){
			this.setOfStudents.add(s);
			s.register(this, u);
			return true;
		}
		return false;

	}
	public void removeStudent(Student s, User u){
		try{this.validate(s, u, 2);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.setOfStudents.remove(s);
	}
	public void addPrerec(Course c, User u){
		try{this.validate(u, 3);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.prerex.add(c);

	}
	public void removePrerec(Course c, User u){
		try{this.validate(u, 3);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.prerex.remove(c);
	}
	private void validate(Student s, User u, int accessLevelRequired) throws IllegalAccessException{
		if((u instanceof Student && !s.equals((Student)u)) && u.getAccess()<accessLevelRequired){
			throw new IllegalAccessException();
		}
	}
	private void validate(User u, int accessLevelRequired) throws IllegalAccessException{
		if(u.getAccess()<accessLevelRequired){
			throw new IllegalAccessError();
		}
	}

}