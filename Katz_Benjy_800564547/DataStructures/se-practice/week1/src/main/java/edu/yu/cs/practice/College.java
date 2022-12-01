package edu.yu.cs.practice;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
public class College{
	List<Department> listOfDepartments = new ArrayList<Department>();
	String nameOfCollege;
	public College(String name){
		this.nameOfCollege = name;
	}
	public void addDepartment(User u, Department d){
		try{this.validate(u, 3);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.listOfDepartments.add(d);
	}
	public void removeDepartment(User u, Department d){
		try{this.validate(u, 3);}
		catch(IllegalAccessException e){
			//deal with this later
		}
		this.listOfDepartments.remove(d);
	}
	public List<Department> getAllDepartments(){
		List<Department> tempList = new ArrayList<Department>();
		tempList.addAll(this.listOfDepartments);
		return tempList;
	}
	public List<Course> getAllCourses(){
		List<Department> departments = this.getAllDepartments();
		List<Course> allCourses = new ArrayList<Course>();
		for(Department d: departments){
			allCourses.addAll(d.getCourseCatalog());
		}
		return allCourses;
	}
	private void validate(User u, int accessLevelRequired) throws IllegalAccessException{
		if(u.getAccess()<accessLevelRequired){
			throw new IllegalAccessError();
		}
	}

}