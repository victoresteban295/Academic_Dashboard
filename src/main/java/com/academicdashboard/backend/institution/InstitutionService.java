package com.academicdashboard.backend.institution;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.academicdashboard.backend.exception.ApiRequestException;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    //Create New Public Id (JNanoId)
    private static String publicId(int size) {
        Random random = new Random();
        char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','p','q','r','s','t','u','v','w','x','y','z','1','2','3','5','6','7','8','9'};
        return NanoIdUtils.randomNanoId(random, alphabet, size); //Create New Public Id
    }


    /*********** CRUD METHODS ***********/

    //Creates New Academic Institution | Returns Institution Created
    public Institution createInstitution(String schoolName) {
        String schoolId = publicId(20);
        String profIdCode = publicId(10);
        String studIdCode = publicId(10);
        Institution school = institutionRepository.insert(
                Institution.builder()
                .schoolId(schoolId)
                .schoolName(schoolName)
                .profIdCode(profIdCode)
                .studIdCode(studIdCode)
                .departments(new ArrayList<>())
                .deptNames(new ArrayList<>())
                .build());
        
        return school;
    }

    //Modify Existing Institution | Returns Modified Institution
    public Institution modifyInstitution(String schoolId, String newName) {
        Institution school = institutionRepository
            .findInstitutionBySchoolId(schoolId) 
            .orElseThrow(() -> new ApiRequestException("Invalid School Id"));
        school.setSchoolName(newName);
        return institutionRepository.save(school);
    }

    //Delete Existing Institution | Void
    public void deleteInstitution(String schoolId) {
        Institution school = institutionRepository
            .findInstitutionBySchoolId(schoolId)
            .orElseThrow(() -> new ApiRequestException("Invalid School Id"));

        institutionRepository.delete(school);
    }

    //Verify Academic Institution | Return Academic Insitution
    public VerificationResponse verifyInstitution(String profile, String codeId) {
        Institution school;
        if(profile == "STUDENT") {
            school = institutionRepository
                .findInstitutionByStudIdCode(codeId)
                .orElseThrow(() -> new ApiRequestException("Invalid Identification Code"));

        } else {
            school = institutionRepository
                .findInstitutionByProfIdCode(codeId)
                .orElseThrow(() -> new ApiRequestException("Invalid Identification Code"));
        }

        VerificationResponse res = VerificationResponse.builder()
            .schoolName(school.getSchoolName())
            .depts(school.getDeptNames())
            .majors(school.getMajors())
            .minors(school.getMinors())
            .build();

        return res;
    }


}
