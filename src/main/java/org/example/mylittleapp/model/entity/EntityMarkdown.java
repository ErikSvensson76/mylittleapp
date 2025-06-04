package org.example.mylittleapp.model.entity;

import jakarta.persistence.*;

import java.util.Objects;

import static org.example.mylittleapp.model.entity.EntityProperties.EntityMarkdownProperties.*;


@Entity
@Table(name = TABLE)
public class EntityMarkdown {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = PK_MARKDOWN, updatable = false)
  private String id;
  @Column(name = ORDER)
  private Integer order;
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = MARKDOWN_CONTENT)
  private String markdownContent;
  @Column(name = CURSOR_POS)
  private Integer cursorPos;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = FK_LESSON)
  private EntityLesson lesson;

  public EntityMarkdown(String id, Integer order, String markdownContent) {
    this.id = id;
    this.order = order;
    this.markdownContent = markdownContent;
  }

  public EntityMarkdown(Integer order, String markdownContent) {
    this.order = order;
    this.markdownContent = markdownContent;
  }

  public EntityMarkdown() {}

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

  public EntityLesson getLesson() {
    return lesson;
  }

  public void setLesson(EntityLesson lesson) {
    this.lesson = lesson;
  }

  public Integer getCursorPos() {
    return cursorPos;
  }

  public void setCursorPos(Integer cursorPos) {
    this.cursorPos = cursorPos;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityMarkdown that)) return false;
    return Objects.equals(id, that.id) && Objects.equals(order, that.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, order);
  }
}
