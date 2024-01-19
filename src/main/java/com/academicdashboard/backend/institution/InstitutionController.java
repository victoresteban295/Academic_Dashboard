package com.academicdashboard.backend.institution;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/v1.0/auth")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    //Creates New Academic Institution | Returns Institution Created
    @PostMapping("/institutions")
    public ResponseEntity<Institution> createInstitution(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Institution>(
                institutionService.createInstitution(
                    payload.get("schoolName")
                ),
                HttpStatus.CREATED); 
    }


    //Modify Existing Institution | Returns Modified Institution
    @PatchMapping("/institutions/{schoolId}")
    public ResponseEntity<Institution> modifyInstitution(
            @PathVariable String schoolId, 
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Institution>(
                institutionService.modifyInstitution(
                    schoolId,
                    payload.get("schoolName")
                ),
                HttpStatus.OK);
    }

    //Delete Existing Institution | Void
    @DeleteMapping("/institutions/{schoolId}")
    public ResponseEntity<Void> deleteInstitution(@PathVariable String schoolId) {
        institutionService.deleteInstitution(schoolId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    } 

    //Get Institution Informaiton Based on Profile || Returns VerificatonResponse
    @GetMapping("/institutions/profiles/{profile}/{codeId}")
    public ResponseEntity<VerificationResponse> verifyInstitution(
            @PathVariable String profile, 
            @PathVariable String codeId) {

        return new ResponseEntity<VerificationResponse>(
                institutionService.verifyInstitution(
                    profile, 
                    codeId
                ),
                HttpStatus.OK);
    }
}
