package com.academicdashboard.backend.institution;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/auth/institution")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    //Creates New Academic Institution | Returns Institution Created
    @PostMapping("/new")
    public ResponseEntity<Institution> createInstitution(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Institution>(
                institutionService.createInstitution(
                    payload.get("schoolName")
                ),
                HttpStatus.CREATED); 
    }


    //Modify Existing Institution | Returns Modified Institution
    @PutMapping("/modify")
    public ResponseEntity<Institution> modifyInstitution(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Institution>(
                institutionService.modifyInstitution(
                    payload.get("schoolId"),
                    payload.get("newName")
                ),
                HttpStatus.OK);
    }

    //Delete Existing Institution | Void
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteInstitution(@RequestBody Map<String, String> payload) {
        institutionService.deleteInstitution(payload.get("schoolId"));
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    } 

    @GetMapping("/verify")
    public ResponseEntity<VerificationResponse> verifyInstitution(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<VerificationResponse>(
                institutionService.verifyInstitution(
                    payload.get("profile"), 
                    payload.get("codeId")
                ),
                HttpStatus.OK);
    }

}
