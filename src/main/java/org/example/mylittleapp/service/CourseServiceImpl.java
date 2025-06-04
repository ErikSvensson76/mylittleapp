package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Course;
import org.example.mylittleapp.model.input.InputCourse;
import org.example.mylittleapp.service.converter.DtoConverterService;
import org.example.mylittleapp.service.dao.EntityCourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

  private final EntityCourseService entityCourseService;
  private final DtoConverterService converterService;

  public CourseServiceImpl(EntityCourseService entityCourseService, DtoConverterService converterService) {
    this.entityCourseService = entityCourseService;
    this.converterService = converterService;
  }

  @Override
  public Course findByLanguage(String language) {
    return converterService.entityToCourse(entityCourseService.findByLanguage(language));
  }

  @Override
  public Course findById(String id) {
    return converterService.entityToCourse(entityCourseService.findById(id));
  }

  @Override
  public List<Course> findAll() {
    return entityCourseService.findAll().stream()
        .filter(Objects::nonNull)
        .map(converterService::entityToCourse)
        .toList();
  }

  @Override
  public InputCourse save(InputCourse inputCourse) {
    return converterService.entityToInputCourse(entityCourseService.save(inputCourse));
  }

  @Override
  public List<InputCourse> saveAll(List<InputCourse> inputCourses) {
    return entityCourseService.saveAll(inputCourses).stream()
        .filter(Objects::nonNull)
        .map(converterService::entityToInputCourse)
        .toList();
  }

  @Override
  public boolean delete(String id) {
    return entityCourseService.delete(id);
  }

  @Override
  public boolean deleteAll(List<String> ids) {
    return entityCourseService.deleteAll(ids);
  }

}
