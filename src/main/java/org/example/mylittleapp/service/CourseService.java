package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Course;
import org.example.mylittleapp.model.input.InputCourse;

import java.util.List;


public interface CourseService {

  Course findById(String id);

  List<Course> findAll();

  Course findByLanguage(String language);

  InputCourse save(InputCourse inputCourse);

  List<InputCourse> saveAll(List<InputCourse> inputCourses);

  boolean delete(String id);

  boolean deleteAll(List<String> ids);

}
