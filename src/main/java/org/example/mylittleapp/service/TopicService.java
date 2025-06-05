package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Topic;
import org.example.mylittleapp.model.input.InputTopic;

import java.util.List;

public interface TopicService {

  Topic findByTopic(String topic);

  Topic findById(String id);

  List<Topic> findAll();

  InputTopic save(InputTopic inputTopic);

  List<InputTopic> saveAll(List<InputTopic> inputTopics);

  boolean delete(String id);

  boolean deleteAll(List<String> ids);

}
