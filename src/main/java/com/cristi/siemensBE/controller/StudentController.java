package com.cristi.siemensBE.controller;

import com.cristi.siemensBE.model.Student;
import com.cristi.siemensBE.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cristi.siemensBE.repository.StudentRepository;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentRepository studentRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    @PostMapping("/add")
    public String add(@RequestBody Student student) {
        studentService.saveStudent(student);
        return "Studentul nou a fost adaugat";
    }

    @GetMapping("/getAll")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Student student) {
        Student authenticatedStudent = studentService.authenticateStudent(student.getEmail(), student.getPassword());
        if (authenticatedStudent != null) {
            return ResponseEntity.ok("Autentificare reușită!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autentificare eșuată. Verifică emailul și parola.");
        }
    }

//    @GetMapping("/get")
//    public Student getStudent(@RequestParam String email) {
//        return studentRepository.findByEmail(email);
//    }

    @GetMapping("/get")
    public ResponseEntity<Student> getStudent(@RequestParam String email) {
        Student student = studentRepository.findByEmail(email);
        if (student != null) {
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/incrementAttendance/{email}")
    public ResponseEntity<Student> incrementAttendance(@PathVariable String email) {
        Student student = studentRepository.findByEmail(email);
        System.out.println("Found student: " + student); // Log the found student
        System.out.println("Email: " + email); // Log the email

        if (student != null) {
            student.setPrezente(student.getPrezente() + 1);
            System.out.println("incrementAttendance called for " + email);
            studentRepository.save(student);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/resetPrezente")
    public ResponseEntity<String> resetPrezente() {
        List<Student> students = studentRepository.findAll();
        for (Student student : students) {
            student.setPrezente(0);
            studentRepository.save(student);
        }
        return new ResponseEntity<>("Numărul de prezențe a fost resetat pentru toți studenții.", HttpStatus.OK);
    }
}