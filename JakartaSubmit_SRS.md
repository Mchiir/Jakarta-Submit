# Software Requirements Specification (SRS)

## 1. Introduction

### 1.1 Purpose
The purpose of this document is to define the Software Requirements Specification (SRS) for the **JakartaSubmit** system, an online submission platform for students to submit their work, assignments, and projects, while instructors provide tasks related to specific courses. This system ensures secure, efficient, and deadline-bound submissions.

### 1.2 Scope
**JakartaSubmit** is a Jakarta EE web-based application that allows:
- Students to submit assignments before deadlines.
- Instructors to create and manage submission tasks.
- Secure storage of all submissions and instructor tasks on a local server.
- Access control with authentication and authorization.

### 1.3 Technologies Used
- **Backend**: Jakarta EE, Servlets
- **Database**: PostgreSQL with Hibernate ORM
- **Frontend**: JSP, Bootstrap
- **Server**: Tomcat

## 2. Functional Requirements

### 2.1 User Roles
#### 1. Student
- Register/Login to the system.
- View available assignments.
- Submit assignments in **PDF, Excel, PPTX, or ZIP** format.
- Track submission status.

#### 2. Instructor
- Register/Login to the system.
- Create tasks for students.
- Set deadlines for submissions.
- View student submissions.

### 2.2 System Features
- **Authentication**: Secure login and access control for students and instructors.
- **Task Management**: Instructors can create, edit, and delete submission tasks.
- **Submission Handling**: Students can upload submissions and receive confirmation.
- **Deadline Enforcement**: Submissions must be made before a given deadline.
- **Storage Management**: All files are stored securely on the local server.

## 3. Non-Functional Requirements

### 3.1 Performance Requirements
- The system should handle multiple concurrent submissions.
- Submissions should be processed and stored within **3 seconds**.

### 3.2 Security Requirements
- Passwords should be securely hashed.
- Only authenticated users should access the system.
- File uploads should be scanned for malicious content.

### 3.3 Availability and Reliability
- The system should be available **99.5%** of the time.
- Auto-backup of stored data should be implemented.

## 4. Database Design

### 4.1 Tables
#### **Users Table**
```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) CHECK (role IN ('STUDENT', 'INSTRUCTOR')) NOT NULL
);
```

#### **Tasks Table**
```sql
CREATE TABLE tasks (
    task_id UUID PRIMARY KEY,
    instructor_id UUID REFERENCES users(user_id),
    course_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    deadline TIMESTAMP NOT NULL
);
```

#### **Submissions Table**
```sql
CREATE TABLE submissions (
    submission_id UUID PRIMARY KEY,
    student_id UUID REFERENCES users(user_id),
    task_id UUID REFERENCES tasks(task_id),
    file_path VARCHAR(255) NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 5. System Architecture
- **Model Layer**: Hibernate ORM for database interaction.
- **Controller Layer**: Servlets handling requests.
- **View Layer**: JSP for rendering the UI with Bootstrap.
- **Server**: Apache Tomcat for hosting.

## 6. Assumptions and Constraints
- Users must have valid accounts before accessing the system.
- Files larger than **50MB** are not supported.
- The system will run on **localhost** but can be extended to cloud hosting.

## 7. Future Enhancements
- Implement **cloud storage integration**.
- Add **email notifications** for submission deadlines.
- Extend support for additional file types.

---
