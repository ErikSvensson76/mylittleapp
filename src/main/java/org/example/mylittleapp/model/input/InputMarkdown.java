package org.example.mylittleapp.model.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import org.example.mylittleapp.validation.OnPost;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

@Validated
public class InputMarkdown implements Serializable {

  @Null(groups = OnPost.class)
  private String id;
  @NotBlank(message = "This field is mandatory.")
  @Positive(message = "This field requires a positive value.")
  private Integer order;
  private String markdownContent;
  private Integer cursorPos;
  private List<String> urls;

  public InputMarkdown(String id, Integer order, String markdownContent, Integer cursorPos, List<String> urls) {
    this.id = id;
    this.order = order;
    this.markdownContent = markdownContent;
    this.cursorPos = cursorPos;
    this.urls = urls;
  }

  public InputMarkdown() {
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

  public String getMarkdownContent() {
    return markdownContent;
  }

  public void setMarkdownContent(String markdownContent) {
    this.markdownContent = markdownContent;
  }

  public Integer getCursorPos() {
    return cursorPos;
  }

  public void setCursorPos(Integer cursorPos) {
    this.cursorPos = cursorPos;
  }

  public List<String> getUrls() {
    return urls;
  }

  public void setUrls(List<String> urls) {
    this.urls = urls;
  }
}
