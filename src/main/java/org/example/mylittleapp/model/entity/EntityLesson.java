package org.example.mylittleapp.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.example.mylittleapp.model.entity.EntityProperties.EntityLessonProperties.*;


@Entity
@Table(name = TABLE)
public class EntityLesson {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = PK_LESSON, updatable = false)
  private String id;
  @Column(name = LESSON, unique = true)
  private String lesson;
  @Column(name = ORDER)
  private Integer order;
  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.DETACH, CascadeType.REFRESH}
  )
  @JoinColumn(name = FK_TOPIC)
  private EntityTopic topic;

  @OneToMany(
      cascade = {CascadeType.DETACH, CascadeType.REFRESH},
      fetch = FetchType.LAZY,
      mappedBy = "lesson"
  )
  private List<EntityMarkdown> slides;

  public EntityLesson(String id, String lesson, Integer order, EntityTopic topic, List<EntityMarkdown> slides) {
    this.id = id;
    this.lesson = lesson;
    this.order = order;
    this.topic = topic;
    this.slides = slides;
  }

  public EntityLesson() {
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

  public List<EntityMarkdown> getSlides() {
    if (slides == null) slides = new ArrayList<>();
    return slides;
  }

  public void setSlides(List<EntityMarkdown> slides) {
    if(slides == null) slides = new ArrayList<>();
    if(this.slides == null) this.slides = new ArrayList<>();
    if(slides.isEmpty()){
      this.slides.forEach(slide -> slide.setLesson(null));
    }else{
      slides.forEach(slide -> slide.setLesson(this));
    }
    this.slides = slides;
  }

  public void addSlide(EntityMarkdown slide) {
    if(slide == null) return;
    if(slides == null) slides = new ArrayList<>();
    if(!slides.contains(slide)){
      slides.add(slide);
      slide.setLesson(this);
    }
  }

  public void removeSlide(EntityMarkdown slide){
    if(slide == null) return;
    if(slides == null) slides = new ArrayList<>();
    if(slides.remove(slide)){
      slide.setLesson(null);
    }
  }

  public EntityTopic getTopic() {
    return topic;
  }

  public void setTopic(EntityTopic topic) {
    this.topic = topic;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityLesson that)) return false;
    return Objects.equals(id, that.id) && Objects.equals(lesson, that.lesson);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, lesson);
  }
}
