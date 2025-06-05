package org.example.mylittleapp.model.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.example.mylittleapp.validation.OnPost;
import org.example.mylittleapp.validation.OnPut;
import org.example.mylittleapp.validation.annotations.UniqueTopic;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

@Validated
@UniqueTopic(groups = {OnPost.class, OnPut.class})
public class InputTopic implements Serializable {

  @Null(groups = OnPost.class)
  private String id;
  @NotNull(message = "This field is mandatory.")
  @Positive(message = "This field requires a positive value.")
  private Integer order;
  @NotBlank(message = "This field is mandatory.")
  private String topic;
  @NotBlank(message = "This field is mandatory.")
  @Size(message = "Max 255 characters allowed.", max = 255)
  private String description;
  private List<@Valid @NotNull InputLesson> lessons;

  public InputTopic(String id, Integer order, String topic, String description, List<@Valid @NotNull InputLesson> lessons) {
    this.id = id;
    this.order = order;
    this.topic = topic;
    this.description = description;
    this.lessons = lessons;
  }

  public InputTopic() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<InputLesson> getLessons() {
    return lessons;
  }

  public void setLessons(List<InputLesson> lessons) {
    this.lessons = lessons;
  }
}
