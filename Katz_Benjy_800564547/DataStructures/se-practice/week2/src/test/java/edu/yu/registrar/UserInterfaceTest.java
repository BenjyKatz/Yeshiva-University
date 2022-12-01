package edu.yu.registrar;
import edu.yu.registrar.banner.UserInterface;
import edu.yu.registrar.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class UserInterfaceTest{

	private School syms;
	private School stern;
	private School yc;
	private Department csDep;
	private Department mathDep;
	private Employee john;
	private Employee chen;
	private Major csMaj;
	private Major mathMaj;
	private Course introToCs;
	private Course calcOne;
	private Student benjy;
	private UserInterface ui;
	//Assume it is currently the registration period for spring 2021
	@BeforeEach
	public void init(){
		 ui = new UserInterface(1105555555);//create new ui with registrar level access
		 syms = ui.addSchool("Syms");
		 stern = ui.addSchool("Stern");
		 yc = ui.addSchool("YC");
		 csDep = ui.addDepartment("CompSci", yc);
		 mathDep = ui.addDepartment("Math", yc);
		 john = ui.hire("John", "Doe", yc, csDep, Employee.Role.PROFESSOR, 900123);
		 chen = ui.hire("Wen", "Chen", yc, mathDep, Employee.Role.PROFESSOR, 900321);
		 csMaj = ui.addMajor("Computer Science", yc, csDep);
		 mathMaj = ui.addMajor("Math", yc, mathDep);
		 introToCs = ui.createCourse("introToCs", yc, csDep, csMaj, true, 12345);
		 calcOne = ui.createCourse("calcOne", yc, csDep, csMaj, true, 54321);
		 ui.makeRequired(calcOne, mathMaj);
		 benjy = ui.enroll("Benjy","Katz", yc, csMaj, new GregorianCalendar(2024, 6,6), 80054321);
	}
	//Test unauthorized actions
	@Test
	public void unauthorizedTest(){
		UserInterface studentUi = new UserInterface(800654321);//simulate a student loging in
		try{
			studentUi.fire(john);
			fail("Student should not be able to fire a professor he does not like");
		}
		catch(UnauthorizedActionException e){}
		try{
			studentUi.setGrade(benjy, introToCs, 100);
			fail("Students can't give themselves grades");
		}
		catch(UnauthorizedActionException e){}		

	}
	//Test simply adding and removing students
	@Test
	public void addingStudent(){
		Student kobi = ui.enroll("Kobi", "Karp", yc, mathMaj, new GregorianCalendar(2023, 6,6), 80055555);
		ui.register(2021,CourseOffering.Semester.SPRING, kobi, calcOne);
		assertEquals(ui.getStudentsInCourse(calcOne).contains(kobi), true);
		ui.dropClass(2021, CourseOffering.Semester.SPRING, kobi, calcOne);
		assertEquals(ui.getStudentsInCourse(calcOne).contains(kobi), false);
		ui.register(2021,CourseOffering.Semester.SPRING, kobi, calcOne);
		assertEquals(ui.getStudentsInCourse(calcOne).contains(kobi), true);
		ui.unenroll(kobi);
		assertEquals(ui.getStudentsInCourse(calcOne).contains(kobi), false);
	}

	//Test registering for classes without the prereqs
	@Test
	public void ignorePrerex(){
		Student eli = ui.enroll("Eli", "Levy", yc, mathMaj, new GregorianCalendar(2023, 6,6), 80012348);
		ui.addPrerec(introToCs, calcOne);
		try{
			ui.register(2021,CourseOffering.Semester.SPRING, eli, calcOne);
			fail("Prerex required");
		}
		catch(UnauthorizedActionException e){}		
	}

	//Test hiring a proffessor and seeing if they are in the catalog and fireing them to see that they are removed
	@Test
	public void hrTest(){
		Professor romi = (Professor)ui.hire("Romi", "Harcszark", yc, mathDep, Employee.Role.PROFESSOR, 900321);
		assertEquals(ui.getProfessorsInSchool(yc).contains(romi), true);
		ui.fire(romi);
		assertEquals(ui.getProfessorsInSchool(yc).contains(romi), false);
		UserInterface romisUi = new UserInterface(900321); // new ui with romis id number
		try{
			romisUi.getAllRequiredCourses(yc, csMaj);
			fail("Fired employees should not have any access");
		}
		catch(UnauthorizedActionException e){}	
	}

	//Test getting a transcript with a class that was dropped
	@Test
	public void transcriptTest(){
		Student nir = ui.enroll("Nir", "Solooki", yc, mathMaj, new GregorianCalendar(2023, 6,6), 80065432);
		ui.register(2021,CourseOffering.Semester.SPRING, nir, calcOne);
		HashMap<Course, Integer> trans = ui.getTranscript(nir);
		assertTrue(trans.keySet().contains(calcOne));
		ui.dropClass(2021, CourseOffering.Semester.SPRING, nir, calcOne);
		assertFalse(trans.keySet().contains(calcOne));
	}	
	//Test enrolling a student
	@Test
	public void enrollTest(){
		Student judah = ui.enroll("Judah", "Levy", yc, mathMaj, new GregorianCalendar(2023, 6,6), 80065332);
		assertTrue(ui.getAllRegisteredStudents(2021, CourseOffering.Semester.SPRING).contains(judah));
		assertTrue(ui.getStudentsInMajor(mathMaj).contains(judah));
		assertTrue(ui.getStudentsInSchool(yc).contains(judah));
		ui.register(2021,CourseOffering.Semester.SPRING, judah, calcOne);
		assertTrue(ui.getStudentsInCourse(calcOne).contains(judah));
		ui.unenroll(judah);
		assertFalse(ui.getAllRegisteredStudents(2021, CourseOffering.Semester.SPRING).contains(judah));
		assertFalse(ui.getStudentsInMajor(mathMaj).contains(judah));
		assertFalse(ui.getStudentsInSchool(yc).contains(judah));
		assertFalse(ui.getStudentsInCourse(calcOne).contains(judah));
	}
	//Test professors are added correctly
	@Test
	public void professorAddingTest(){
		Professor p1 = (Professor)ui.hire("p", "one", yc, mathDep, Employee.Role.PROFESSOR, 900001);
		Professor p2 = (Professor)ui.hire("p", "two", yc, csDep, Employee.Role.PROFESSOR, 900002);
		Professor p3 = (Professor)ui.hire("p", "three", yc, mathDep, Employee.Role.PROFESSOR, 900003);
		ui.assignProfessorToCourse(p1, calcOne);
		assertEquals(ui.getProffesorInCourse(calcOne), p1);
		assertTrue(ui.getProfessorsInDepartment(mathDep).contains(p1));
		assertTrue(ui.getProfessorsInSchool(yc).contains(p2));
		assertFalse(ui.getProfessorsInSchool(stern).contains(p2));
		assertTrue(ui.getAllProfessors(2021, CourseOffering.Semester.SPRING).contains(p3));
		assertTrue(ui.getProfessorsInSchool(yc).contains(p3));
		assertTrue(ui.getProfessorsInDepartment(csDep).contains(p2));

	}

	//Test that the course is everywhere
	@Test
	public void addingCourse(){
		Course dataStructures = ui.createCourse("Data Structures", yc, csDep, csMaj, true, 1320);
		Course algo = ui.createCourse("Algorythms", yc, csDep, csMaj, true, 1400);
		Course mathForCs = ui.createCourse("Math For CS", yc, csDep, csMaj, false, 1360);
		assertTrue(ui.getAllRequiredCourses(yc, csMaj).contains(dataStructures));
		assertTrue(ui.getAllRequiredCourses(yc, csMaj).contains(algo));
		assertFalse(ui.getAllRequiredCourses(yc, csMaj).contains(mathForCs));
		assertTrue(ui.getAllCourses(2021, CourseOffering.Semester.SPRING, yc).contains(dataStructures));
		assertTrue(ui.getAllCourses(2021, CourseOffering.Semester.SPRING, yc).contains(algo));
		assertTrue(ui.getAllCourses(2021, CourseOffering.Semester.SPRING, yc).contains(mathForCs));
		assertTrue(ui.getCoursesInDepartment(2021, CourseOffering.Semester.SPRING, yc, csDep).contains(dataStructures));
		assertTrue(ui.getCoursesInDepartment(2021, CourseOffering.Semester.SPRING, yc, csDep).contains(algo));
		assertTrue(ui.getCoursesInDepartment(2021, CourseOffering.Semester.SPRING, yc, csDep).contains(mathForCs));
	}

	//Test adding too high of a grade
	@Test
	public void assignHighGrade(){
		try{
			ui.setGrade(benjy, introToCs, 105);
			fail("Can't give that high of a grade");
		}
		catch(UnauthorizedActionException e){}
		try{
			ui.setGrade(benjy, introToCs, -10);
			fail("Can't give that low of a grade");
		}
		catch(UnauthorizedActionException e){}	
	}

	//Test that immutables are not mutable
	@Test
	public void messWithImmutables(){
		Student yosef = ui.enroll("Yosef", "Lemel", yc, csMaj, new GregorianCalendar(2022, 6,6), 800151515);
		ui.register(2021,CourseOffering.Semester.SPRING, yosef, calcOne);
		ui.register(2021,CourseOffering.Semester.SPRING, yosef, introToCs);
		UserInterface yosefsUi = new UserInterface(800151515);
		try{
			yosefsUi.getTranscript(benjy);
			fail("Students can't look at each others transcripts");
		}
		catch(UnauthorizedActionException e){}	
		HashMap<Course, Integer> yosefsTrans = yosefsUi.getTranscript(yosef);
		for(Course c: yosefsTrans.keySet()){
			try{
				c.setName("The stupid course");
				fail("Student can't set names");
			}
			catch(UnauthorizedActionException e){}	
		}

		HashMap<Course, Integer> registrarsCopyOfYosefsTrans = ui.getTranscript(yosef);
		for(Course c: yosefsTrans.keySet()){
			try{
				c.setName("New Name");
			}
			catch(UnauthorizedActionException e){
				fail("registrar can change names");
			}	
		}
	}

	
}