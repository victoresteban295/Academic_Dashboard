package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.academicdashboard.backend.user.User;
import com.academicdashboard.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestData {

    private final UserRepository userRepository;
    private final GrouplistRepository grouplistRepository;
    private final ChecklistRepository checklistRepository;

    public void cleanupDatabase() {
        checklistRepository.deleteAll();
        grouplistRepository.deleteAll();
        userRepository.deleteAll();
    }

    public void populateDatabase() {

        /****************************************/
        /* ********** Checkpoint A11 ********** */
        /****************************************/
        Checkpoint subpointA11A = Checkpoint.builder()
                .index("0")
                .content("ContentA11A")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();
        Checkpoint subpointA11B = Checkpoint.builder()
                .index("1")
                .content("ContentA11B")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> A11Subpoints = new ArrayList<>();
        A11Subpoints.add(subpointA11A);
        A11Subpoints.add(subpointA11B);

        Checkpoint subpointA11C = Checkpoint.builder()
                .index("0")
                .content("ContentA11C")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();
        Checkpoint subpointA11D = Checkpoint.builder()
                .index("1")
                .content("ContentA11D")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> A11CompletedSubpoints = new ArrayList<>();
        A11CompletedSubpoints.add(subpointA11C);
        A11CompletedSubpoints.add(subpointA11D);

        Checkpoint pointA11 = Checkpoint.builder()
                .index("0")
                .content("ContentA11")
                .subpoints(A11Subpoints)
                .completedSubpoints(A11CompletedSubpoints)
                .build();


        /****************************************/
        /* ********** Checkpoint A12 ********** */
        /****************************************/
        Checkpoint pointA12 = Checkpoint.builder()
                .index("1")
                .content("ContentA12")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();


        /****************************************/
        /* ********** Checkpoint A13 ********** */
        /****************************************/
        Checkpoint pointA13 = Checkpoint.builder()
                .index("0")
                .content("ContentA13")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();


        /****************************************/
        /* ********** Checkpoint A14 ********** */
        /****************************************/
        Checkpoint subpointA14A = Checkpoint.builder()
                .index("0")
                .content("ContentA14A")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint subpointA14B = Checkpoint.builder()
                .index("1")
                .content("ContentA14B")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> A14CompletedSubpoints = new ArrayList<>();
        A14CompletedSubpoints.add(subpointA14A);
        A14CompletedSubpoints.add(subpointA14B);

        Checkpoint pointA14 = Checkpoint.builder()
                .index("1")
                .content("ContentA14")
                .subpoints(new ArrayList<>())
                .completedSubpoints(A14CompletedSubpoints)
                .build();


        /**************************************/
        /* ********** Checklist A1 ********** */
        /**************************************/
        List<Checkpoint> A1Checkpoints = new ArrayList<>();
        A1Checkpoints.add(pointA11);
        A1Checkpoints.add(pointA12);

        List<Checkpoint> A1CompletedPoints = new ArrayList<>();
        A1CompletedPoints.add(pointA13);
        A1CompletedPoints.add(pointA14);

        Checklist checklistA1 = checklistRepository.insert(
                Checklist.builder()
                    .listId("listIdA1")
                    .title("Checklist Title A1")
                    .groupId("groupIdA")
                    .checkpoints(A1Checkpoints)
                    .completedPoints(A1CompletedPoints)
                    .build());


        /**************************************************/
        /* ********** Checkpoint A21, A22, A23 ********** */
        /**************************************************/
        Checkpoint pointA21 = Checkpoint.builder()
                .index("0")
                .content("ContentA21")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint pointA22 = Checkpoint.builder()
                .index("1")
                .content("ContentA22")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint pointA23 = Checkpoint.builder()
                .index("2")
                .content("ContentA23")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();


        /**************************************************/
        /* ********** Checkpoint A24, A25, A26 ********** */
        /**************************************************/
        Checkpoint pointA24 = Checkpoint.builder()
                .index("0")
                .content("ContentA24")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint pointA25 = Checkpoint.builder()
                .index("1")
                .content("ContentA25")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint pointA26 = Checkpoint.builder()
                .index("2")
                .content("ContentA26")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();


        /**************************************/
        /* ********** Checklist A2 ********** */
        /**************************************/
        List<Checkpoint> A2Checkpoints = new ArrayList<>();
        A2Checkpoints.add(pointA21);
        A2Checkpoints.add(pointA22);
        A2Checkpoints.add(pointA23);

        List<Checkpoint> A2CompletedPoints = new ArrayList<>();
        A2CompletedPoints.add(pointA24);
        A2CompletedPoints.add(pointA25);
        A2CompletedPoints.add(pointA26);

        Checklist checklistA2 = checklistRepository.insert(
                Checklist.builder()
                    .listId("listIdA2")
                    .title("Checklist Title A2")
                    .groupId("groupIdA")
                    .checkpoints(A2Checkpoints)
                    .completedPoints(A2CompletedPoints)
                    .build());


        /*************************************/
        /* ********** Grouplist A ********** */
        /*************************************/
        List<Checklist> AChecklist = new ArrayList<>();
        AChecklist.add(checklistA1);
        AChecklist.add(checklistA2);

        Grouplist grouplistA = grouplistRepository.insert(
                Grouplist.builder()
                    .groupId("groupIdA")
                    .title("Grouplist Title A")
                    .checklists(AChecklist)
                    .build());


        /****************************************/
        /* ********** Checkpoint B11 ********** */
        /****************************************/
        Checkpoint subpointB11A = Checkpoint.builder()
                .index("0")
                .content("ContentB11A")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint subpointB11B = Checkpoint.builder()
                .index("1")
                .content("ContentB11B")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> B11Subpoints = new ArrayList<>();
        B11Subpoints.add(subpointB11A);
        B11Subpoints.add(subpointB11B);

        Checkpoint subpointB11C = Checkpoint.builder()
                .index("0")
                .content("ContentB11C")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> B11CompletedSubpoints = new ArrayList<>();
        B11CompletedSubpoints.add(subpointB11C);

        Checkpoint pointB11 = Checkpoint.builder()
                .index("0")
                .content("ContentB11")
                .subpoints(B11Subpoints)
                .completedSubpoints(B11CompletedSubpoints)
                .build();


        /****************************************/
        /* ********** Checkpoint B12 ********** */
        /****************************************/
        Checkpoint subpointB12A = Checkpoint.builder()
                .index("0")
                .content("ContentB12A")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint subpointB12B = Checkpoint.builder()
                .index("1")
                .content("ContentB12B")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint subpointB12C = Checkpoint.builder()
                .index("2")
                .content("ContentB12C")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> B12Subpoints = new ArrayList<>();
        B12Subpoints.add(subpointB12A);
        B12Subpoints.add(subpointB12B);
        B12Subpoints.add(subpointB12C);

        Checkpoint pointB12 = Checkpoint.builder()
                .index("1")
                .content("ContentB12")
                .subpoints(B12Subpoints)
                .completedSubpoints(new ArrayList<>())
                .build();


        /**************************************/
        /* ********** Checklist B1 ********** */
        /**************************************/
        List<Checkpoint> B1Checkpoints = new ArrayList<>();
        B1Checkpoints.add(pointB11);
        B1Checkpoints.add(pointB12);

        Checklist checklistB1 = checklistRepository.insert(
                Checklist.builder()
                    .listId("listIdB1")
                    .title("Checklist Title B1")
                    .groupId("groupIdB")
                    .checkpoints(B1Checkpoints)
                    .completedPoints(new ArrayList<>())
                    .build());


        /**************************************/
        /* ********** Checklist B2 ********** */
        /**************************************/
        Checklist checklistB2 = checklistRepository.insert(
                Checklist.builder()
                    .listId("listIdB2")
                    .title("Checklist Title B2")
                    .groupId("groupIdB")
                    .checkpoints(new ArrayList<>())
                    .completedPoints(new ArrayList<>())
                    .build());
        

        /*************************************/
        /* ********** Grouplist B ********** */
        /*************************************/
        List<Checklist> BChecklist = new ArrayList<>();
        BChecklist.add(checklistB1);
        BChecklist.add(checklistB2);

        Grouplist grouplistB = grouplistRepository.insert(
                Grouplist.builder()
                    .groupId("groupIdB")
                    .title("Grouplist Title B")
                    .checklists(BChecklist)
                    .build());


        /*******************************************************/
        /* ********** Checkpoint C11, C12, C13, C14 ********** */
        /*******************************************************/
        Checkpoint pointC11 = Checkpoint.builder()
                .index("0")
                .content("ContentC11")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint pointC12 = Checkpoint.builder()
                .index("1")
                .content("ContentC12")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint pointC13 = Checkpoint.builder()
                .index("2")
                .content("ContentC13")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint pointC14 = Checkpoint.builder()
                .index("3")
                .content("ContentC14")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();


        /**************************************/
        /* ********** Checklist C1 ********** */
        /**************************************/
        List<Checkpoint> C1Checkpoints = new ArrayList<>();
        C1Checkpoints.add(pointC11);
        C1Checkpoints.add(pointC12);
        C1Checkpoints.add(pointC13);
        C1Checkpoints.add(pointC14);

        Checklist checklistC1 = checklistRepository.insert(
                Checklist.builder()
                    .listId("listIdC1")
                    .title("Checklist Title C1")
                    .groupId("")
                    .checkpoints(C1Checkpoints)
                    .completedPoints(new ArrayList<>())
                    .build());


        /****************************************/
        /* ********** Checkpoint D11 ********** */
        /****************************************/
        Checkpoint pointD11 = Checkpoint.builder()
                .index("0")
                .content("ContentD11")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();


        /****************************************/
        /* ********** Checkpoint D12 ********** */
        /****************************************/
        Checkpoint subpointD12A = Checkpoint.builder()
                .index("0")
                .content("ContentD12A")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint subpointD12B = Checkpoint.builder()
                .index("1")
                .content("ContentD12B")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> D12CompletedSubpoints = new ArrayList<>();
        D12CompletedSubpoints.add(subpointD12A);
        D12CompletedSubpoints.add(subpointD12B);

        Checkpoint pointD12 = Checkpoint.builder()
                .index("1")
                .content("ContentD12")
                .subpoints(new ArrayList<>())
                .completedSubpoints(D12CompletedSubpoints)
                .build();
        

        /**************************************/
        /* ********** Checklist D1 ********** */
        /**************************************/
        List<Checkpoint> D1CompletedPoints = new ArrayList<>();
        D1CompletedPoints.add(pointD11);
        D1CompletedPoints.add(pointD12);

        Checklist checklistD1 = checklistRepository.insert(
                Checklist.builder()
                    .listId("listIdD1")
                    .title("Checklist Title D1")
                    .groupId("")
                    .checkpoints(new ArrayList<>())
                    .completedPoints(D1CompletedPoints)
                    .build());

        /**************************************/
        /* ********** User Account ********** */
        /**************************************/
        List<Grouplist> grouplists = new ArrayList<>();
        grouplists.add(grouplistA);
        grouplists.add(grouplistB);

        List<Checklist> checklists = new ArrayList<>();
        checklists.add(checklistC1);
        checklists.add(checklistD1);

        userRepository.insert(
                User.builder()
                .userId("ju7db63uy678erdybncpo")
                .firstname("Test")
                .lastname("User")
                .email("testuser@email.com")
                .username("testuser")
                .checklists(checklists)
                .grouplists(grouplists)
                .build());


        List<Checklist> checklists01 = new ArrayList<>();
        for(int i = 1; i <= 20; i++) {
            String listId = "listId" + Integer.toString(i);
            String title = "Checklist Title " + Integer.toString(i);

            Checklist checklist01 = checklistRepository.insert(
                    Checklist.builder()
                        .listId(listId)
                        .title(title)
                        .groupId("")
                        .checkpoints(new ArrayList<>())
                        .completedPoints(new ArrayList<>())
                        .build());

            checklists01.add(checklist01);
        }

        userRepository.insert(
                User.builder()
                .userId("bw79b63ub67vendybnqpa")
                .firstname("Test")
                .lastname("User01")
                .email("testuser01@email.com")
                .username("testuser01")
                .checklists(checklists01)
                .grouplists(new ArrayList<>())
                .build());

    } 
}
