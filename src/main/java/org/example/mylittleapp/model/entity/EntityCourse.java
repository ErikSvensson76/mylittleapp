package org.example.mylittleapp.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.example.mylittleapp.model.entity.EntityProperties.EntityCourseProperties.*;

@Entity
@Table(name = TABLE)
public class EntityCourse {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = PK_COURSE, updatable = false)
  private String id;
  @Column(name = LANGUAGE, unique = true)
  private String language;

  @OneToMany(
      cascade = {CascadeType.DETACH, CascadeType.REFRESH},
      fetch = FetchType.LAZY,
      mappedBy = "course"
  )
  private List<EntityTopic> topics;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public List<EntityTopic> getTopics() {
    if(topics == null) topics = new ArrayList<>();
    return topics;
  }

  public void setTopics(List<EntityTopic> topics) {
    if(topics == null) topics = new ArrayList<>();
    if(this.topics == null) this.topics = new ArrayList<>();

    if(topics.isEmpty()){
      this.topics.forEach(entityTopic -> entityTopic.setCourse(null));
    }else{
      topics.forEach(entityTopic -> entityTopic.setCourse(this));
    }
    this.topics = topics;
  }

  public void addTopic(EntityTopic topic) {
    if(topic == null) return;
    if(topics == null) topics = new ArrayList<>();
    if(!topics.contains(topic)){
      topic.setCourse(this);
      topics.add(topic);
    }
  }

  public void removeTopic(EntityTopic topic) {
    if(topic == null) return;
    if(topics == null) topics = new ArrayList<>();
    if(topics.contains(topic) && topics.remove(topic)){
      topic.setCourse(null);
    }
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityCourse that)) return false;
    return Objects.equals(id, that.id) && Objects.equals(language, that.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, language);
  }
}
