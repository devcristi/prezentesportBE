package com.cristi.siemensBE.service;

import com.cristi.siemensBE.model.Student;

import java.util.List;

public interface StudentService {
    public Student saveStudent  (Student student);
    public List<Student> getAllStudents();
    Student authenticateStudent(String email, String password);

}
