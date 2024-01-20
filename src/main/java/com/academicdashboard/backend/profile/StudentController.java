package com.academicdashboard.backend.profile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/v1.0/profiles/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{username}")
    public ResponseEntity<Student> getStudentProfile(
            @PathVariable String username) {

        return new ResponseEntity<Student>(
                studentService.getStudentProfile(username, "STUDENT"),
                HttpStatus.OK);
    }

}
