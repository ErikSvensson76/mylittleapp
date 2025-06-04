package org.example.mylittleapp.service.converter;

import org.example.mylittleapp.exception.RuntimeIOException;
import org.example.mylittleapp.model.dto.*;
import org.example.mylittleapp.model.entity.*;
import org.example.mylittleapp.model.input.InputCourse;
import org.example.mylittleapp.model.input.InputLesson;
import org.example.mylittleapp.model.input.InputMarkdown;
import org.example.mylittleapp.model.input.InputTopic;
import org.example.mylittleapp.service.UrlMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;


@Component
@Transactional
public class DtoConverterService {

  private final UrlMap urlMap;

  public DtoConverterService(UrlMap urlMap) {
    this.urlMap = urlMap;
  }

  public Course entityToCourse(EntityCourse entity){
    Course course = null;
    if(entity != null){
      course = new Course(
          entity.getId(),
          entity.getLanguage(),
          entity.getTopics().isEmpty() ? new ArrayList<>() : entity.getTopics().stream()
              .filter(Objects::nonNull)
              .map(this::entityToTopic)
              .sorted(Comparator.comparing(Topic::order))
              .toList()
      );
    }
    return course;
  }

  public InputCourse entityToInputCourse(EntityCourse entity){
    InputCourse inputCourse = null;
    if(entity != null){
      inputCourse = new InputCourse(
        entity.getId(),
        entity.getLanguage(),
        entity.getTopics().isEmpty() ? new ArrayList<>() : entity.getTopics().stream()
          .filter(Objects::nonNull)
          .map(this::entityToInputTopic)
          .sorted(Comparator.comparing(InputTopic::getOrder))
          .toList()
      );
    }
    return inputCourse;
  }



  public Topic entityToTopic(EntityTopic entity){
    Topic topic = null;
    if(entity != null){
      topic = new Topic(
          entity.getId(),
          entity.getOrder(),
          entity.getTopic(),
          entity.getDescription(),
          entity.getLessons().isEmpty() ? new ArrayList<>() : entity.getLessons().stream()
              .filter(Objects::nonNull)
              .map(this::entityToLesson)
              .sorted(Comparator.comparing(Lesson::order))
              .toList()
      );
    }
    return topic;
  }

  public InputTopic entityToInputTopic(EntityTopic entity){
    InputTopic inputTopic = null;
    if(entity != null){
      inputTopic = new InputTopic(
        entity.getId(),
        entity.getOrder(),
        entity.getTopic(),
        entity.getDescription(),
        entity.getLessons().isEmpty() ? new ArrayList<>() : entity.getLessons().stream()
          .filter(Objects::nonNull)
          .map(this::entityToInputLesson)
          .sorted(Comparator.comparing(InputLesson::getOrder))
          .toList()
      );
    }
    return inputTopic;
  }

  public Lesson entityToLesson(EntityLesson entity){
    Lesson lesson = null;
    if(entity != null){
      lesson = new Lesson(
          entity.getId(),
          entity.getLesson(),
          entity.getOrder(),
          entity.getSlides().isEmpty() ? new ArrayList<>() : entity.getSlides().stream()
              .filter(Objects::nonNull)
              .map(this::entityToMarkdown)
              .sorted(Comparator.comparing(Markdown::order))
              .toList()
      );
    }
    return lesson;
  }

  public InputLesson entityToInputLesson(EntityLesson entity){
    InputLesson inputLesson = null;
    if(entity != null){
      inputLesson = new InputLesson(
        entity.getId(),
        entity.getLesson(),
        entity.getOrder(),
        entity.getSlides().isEmpty() ? new ArrayList<>() : entity.getSlides().stream()
          .filter(Objects::nonNull)
          .map(this::entityToInputMarkdown)
          .sorted(Comparator.comparing(InputMarkdown::getOrder))
          .toList()
      );
    }
    return inputLesson;
  }

  public Markdown entityToMarkdown(EntityMarkdown entity){
    Markdown markdown = null;
    if(entity != null){
      markdown = new Markdown(
          entity.getId(),
          entity.getOrder(),
          entity.getMarkdownContent()
      );
    }
    return markdown;
  }

  public InputMarkdown entityToInputMarkdown(EntityMarkdown entity){
    InputMarkdown inputMarkdown = null;
    if(entity != null){
      inputMarkdown = new InputMarkdown(
        entity.getId(),
        entity.getOrder(),
        entity.getMarkdownContent(),
        entity.getCursorPos(),
        urlMap.getUrls(entity.getId()).get()
      );
    }
    return inputMarkdown;
  }

  public Binary entityToBinary(EntityBinary entity) {
    Binary binary = null;
    if(entity != null){
      try{
        binary = new Binary(
            entity.getId(),
            entity.getFileName(),
            entity.getMimeType(),
            entity.getUrl(),
            new ByteArrayResource(entity.getBinaryData())
        );
      }catch (Exception e){
        throw new RuntimeIOException(e.getMessage(), e);
      }
    }
    return binary;
  }

}
