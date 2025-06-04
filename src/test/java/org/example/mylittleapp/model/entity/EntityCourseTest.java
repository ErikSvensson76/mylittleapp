package org.example.mylittleapp.model.entity;

import org.example.mylittleapp.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EntityCourseTest {

  TestUtils testUtils = TestUtils.getInstance();

  @Test
  void entityInstantiation() {
    EntityCourse course = testUtils.getEntityCourse(true);

    course.setTopics(
        Stream.generate(() -> testUtils.getEntityTopic(true))
            .limit(5)
            .toList()
    );
    assertNotNull(course);
    assertNotNull(course.getId());
    assertNotNull(course.getTopics());
    assertNotNull(course.getLanguage());
    assertEquals(5, course.getTopics().size());
    assertTrue(
        course.getTopics().stream().allMatch(entityTopic -> entityTopic.getCourse().equals(course))
    );
  }

  @Test
  void removeAllTopics(){
    EntityCourse course = testUtils.getEntityCourse(true);
    course.setTopics(
        Stream.generate(() -> testUtils.getEntityTopic(true))
            .limit(2)
            .toList()
    );

    assertNotNull(course);
    assertEquals(2, course.getTopics().size());

    course.setTopics(null);

    assertTrue(course.getTopics().isEmpty());
  }

  @Test
  void removeSingleTopic(){
    EntityCourse course = testUtils.getEntityCourse(true);
    EntityTopic topic = testUtils.getEntityTopic(true);
    course.setTopics(new ArrayList<>(Collections.singletonList(topic)));

    assertNotNull(course);
    assertEquals(1, course.getTopics().size());

    course.removeTopic(topic);
    assertTrue(course.getTopics().isEmpty());
  }

  @Test
  void addSingleTopic(){
    EntityCourse course = testUtils.getEntityCourse(true);
    EntityTopic topic = testUtils.getEntityTopic(true);
    course.addTopic(topic);

    assertNotNull(course);
    assertEquals(1, course.getTopics().size());
  }
}