package com.academicdashboard.backend.institution;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/auth/dept")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService deptService;
     
    //Create New Department | Returns Institution
    @PostMapping("/new")
    public ResponseEntity<Institution> createDept(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Institution>(
                deptService.createDept(
                    payload.get("schoolId"), 
                    payload.get("deptName")
                ), 
                HttpStatus.CREATED);
    }

    //Modify Existing Department | Returns Modified Department
    @PutMapping("/modify")
    public ResponseEntity<Department> modifyDepartment(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Department>(
                deptService.modifyDepartment(
                    payload.get("deptId"), 
                    payload.get("newName")
                ), 
                HttpStatus.OK);
    }

    //Delete Existing Department | Void
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDepartment(@RequestBody Map<String, String> payload) {
        deptService.deleteDepartment(payload.get("deptId"));
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    } 
    
    @PutMapping("/major")
    public ResponseEntity<Department> createMajor(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Department>(
                deptService.createMajor(
                    payload.get("schoolId"), 
                    payload.get("deptId"), 
                    payload.get("major")
                ), 
                HttpStatus.OK);
    }

    @PutMapping("/remove/major")
    public ResponseEntity<Department> deleteMajor(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Department>(
                deptService.deleteMajor(
                    payload.get("schoolId"), 
                    payload.get("deptId"), 
                    payload.get("major")
                ), 
                HttpStatus.OK);
    }

    @PutMapping("/minor")
    public ResponseEntity<Department> createMinor(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Department>(
                deptService.createMinor(
                    payload.get("schoolId"), 
                    payload.get("deptId"), 
                    payload.get("minor")
                ), 
                HttpStatus.OK);
    }

    @PutMapping("/remove/minor")
    public ResponseEntity<Department> deleteMinor(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Department>(
                deptService.deleteMinor(
                    payload.get("schoolId"), 
                    payload.get("deptId"), 
                    payload.get("minor")
                ), 
                HttpStatus.OK);
    }
}
