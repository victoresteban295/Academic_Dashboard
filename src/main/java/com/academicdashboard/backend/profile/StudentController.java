package com.academicdashboard.backend.profile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/profile/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("get/{role}/{username}")
    public ResponseEntity<Student> getUserDetails(
            @PathVariable String username, 
            @PathVariable String role) {

        return new ResponseEntity<Student>(
                studentService.getStudentProfile(username, role),
                HttpStatus.OK);
    }

}
