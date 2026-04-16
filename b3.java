package com.example.demo;

import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@Entity
@Table(name = "students")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @OneToMany(mappedBy = "student")
    private List<StudentEnrollment> enrollments;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<StudentEnrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<StudentEnrollment> enrollments) { this.enrollments = enrollments; }
}

@Entity
@Table(name = "instructors")
class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}

@Entity
@Table(name = "courses")
class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @OneToMany(mappedBy = "course")
    private List<StudentEnrollment> enrollments;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public List<StudentEnrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<StudentEnrollment> enrollments) { this.enrollments = enrollments; }
}

@Entity
@Table(name = "student_enrollments")
class StudentEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}

interface StudentRepository extends JpaRepository<Student, Long> {}
interface InstructorRepository extends JpaRepository<Instructor, Long> {}
interface CourseRepository extends JpaRepository<Course, Long> {}
interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {}

class InstructorCreateRequest {
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

@Service
class InstructorService {
    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public Optional<Instructor> findInstructorById(Long id) {
        return instructorRepository.findById(id);
    }

    public List<Instructor> findAllInstructors() {
        return instructorRepository.findAll();
    }

    public Instructor createInstructor(InstructorCreateRequest request) {
        Instructor instructor = new Instructor();
        instructor.setName(request.getName());
        return instructorRepository.save(instructor);
    }
}