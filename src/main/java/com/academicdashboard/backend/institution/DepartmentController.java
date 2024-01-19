package com.academicdashboard.backend.institution;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/v1.0/auth")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService deptService;
     
    //Create New Department | Returns Institution
    @PostMapping("/institutins/{schoolId}/departments")
    public ResponseEntity<Institution> createDept(
            @PathVariable String schoolId,
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Institution>(
                deptService.createDept(
                    schoolId, 
                    payload.get("deptName")
                ), 
                HttpStatus.CREATED);
    }

    //Modify Existing Department | Returns Modified Department
    @PatchMapping("/departments/{deptId}")
    public ResponseEntity<Department> modifyDepartment(
            @PathVariable String deptId,
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Department>(
                deptService.modifyDepartment(
                    deptId, 
                    payload.get("deptName")
                ), 
                HttpStatus.OK);
    }

    //Delete Existing Department | Void
    @DeleteMapping("/departments/{deptId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String deptId) {
        deptService.deleteDepartment(deptId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    } 
    
    @PostMapping("/departments/{deptId}/majors")
    public ResponseEntity<Department> createMajor(
            @PathVariable String deptId,
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Department>(
                deptService.createMajor(
                    payload.get("schoolId"), 
                    deptId, 
                    payload.get("major")
                ), 
                HttpStatus.OK);
    }

    @DeleteMapping("/departments/{deptId}/majors")
    public ResponseEntity<Department> deleteMajor(
            @PathVariable String deptId,
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Department>(
                deptService.deleteMajor(
                    payload.get("schoolId"), 
                    deptId, 
                    payload.get("major")
                ), 
                HttpStatus.OK);
    }

    @PatchMapping("/departments/{deptId}/minors")
    public ResponseEntity<Department> createMinor(
            @PathVariable String deptId,
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Department>(
                deptService.createMinor(
                    payload.get("schoolId"), 
                    deptId, 
                    payload.get("minor")
                ), 
                HttpStatus.OK);
    }

    @DeleteMapping("/departments/{deptId}/minors")
    public ResponseEntity<Department> deleteMinor(
            @PathVariable String deptId,
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Department>(
                deptService.deleteMinor(
                    payload.get("schoolId"), 
                    deptId, 
                    payload.get("minor")
                ), 
                HttpStatus.OK);
    }
}
