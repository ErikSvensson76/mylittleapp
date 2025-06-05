package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Topic;
import org.example.mylittleapp.model.input.InputTopic;
import org.example.mylittleapp.service.converter.DtoConverterService;
import org.example.mylittleapp.service.dao.EntityTopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TopicServiceImpl implements TopicService{

  private final EntityTopicService entityTopicService;
  private final DtoConverterService converterService;

  public TopicServiceImpl(EntityTopicService entityTopicService, DtoConverterService converterService) {
    this.entityTopicService = entityTopicService;
    this.converterService = converterService;
  }

  @Override
  public Topic findByTopic(String topic) {
    return converterService.entityToTopic(entityTopicService.findByTopic(topic));
  }

  @Override
  public Topic findById(String id) {
    return converterService.entityToTopic(entityTopicService.findById(id));
  }

  @Override
  public List<Topic> findAll() {
    return entityTopicService.findAll().stream()
        .filter(Objects::nonNull)
        .map(converterService::entityToTopic)
        .sorted(Comparator.comparing(Topic::order))
        .toList();
  }

  @Override
  public InputTopic save(InputTopic inputTopic) {
    return converterService.entityToInputTopic(entityTopicService.save(inputTopic));
  }

  @Override
  public List<InputTopic> saveAll(List<InputTopic> inputTopics) {
    return entityTopicService.saveAll(inputTopics).stream()
        .filter(Objects::nonNull)
        .map(converterService::entityToInputTopic)
        .sorted(Comparator.comparing(InputTopic::getOrder))
        .toList();
  }

  @Override
  public boolean delete(String id) {
    return entityTopicService.delete(id);
  }

  @Override
  public boolean deleteAll(List<String> ids) {
    return entityTopicService.deleteAll(ids);
  }
}
