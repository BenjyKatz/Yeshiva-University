package edu.yu.registrar.banner;
import edu.yu.registrar.model.*;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import edu.yu.registrar.banner.UserInterface;

public class UserInterface{
	private int userId;
	public UserInterface(int userId){
		this.userId = userId;
	}
	public Set<Course> getCoursesInDepartment(int year, CourseOffering.Semester sem, School sch, Department dep){
		return null;
	}
	public Set<Course> getAllCourses(int year, CourseOffering.Semester sem, School sch){
		return null;
	}
	public boolean register(int year, CourseOffering.Semester sem, Student s, Course c){
		return false;
	}
	public Set<Student> getAllRegisteredStudents(int year, CourseOffering.Semester sem){
		return null;
	}
	public Set<Professor> getAllProfessors(int year, CourseOffering.Semester sem){
		return null;
	}
	public Set<Course> getAllRequiredCourses(School sch, Major m){
		return null;
	}
	public boolean dropClass(int year, CourseOffering.Semester sem, Student s, Course c){
		return false;
	}
	public Student enroll(String firstName, String lastName, School school, Major major, GregorianCalendar graduationDate, int id){
		return null;
	}
	public boolean unenroll(Student s){
		return false;
	}
	public HashMap<Course, Integer> getTranscript(Student s){
		return null;
	}
	public Course createCourse(String name, School school, Department dep, Major major, boolean isRequired, int number){
		return null;
	}
	public Employee hire(String firstName, String lastName, School school, Department department, Employee.Role role, int id){
		return null;
	}
	public Employee.Role fire(Employee e){
		return null;
	}
	public void makeRequired(Course c, Major m){

	}
	public Department addDepartment(String name, School school){
		return null;
	}
	public School addSchool(String name){
		return null;
	}
	public int setGrade(Student student, Course course, int grade){
		return -1;
	}
	public Major addMajor(String name, School school, Department department){
		return null;
	}
	public Set<Student> getStudentsInCourse(Course course){
		return null;
	}
	public Set<Student> getStudentsInMajor(Major major){
		return null;
	}
	public Set<Student> getStudentsInSchool(School school){
		return null;
	}
	public Professor getProffesorInCourse(Course course){
		return null;
	}
	public Professor assignProfessorToCourse(Professor p, Course c){
		return null;
	}
	public Set<Professor> getProfessorsInDepartment(Department department){
		return null;
	}
	public Set<Professor> getProfessorsInSchool(School school){
		return null;
	}
	public void addPrerec(Course baseCourse, Course prerec){

	}




}