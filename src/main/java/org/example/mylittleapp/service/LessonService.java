package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Lesson;
import org.example.mylittleapp.model.input.InputLesson;

import java.util.List;

public interface LessonService {

  Lesson findByLesson(String lesson);

  Lesson findById(String id);

  List<Lesson> findAll();

  InputLesson save(InputLesson inputLesson);

  List<InputLesson> saveAll(List<InputLesson> inputLessons);

  boolean delete(String id);

  boolean deleteAll(List<String> ids);

}
