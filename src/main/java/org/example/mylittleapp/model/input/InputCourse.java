package org.example.mylittleapp.model.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.mylittleapp.validation.OnPut;
import org.example.mylittleapp.validation.annotations.UniqueLanguage;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

@Validated
public class InputCourse implements Serializable {

  @NotBlank(groups = OnPut.class)
  private String id;
  @NotBlank(message = "This field is mandatory")
  @UniqueLanguage
  private String language;
  @NotBlank(message = "This field is mandatory")
  private String difficulty;
  private List<@NotNull InputTopic> topics;

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

  public String getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  public List<InputTopic> getTopics() {
    return topics;
  }

  public void setTopics(List<InputTopic> topics) {
    this.topics = topics;
  }

}


