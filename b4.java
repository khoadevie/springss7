package com.example.demo;

import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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
    private String status;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @OneToMany(mappedBy = "course")
    private List<StudentEnrollment> enrollments;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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

class CourseCreateRequest {
    private String title;
    private String status;
    private Long instructorId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }
}

class CourseUpdateRequest {
    private String title;
    private String status;
    private Long instructorId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }
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

@Service
class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    public CourseService(CourseRepository courseRepository, InstructorRepository instructorRepository) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    public Course createCourse(CourseCreateRequest req) {
        Instructor instructor = instructorRepository.findById(req.getInstructorId()).orElseThrow();
        Course course = new Course();
        course.setTitle(req.getTitle());
        course.setStatus(req.getStatus());
        course.setInstructor(instructor);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, CourseUpdateRequest req) {
        Course course = courseRepository.findById(id).orElseThrow();
        if (req.getTitle() != null) course.setTitle(req.getTitle());
        if (req.getStatus() != null) course.setStatus(req.getStatus());
        if (req.getInstructorId() != null) {
            Instructor instructor = instructorRepository.findById(req.getInstructorId()).orElseThrow();
            course.setInstructor(instructor);
        }
        return courseRepository.save(course);
    }
}

@Service
class StudentEnrollmentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentEnrollmentRepository enrollmentRepository;

    public StudentEnrollmentService(StudentRepository studentRepository,
                                    CourseRepository courseRepository,
                                    StudentEnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public StudentEnrollment enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        return enrollmentRepository.save(enrollment);
    }
}

@RestController
@RequestMapping("/instructors")
class InstructorController {
    private final InstructorService service;

    public InstructorController(InstructorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InstructorCreateRequest req) {
        service.createInstructor(req);
        return ResponseEntity.ok("created");
    }

    @GetMapping
    public ResponseEntity<List<Instructor>> findAll() {
        return ResponseEntity.ok(service.findAllInstructors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return service.findInstructorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

@RestController
@RequestMapping("/courses")
class CourseController {
    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CourseCreateRequest req) {
        service.createCourse(req);
        return ResponseEntity.ok("created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CourseUpdateRequest req) {
        service.updateCourse(id, req);
        return ResponseEntity.ok("updated");
    }
}

@RestController
@RequestMapping("/enrollments")
class EnrollmentController {
    private final StudentEnrollmentService service;

    public EnrollmentController(StudentEnrollmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> enroll(@RequestParam Long studentId, @RequestParam Long courseId) {
        service.enrollStudent(studentId, courseId);
        return ResponseEntity.ok("enrolled");
    }
}