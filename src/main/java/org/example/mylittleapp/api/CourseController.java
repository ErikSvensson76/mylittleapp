package org.example.mylittleapp.api;

import jakarta.validation.Valid;
import org.example.mylittleapp.model.dto.Course;
import org.example.mylittleapp.model.input.InputCourse;
import org.example.mylittleapp.service.CourseService;
import org.example.mylittleapp.validation.OnPost;
import org.example.mylittleapp.validation.OnPut;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/course")
public class CourseController {

  private final CourseService courseService;


  public CourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  @GetMapping()
  public ResponseEntity<List<Course>> getCourses() {
    return ResponseEntity.ok(
        courseService.findAll()
    );
  }

  @PostMapping()
  public ResponseEntity<InputCourse> addCourse(@RequestBody @Validated(OnPost.class) @Valid InputCourse course) {
    return ResponseEntity.ok(
        courseService.save(course)
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<Course> findCourseById(@PathVariable String id) {
    return ResponseEntity.ok(
        courseService.findById(id)
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<InputCourse> updateCourse(
      @PathVariable String id,
      @Validated(OnPut.class) @RequestBody @Valid InputCourse course) {
    course.setId(id);
    return ResponseEntity.ok(courseService.save(course));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
    boolean status = courseService.delete(id);
    if(status) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}
