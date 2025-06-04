package org.example.mylittleapp.service.dao;

import org.example.mylittleapp.exception.NoEntityFoundException;
import org.example.mylittleapp.model.entity.EntityLesson;
import org.example.mylittleapp.model.entity.EntityTopic;
import org.example.mylittleapp.model.input.InputLesson;
import org.example.mylittleapp.service.repos.EntityLessonRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class EntityLessonServiceDao implements EntityLessonService {

  private final EntityLessonRepo repository;
  private final EntityMarkdownService entityMarkdownService;

  public EntityLessonServiceDao(EntityLessonRepo repository, EntityMarkdownService entityMarkdownService) {
    this.repository = repository;
    this.entityMarkdownService = entityMarkdownService;
  }

  @Override
  public EntityLesson findByLesson(String lesson) {
    return repository.findByLesson(lesson)
        .orElseThrow(() -> new NoEntityFoundException("No entity found for lesson " + lesson));
  }

  @Override
  public EntityLesson findById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoEntityFoundException("No entity found for id " + id));
  }

  @Override
  public List<EntityLesson> findAll() {
    return repository.findAll();
  }

  @Override
  public EntityLesson save(InputLesson inputLesson) {
    if (inputLesson == null) throw new IllegalArgumentException("inputLesson cannot be null");
    EntityLesson entityLesson;
    if(inputLesson.getId() == null){
      entityLesson = new EntityLesson();
      entityLesson.setLesson(inputLesson.getLesson().trim());
      entityLesson.setOrder(inputLesson.getOrder());
      entityLesson = repository.save(entityLesson);
    }else{
      entityLesson = findById(inputLesson.getId());
      entityLesson.setOrder(inputLesson.getOrder());
      entityLesson.setLesson(inputLesson.getLesson().trim());
    }
    if(inputLesson.getSlides() != null && !inputLesson.getSlides().isEmpty()){
      entityLesson.setSlides(
          entityMarkdownService.saveAll(inputLesson.getSlides())
      );
    }
    return repository.save(entityLesson);
  }

  @Override
  public List<EntityLesson> saveAll(List<InputLesson> inputLessons) {
    if(inputLessons == null) throw new IllegalArgumentException("inputLessons cannot be null");
    return inputLessons.stream()
        .filter(Objects::nonNull)
        .map(this::save)
        .collect(Collectors.toList());
  }

  @Override
  public boolean delete(String id) {
    EntityLesson entityLesson = findById(id);
    entityLesson.setSlides(null);
    EntityTopic topic = entityLesson.getTopic();
    if(topic != null && !topic.getLessons().isEmpty()){
      topic.removeLesson(entityLesson);
    }
    repository.delete(entityLesson);
    return repository.findById(id).isEmpty();
  }

  @Override
  public boolean deleteAll(List<String> ids) {
    if(ids == null) throw new IllegalArgumentException("ids cannot be null");
    return ids.stream()
        .filter(Objects::nonNull)
        .map(this::delete)
        .allMatch(bool -> bool.equals(true));
  }
}
