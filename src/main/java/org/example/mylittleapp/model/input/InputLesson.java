package org.example.mylittleapp.model.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import org.example.mylittleapp.validation.OnPost;
import org.example.mylittleapp.validation.OnPut;
import org.example.mylittleapp.validation.annotations.UniqueLesson;

import java.util.List;

@UniqueLesson(groups = {OnPost.class, OnPut.class})
public class InputLesson {

  @Null(groups = OnPost.class)
  private String id;
  @NotBlank(message = "This field is mandatory.")
  private String lesson;
  @NotNull(message = "This field is mandatory.")
  @Positive(message = "This field requires a positive value.")
  private Integer order;
  private List<@NotNull InputMarkdown> slides;

  public InputLesson(String id, String lesson, Integer order, List<@Valid @NotNull InputMarkdown> slides) {
    this.id = id;
    this.lesson = lesson;
    this.order = order;
    this.slides = slides;
  }

  public InputLesson() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLesson() {
    return lesson;
  }

  public void setLesson(String lesson) {
    this.lesson = lesson;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public List<InputMarkdown> getSlides() {
    return slides;
  }

  public void setSlides(List<InputMarkdown> slides) {
    this.slides = slides;
  }
}
