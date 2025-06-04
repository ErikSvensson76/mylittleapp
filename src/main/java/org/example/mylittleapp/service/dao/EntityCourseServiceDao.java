package org.example.mylittleapp.service.dao;

import org.example.mylittleapp.exception.NoEntityFoundException;
import org.example.mylittleapp.model.entity.EntityCourse;
import org.example.mylittleapp.model.input.InputCourse;
import org.example.mylittleapp.service.repos.EntityCourseRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class EntityCourseServiceDao implements EntityCourseService {

  private final EntityCourseRepo repository;
  private final EntityTopicService entityTopicService;

  public EntityCourseServiceDao(EntityCourseRepo repository, EntityTopicService entityTopicService) {
    this.repository = repository;
    this.entityTopicService = entityTopicService;
  }

  @Override
  public EntityCourse findByLanguage(String language) {
    return repository.findByLanguage(language)
        .orElseThrow(() -> new NoEntityFoundException("No entity course found with language: " + language));
  }

  @Override
  public EntityCourse findById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoEntityFoundException("No entity found with id: " + id));
  }

  @Override
  public List<EntityCourse> findAll() {
    return repository.findAll();
  }

  @Override
  public EntityCourse save(InputCourse inputCourse) {
    if(inputCourse == null) throw new IllegalArgumentException("Input course cannot be null");

    EntityCourse entityCourse;
    if(inputCourse.getId() == null){
      entityCourse = new EntityCourse();
      entityCourse.setLanguage(inputCourse.getLanguage().trim());
      entityCourse = repository.save(entityCourse);
    }else {
      entityCourse = findById(inputCourse.getId());
      entityCourse.setLanguage(inputCourse.getLanguage().trim());
    }
    if(inputCourse.getTopics() != null && !inputCourse.getTopics().isEmpty()){
      entityCourse.setTopics(
          entityTopicService.saveAll(inputCourse.getTopics())
      );
    }
    return repository.save(entityCourse);
  }

  @Override
  public List<EntityCourse> saveAll(List<InputCourse> inputCourses) {
    if(inputCourses == null) throw new IllegalArgumentException("inputCourses cannot be null");
    return inputCourses.stream()
        .filter(Objects::nonNull)
        .map(this::save)
        .collect(Collectors.toList());
  }

  @Override
  public boolean delete(String id) {
    EntityCourse entityCourse = findById(id);
    entityCourse.setTopics(null);
    repository.delete(entityCourse);
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
