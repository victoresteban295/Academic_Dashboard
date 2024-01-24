# REST API: Academic Dashboard

## Table of Content
- [About REST API](#about-rest-api)
- [Development Stack](#development-stack)
- [Project Structure](#project-structure)
- [REST API Endpoints](#rest-api-endpoints)
    - [Authentication](#authentication)
    - [Profile](#profile)
    - [Checklist](#checklist)
    - [Grouplist](#grouplist)
    - [Institution](#institution)
    - [Department](#department)
***

## About REST API
A RESTful API written in Java that uses the Spring framework to allow Academic Dashboard to access and manipulate data stored in a MongoDB database.

## Development Stack
- [Spring Boot](https://spring.io/projects/spring-boot/) - Java-based Framework Used to Create a Micro Service
- [Spring Data](https://spring.io/projects/spring-data/) - Spring-based Programming Model for Data Access
- [Spring Security](https://spring.io/projects/spring-security/) - Java-based Framework that Provides Authentication, Authorization, and Access-Control
- [JUnit 5](https://junit.org/junit5/) - Unit Testing Framework for the Java Programming Language
- [MongoDB](https://www.mongodb.com/) - NoSQL Document-Oriented Database
- [Docker](https://www.docker.com/) - Software Platform that Allows You to Build, Test, Share, and Run Container Applications

## Project Structure
```
src
├── main
│   ├── java/com/academicdashboard/backend
│   │   ├── auth*
│   │   ├── calendar*
│   │   ├── checklist*
│   │   ├── config
│   │   ├── course*
│   │   ├── exception
│   │   ├── institution*
│   │   ├── profile*
│   │   ├── reminder*
│   │   ├── token*
│   │   ├── user*
│   │   └── BackendApplication.java
│   └── resources
│       ├── .env.example
│       └── application.properties
├── test/java/com/academicdashboard/backend
│   ├── checklist**
│   ├── user**
│   └── config 
│       ├── MongoContainerConfiguration.java
│       └── TestData.java
└── pom.xml
```

- `config` directory - Spring Security Configuration Files
- `exception` directory - Custom Exception for Request Errors
- `exception` directory - API Request Exception Files for Handling Errors
- `dataEntity*` directory - Service, Controller, Repository Classes for that Data Entity
- `resources/.env.example` file - Environment Variables
- `resources/application.properties` file - Spring Boot Application's Configuration
- `test/.../dataEntity**` directory - ServiceTest, ControllerTest, RepositoryTest Classes for that Data Entity
- `test/.../config/MongoContainerConfiguration.java` file - Test Container Configuration File
- `test/.../config/TestData.java` file - Mocked Data Used for Integration Testing
- `pom.xml` file - Maven-related File Used for Dependencies and Configurations

***

# REST API Endpoints

## Authentication
- [Register a New User](#register-a-new-user)
- [Authenticate User](#authenticate-user)
- [Validate Access Token](#validate-access-token)
- [Logout User](#logout-user)
- [Username Availability](#username-availability)
- [Email Availability](#email-availability)
- [Phone Availability](#phone-availability)

## Register A New User
**Method:** `POST` 

**Path:** `/v1.0/auth/register`
#### Request
```
Content-Type: application/json

{
    "profileType": "profileType",
    "schoolName": "schoolName",
    "schooId": "schoolId",
    "firstName": "firstName",
    "middleName": "middleName",
    "lastName": "lastName",
    "birthMonth": "birthMonth",
    "birthDay": "birthDay",
    "birthYear": "birthYear",
    "email": "email",
    "phone": "phone",
    "username": "username",
    "password": "password",

    /* Professor Account */
    "academicRole": "academicRole",
    "apptYear": "apptYear",
    "department": "department",
    "officeBuilding": "officeBuilding",
    "officeRoom": "officeRoom",

    /* Student Account */
    "gradeLvl": "gradeLvl",
    "major": "major",
    "minor": "minor",
    "concentration": "concentration",
}
```
#### Response
```
Status: 201 CREATED
```


## Authenticate User
**Method:** `POST` 

**Path:** `/v1.0/auth/authenticate`
#### Request
```
Content-Type: application/json

{
    "username": "username",
    "password": "password"
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json
Set-Cookie: <username>=<username>; HttpOnly
Set-Cookie: <role>=<role>; HttpOnly
Set-Cookie: <accessToken>=<accessToken>; HttpOnly

{
    "username": "username",
    "role": "role",
    "accessToken": "accessToken"
}
```


## Validate Access Token
**Method:** `POST` 

**Path:** `/v1.0/auth/validate/access-token`
#### Request
```
Content-Type: application/json
Cookie: username=username; role=role; accessToken=accessToken;
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "username": "username",
    "authRole": "authRole",
}
```


## Logout User
**Method:** `POST` 

**Path:** `/v1.0/auth/logout`
#### Request
```
Content-Type: application/json
Authorizaton: Bearer *access-token*
```
#### Response
```
Status: 200 OK
```

## Username Availability
**Method:** `GET` 

**Path:** `/v1.0/auth/username/{username}`
#### Response
```
Status: 204 NO CONTENT
```

## Email Availability
**Method:** `GET` 

**Path:** `/v1.0/auth/email/{email}`
#### Response
```
Status: 204 NO CONTENT
```


## Phone Availability
**Method:** `GET` 

**Path:** `/v1.0/auth/phone/{phone}`
#### Response
```
Status: 204 NO CONTENT
```
***

## Profile
- [Get Professor's Profile Information](#get-professors-profile-information)
- [Get Student's Profile Information](#get-students-profile-information)

## Get Professor's Profile Information
**Method:** `GET` 

**Path:** `/v1.0/profiles/professor/{username}`
#### Request
```
Authorization: Bearer **access-token**
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": objectId,
    "username": "username",
    "firstname": "firstname",
    "middlename": "middlename",
    "lastname": "lastname",
    "birthMonth": "birthMonth",
    "birthDay": "birthDay",
    "birthYear": "birthYear",
    "department": "department",
    "academicRole": "academicRole",
    "apptYear": "apptYear",
    "officeBuilding": "officeBuilding",
    "officeRoom": "officeRoom",
    "officeHrs": "officeHrs"
}
```


## Get Student's Profile Information
**Method:** `GET` 

**Path:** `/v1.0/profiles/student/{username}`
#### Request
```
Authorization: Bearer **access-token**
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": objectId,
    "username": "username",
    "firstname": "firstname",
    "middlename": "middlename",
    "lastname": "lastname",
    "birthMonth": "birthMonth",
    "birthDay": "birthDay",
    "birthYear": "birthYear",
    "gradeLvl": "gradeLvl",
    "major": "major",
    "minor": "minor",
    "concentration": "concentration"
}
```
***


## Checklist
- [Get User's Checklists](#get-users-checklists)
- [Reorder User's Checklists](#reorder-users-checklists)
- [Create a New Checklist](#create-a-new-checklist)
- [Edit Checklist's Title](#edit-checklists-title)
- [Edit Checklist's Checkpoints](#edit-checklists-checkpoints)
- [Edit Checklist's Grouplist](#edit-checklists-grouplist)
- [Delete Checklist](#delete-checklist)

## Get User's Checklists
**Method:** `GET` 

**Path:** `/v1.0/users/{username}/checklists`
#### Request
```
Authorization: Bearer **access-token**
```
#### Response
```
Status: 200 OK
Content-Type: application/json

[
    ...checklists,
]
```

## Reorder User's Checklists
**Method:** `PATCH` 

**Path:** `/v1.0/users/{username}/checklists`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "checklists": "[...checklists]",
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

[
    ...checklists,
]
```

## Create a New Checklist
**Method:** `POST` 

**Path:** `/v1.0/users/{username}/checklists`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "title": "title",
    "listId": "listId",
}
```
#### Response
```
Status: 201 CREATED
Content-Type: application/json

{
    "objectId": "objectId",
    "username": "username",
    "listId": "listId",
    "title": "title",
    "groupId": "",
    "checkpoints": [...checkpoints],
    "completedPoints": [...checkpoints]
}
```

## Edit Checklist's Title
**Method:** `PATCH` 

**Path:** `/v1.0/checklists/{listId}`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "title": "title",
}
```
#### Response
```
Status: 200 OK 
Content-Type: application/json

{
    "objectId": "objectId",
    "username": "username",
    "listId": "listId",
    "title": "title",
    "groupId": "",
    "checkpoints": [...checkpoints],
    "completedPoints": [...checkpoints]
}
```


## Edit Checklist's Checkpoints
**Method:** `PATCH` 

**Path:** `/v1.0/checklists/{listId}/checkpoints`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "checkpoints": [...checkpoints],
    "completedPoints": [...checkpoints]
}
```
#### Response
```
Status: 200 OK 
Content-Type: application/json

{
    "objectId": "objectId",
    "username": "username",
    "listId": "listId",
    "title": "title",
    "groupId": "",
    "checkpoints": [...checkpoints],
    "completedPoints": [...checkpoints]
}
```

## Edit Checklist's Grouplist
**Method:** `PATCH` 

**Path:** `/v1.0/checklists/{listId}/grouplists`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "groupId": "groupId",
}
```
#### Response
```
Status: 200 OK 
Content-Type: application/json

{
    "objectId": "objectId",
    "username": "username",
    "listId": "listId",
    "title": "title",
    "groupId": "",
    "checkpoints": [...checkpoints],
    "completedPoints": [...checkpoints]
}
```


## Delete Checklist
**Method:** `DELETE` 

**Path:** `/v1.0/checklists/{listId}`
#### Request
```
Authorization: Bearer **access-token**
```
#### Response
```
Status: 204 NO CONTENT 
```
***

## Grouplist
- [Get User's Grouplists](#get-users-grouplists)
- [Reorder User's Grouplists](#reorder-users-grouplists)
- [Create a New Grouplist](#create-a-new-grouplist)
- [Edit Grouplist's Tilte](#edit-grouplists-title)
- [Edit Grouplist's Checklists](#edit-grouplists-checklists)
- [Create New Checklist Under a Grouplist](#create-new-checklist-under-a-grouplist)
- [Delete Grouplist](#delete-grouplist)

## Get User's Grouplists
**Method:** `GET` 

**Path:** `/v1.0/users/{username}/grouplists`
#### Request
```
Authorization: Bearer **access-token**
```
#### Response
```
Status: 200 OK
Content-Type: application/json

[
    ...grouplists,
]
```


## Reorder User's Grouplists
**Method:** `PATCH` 

**Path:** `/v1.0/users/{username}/grouplists`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "grouplists": "[...grouplists]",
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

[
    ...grouplists,
]
```


## Create a New Grouplist
**Method:** `POST` 

**Path:** `/v1.0/users/{username}/grouplists`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "title": "title",
    "groupId": "groupId"
}
```
#### Response
```
Status: 201 CREATED
Content-Type: application/json

{
    "objectId": "objectId",
    "username": "username",
    "groupId": "groupId",
    "title": "title",
    "checklists": [...checklists]
}
```


## Edit Grouplist's Title
**Method:** `PATCH` 

**Path:** `/v1.0/grouplists/{groupId}`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "title": "title",
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "username": "username",
    "groupId": "groupId",
    "title": "title",
    "checklists": [...checklists]
}
```


## Edit Grouplist's Checklists
**Method:** `PATCH` 

**Path:** `/v1.0/grouplists/{groupId}/checklists`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "checklists": [...checklists]
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "username": "username",
    "groupId": "groupId",
    "title": "title",
    "checklists": [...checklists]
}
```


## Create New Checklist Under a Grouplist
**Method:** `POST` 

**Path:** `/v1.0/grouplists/{groupId}/checklists`
#### Request
```
Content-Type: application/json
Authorization: Bearer **access-token**

{
    "listId": "listId",
    "title": "title"
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "username": "username",
    "groupId": "groupId",
    "title": "title",
    "checklists": [...checklists]
}
```


## Delete Grouplist
**Method:** `DELETE` 

**Path:** `/v1.0/grouplists/{groupId}`
#### Request
```
Authorization: Bearer **access-token**
```
#### Response
```
Status: 204 NO CONTENT
Content-Type: application/json
```
***


## Institution
- [Create a New Academic Institution](#create-a-new-academic-institution)
- [Get Institution Information Based on Profile](#get-institution-information-based-on-profile)
- [Edit Academic Institution's Name](#edit-academic-institutions-name)
- [Delete Academic Institution](#delete-academic-institution)

## Create a New Academic Institution
**Method:** `POST` 

**Path:** `/v1.0/auth/institutions`
#### Request
```
Content-Type: application/json

{
    "schoolName": "schoolName",
}
```
#### Response
```
Status: 201 CREATED
Content-Type: application/json

{
    "objectId": "objectId",
    "schoolId": "schoolId",
    "schoolName": "schoolName",
    "profIdCode": "profIdCode"
    "studIdCode": 'studIdCode',
    "departments": [...departments],
    "deptNames": [...deptNames],
    "majors": [...majors],
    "minors": [...minors],
    "professors": [...professors],
    "students": [...students]
}
```

## Get Institution Information Based on Profile
**Method:** `GET` 

**Path:** `/v1.0/auth/institutions/profiles/{profiles}/{codeId}`
#### Request
```
Content-Type: application/json
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "schoolName": "schoolName",
    "depts": [...departments],
    "majors": [...majors],
    "minors": [...minors],
}
```


## Edit Academic Institution's Name
**Method:** `PATCH` 

**Path:** `/v1.0/auth/institutions/{schoolId}`
#### Request
```
Content-Type: application/json

{
    "schoolName": "schoolName",
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "schoolId": "schoolId",
    "schoolName": "schoolName",
    "profIdCode": "profIdCode"
    "studIdCode": 'studIdCode',
    "departments": [...departments],
    "deptNames": [...deptNames],
    "majors": [...majors],
    "minors": [...minors],
    "professors": [...professors],
    "students": [...students]
}
```


## Delete Academic Institution
**Method:** `DELETE` 

**Path:** `/v1.0/auth/institutions/{schoolId}`
#### Response
```
Status: 204 NO CONTENT
```
***

## Department
- [Create a New Department](#create-a-new-department)
- [Rename Department](#rename-department)
- [Delete Department](#delete-department)
- [Create a New Major Under a Department](#create-a-new-major-under-a-department)
- [Delete Major](#delete-major)
- [Create a New Minor Under a Department](#create-a-new-minor-under-a-department)
- [Delete Minor](#delete-minor)

## Create a New Department
**Method:** `POST` 

**Path:** `/v1.0/auth/institutions/{schoolId}/departments`
#### Request
```
Content-Type: application/json

{
    "deptName": "deptName",
}
```
#### Response
```
Status: 201 CREATED
Content-Type: application/json

{
    "objectId": "objectId",
    "schoolId": "schoolId",
    "schoolName": "schoolName",
    "profIdCode": "profIdCode"
    "studIdCode": 'studIdCode',
    "departments": [...departments],
    "deptNames": [...deptNames],
    "majors": [...majors],
    "minors": [...minors],
    "professors": [...professors],
    "students": [...students]
}
```


## Rename Department
**Method:** `PATCH` 

**Path:** `/v1.0/auth/departments/{deptId}`
#### Request
```
Content-Type: application/json

{
    "deptName": "deptName",
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "deptId": "deptId",
    "dept": "dept",
    "schoolName": "schoolName",
    "majors": [...majors],
    "minors": [...minors],
}
```


## Delete Department
**Method:** `DELETE` 

**Path:** `/v1.0/auth/departments/{deptId}`
#### Response
```
Status: 204 NO CONTENT
```


## Create a New Major Under a Department
**Method:** `PATCH` 

**Path:** `/v1.0/auth/departments/{deptId}/majors`
#### Request
```
Content-Type: application/json

{
    "schoolId": "schoolId",
    "major": "major"
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "deptId": "deptId",
    "dept": "dept",
    "schoolName": "schoolName",
    "majors": [...majors],
    "minors": [...minors],
}
```


## Delete Major
**Method:** `DELETE` 

**Path:** `/v1.0/auth/departments/{deptId}/majors`
#### Request
```
Content-Type: application/json

{
    "schoolId": "schoolId",
    "major": "major"
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "deptId": "deptId",
    "dept": "dept",
    "schoolName": "schoolName",
    "majors": [...majors],
    "minors": [...minors],
}
```


## Create a New Minor Under a Department
**Method:** `PATCH` 

**Path:** `/v1.0/auth/departments/{deptId}/minors`
#### Request
```
Content-Type: application/json

{
    "schoolId": "schoolId",
    "minor": "minor"
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "deptId": "deptId",
    "dept": "dept",
    "schoolName": "schoolName",
    "majors": [...majors],
    "minors": [...minors],
}
```


## Delete Minor
**Method:** `DELETE` 

**Path:** `/v1.0/auth/departments/{deptId}/minors`
#### Request
```
Content-Type: application/json

{
    "schoolId": "schoolId",
    "minor": "minor"
}
```
#### Response
```
Status: 200 OK
Content-Type: application/json

{
    "objectId": "objectId",
    "deptId": "deptId",
    "dept": "dept",
    "schoolName": "schoolName",
    "majors": [...majors],
    "minors": [...minors],
}
```
***
