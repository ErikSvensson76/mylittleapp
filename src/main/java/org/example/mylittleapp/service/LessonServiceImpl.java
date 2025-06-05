package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Lesson;
import org.example.mylittleapp.model.input.InputLesson;
import org.example.mylittleapp.service.converter.DtoConverterService;
import org.example.mylittleapp.service.dao.EntityLessonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LessonServiceImpl implements LessonService {

  private final EntityLessonService entityLessonService;
  private final DtoConverterService converterService;

  public LessonServiceImpl(EntityLessonService entityLessonService, DtoConverterService converterService) {
    this.entityLessonService = entityLessonService;
    this.converterService = converterService;
  }

  @Override
  public Lesson findByLesson(String lesson) {
    return converterService.entityToLesson(entityLessonService.findByLesson(lesson));
  }

  @Override
  public Lesson findById(String id) {
    return converterService.entityToLesson(entityLessonService.findById(id));
  }

  @Override
  public List<Lesson> findAll() {
    return entityLessonService.findAll().stream()
        .filter(Objects::nonNull)
        .map(converterService::entityToLesson)
        .sorted(Comparator.comparing(Lesson::order))
        .toList();
  }

  @Override
  public InputLesson save(InputLesson inputLesson) {
    return converterService.entityToInputLesson(entityLessonService.save(inputLesson));
  }

  @Override
  public List<InputLesson> saveAll(List<InputLesson> inputLessons) {
    return entityLessonService.saveAll(inputLessons).stream()
        .filter(Objects::nonNull)
        .map(converterService::entityToInputLesson)
        .sorted(Comparator.comparing(InputLesson::getOrder))
        .toList();
  }

  @Override
  public boolean delete(String id) {
    return entityLessonService.delete(id);
  }

  @Override
  public boolean deleteAll(List<String> ids) {
    return entityLessonService.deleteAll(ids);
  }
}
