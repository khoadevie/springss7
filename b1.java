package com.example.coursemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

@SpringBootApplication
public class CourseManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseManagementApplication.class, args);
    }
}

enum CourseStatus {
    ACTIVE,
    INACTIVE
}

@Entity
@Table(name = "instructors")
class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    public Instructor() {}

    public Instructor(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}

@Entity
@Table(name = "courses")
class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status;

    @Column(nullable = false)
    private Long instructorId;

    public Course() {}

    public Course(Long id, String title, CourseStatus status, Long instructorId) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.instructorId = instructorId;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public CourseStatus getStatus() { return status; }
    public Long getInstructorId() { return instructorId; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setStatus(CourseStatus status) { this.status = status; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }
}

interface InstructorRepository extends JpaRepository<Instructor, Long> {}
interface CourseRepository extends JpaRepository<Course, Long> {}