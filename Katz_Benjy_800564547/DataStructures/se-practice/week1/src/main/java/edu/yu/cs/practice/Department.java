package edu.yu.cs.practice;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

public class Department{
	List<Course> listOfCourses;
	Set<Professor> staffDirectory;
	String nameOfDepartment;

	public void Department(String name){
		this.nameOfDepartment = name;
		this.listOfCourses  = new ArrayList<Course>();
		this.staffDirectory = new HashSet<Professor>();
	}
	public void addCourse(Course courseToAdd, Professor professor){
		this.listOfCourses.add(courseToAdd);
		this.staffDirectory.add(professor);
	}
	public void removeCourse(Course courseToRemove){
		this.listOfCourses.remove(courseToRemove);

	}
	public List<Course> getCourseCatalog(){
		List<Course> tempList = new ArrayList<Course>();
		tempList.addAll(this.listOfCourses);
		return tempList;

	}
	public Set<Professor> getProfessors(){
		Set<Professor> tempSet = new HashSet<Professor>();
		tempSet.addAll(this.staffDirectory);
		return tempSet;
	}

}