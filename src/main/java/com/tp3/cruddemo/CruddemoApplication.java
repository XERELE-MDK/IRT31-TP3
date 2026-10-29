package com.tp3.cruddemo;

import com.tp3.cruddemo.dao.StudentDAO;
import com.tp3.cruddemo.entity.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CruddemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CruddemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(StudentDAO studentDAO) {
        return args -> {
            String action = (args.length > 0) ? args[0] : "read";
            switch (action) {
                case "create" -> createStudent(studentDAO);
                case "read"
                        -> readStudents(studentDAO);
                case "update" -> updateStudent(studentDAO);
                case "delete" -> deleteStudent(studentDAO);
                default
                        -> System.out.println("Action inconnue: " + action);
            }
        };
    }

    private void deleteStudent(StudentDAO studentDAO) {
        System.out.println("--- Deleting Student ---");
        int studentId = 1;

        // Delete single student
        studentDAO.delete(studentId);
        System.out.println("Deleted student ID " + studentId + ". Verification findById: " + studentDAO.findById(studentId));

        // Delete all students
        int rowsDeleted = studentDAO.deleteAll();
        System.out.println("Deleted row count (deleteAll): " + rowsDeleted);
    }

    private void updateStudent(StudentDAO studentDAO) {
        System.out.println("--- Updating Student ---");
        int studentId = 1;
        Student myStudent = studentDAO.findById(studentId);

        if (myStudent != null) {
            myStudent.setFirstName("John1");
            studentDAO.update(myStudent);
            System.out.println("Updated student: " + studentDAO.findById(studentId));
        } else {
            System.out.println("Student with ID " + studentId + " not found.");
        }
    }

    private void readStudents(StudentDAO studentDAO) {
        System.out.println("--- Reading Students ---");

        // Find single student by ID
        Student myStudent = studentDAO.findById(1);
        System.out.println("Found student with ID 1: " + myStudent);

        // Find all students
        System.out.println("\nAll students:");
        List<Student> allStudents = studentDAO.findAll();
        allStudents.forEach(System.out::println);

        // Find students by last name
        System.out.println("\nStudents with last name 'Doe':");
        List<Student> doeStudents = studentDAO.findByLastName("Doe");
        doeStudents.forEach(System.out::println);
    }

    private void createStudent(StudentDAO studentDAO){
        Student tempStudent = new Student("Paul5","Doe","paul5@test.com");
        studentDAO.save(tempStudent);
        System.out.println("saved student. Generated id:"+ tempStudent.getId());

        Student myStudent = studentDAO.findById(1);
        System.out.println("Found the student: " + myStudent);

        List<Student> theStudents = studentDAO.findAll();
        for (Student s : theStudents) {
            System.out.println(s);
        }

        List<Student> theStudent = studentDAO.findByLastName("Doe");
        theStudents.forEach(System.out::println);

        int studentId = 1;
        Student myStudentSS = studentDAO.findById(studentId);
        myStudentSS.setFirstName("John");
        studentDAO.update(myStudentSS);
        System.out.println("Updated student: " + studentDAO.findById(studentId));

        studentDAO.delete(1);
        System.out.println("After delete, findById(1) = " + studentDAO.findById(1));

        int n = studentDAO.deleteAll();
        System.out.println("Deleted row count: " + n);
    }
}
