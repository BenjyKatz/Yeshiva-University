package edu.yu.cs.practice;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
public class RegistrarSystem{
	List<College> listOfColleges;
	public RegistrarSystem(){
		this.listOfColleges = new ArrayList<College>();
	}
	public void addCollege(User u, College col){
		try{this.validate(u, 5);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.listOfColleges.add(col);
	}
	public List<College> getAllColleges(){
		List<College> tempList = new ArrayList<College>();
		tempList.addAll(this.listOfColleges);
		return tempList;
	}
	public List<Department> getAllDepartments(){
		List<College> colleges = this.getAllColleges();
		List<Department> allDepartments = new ArrayList<Department>();
		for(College col: colleges){
			allDepartments.addAll(col.getAllDepartments());
		}
		return allDepartments;
	}
	public List<Course> getAllCourses(){
		List<College> colleges = this.getAllColleges();
		List<Course> allCourses = new ArrayList<Course>();
		for(College col: colleges){
			allCourses.addAll(col.getAllCourses());
		}
		return allCourses;
	}
	private void validate(User u, int accessLevelRequired) throws IllegalAccessException{
		if(u.getAccess()<accessLevelRequired){
			throw new IllegalAccessError();
		}
	}

}