package org.example.mylittleapp.model.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.example.mylittleapp.validation.OnPost;
import org.example.mylittleapp.validation.OnPut;
import org.example.mylittleapp.validation.annotations.UniqueLanguage;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

@Validated
@UniqueLanguage(groups = {OnPost.class, OnPut.class})
public class InputCourse implements Serializable {

  @Null(groups = OnPost.class)
  private String id;
  @NotBlank(message = "This field is mandatory")
  private String language;
  private List<@Valid @NotNull InputTopic> topics;

  public InputCourse(String id, String language, List<@Valid @NotNull InputTopic> topics) {
    this.id = id;
    this.language = language;
    this.topics = topics;
  }

  public InputCourse() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage (String language) {
    this.language = language;
  }

  public List<InputTopic> getTopics() {
    return topics;
  }

  public void setTopics(List<InputTopic> topics) {
    this.topics = topics;
  }

}


