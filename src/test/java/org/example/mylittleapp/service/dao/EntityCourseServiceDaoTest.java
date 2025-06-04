package org.example.mylittleapp.service.dao;

import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.entity.EntityCourse;
import org.example.mylittleapp.model.input.InputCourse;
import org.example.mylittleapp.model.input.InputTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@DirtiesContext
class EntityCourseServiceDaoTest {

  @Autowired
  EntityCourseServiceDao underTest;
  @Autowired TestEntityManager em;
  TestUtils testUtils = TestUtils.getInstance();

  @Test
  void saveSimpleCourse() {
    InputCourse inputCourse = testUtils.getInputCourse();

    EntityCourse result = underTest.save(inputCourse);

    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals(inputCourse.getLanguage(), result.getLanguage());
    assertTrue(result.getTopics().isEmpty());
  }

  @Test
  void saveAllCourses() {
    List<InputCourse> input = Stream.generate(testUtils::getInputCourse)
        .limit(5)
        .collect(Collectors.toList());

    List<EntityCourse> result = underTest.saveAll(input);
    assertNotNull(result);
    assertEquals(input.size(), result.size());

  }

  @Test
  void updateSimpleCourse() {
    EntityCourse course = em.persist(testUtils.getEntityCourse(false));
    InputCourse inputCourse = new InputCourse();
    inputCourse.setId(course.getId());
    String language = "Java";
    inputCourse.setLanguage(language);

    EntityCourse result = underTest.save(inputCourse);
    assertNotNull(result);
    assertEquals(inputCourse.getId(), result.getId());
    assertEquals(language, result.getLanguage());
    assertTrue(result.getTopics().isEmpty());
  }

  @Test
  void saveCourseWithNestedTopics(){
    InputCourse inputCourse = testUtils.getInputCourse();
    inputCourse.setTopics(Stream.generate(
        () -> testUtils.getInputTopic()).
        limit(3)
        .collect(Collectors.toList()));

    EntityCourse result = underTest.save(inputCourse);
    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals(inputCourse.getLanguage(), result.getLanguage());
    assertEquals(3, result.getTopics().size());
    assertTrue(result.getTopics().stream().allMatch(t -> t.getCourse().equals(result)));
  }

  @Test
  void updateCourseWithNestedTopics(){
    InputCourse initialCourse = testUtils.getInputCourse();
    initialCourse.setTopics(Stream.generate(() -> testUtils.getInputTopic())
        .limit(3)
        .collect(Collectors.toList())
    );

    EntityCourse course = underTest.save(initialCourse);
    assertNotNull(course);

    InputCourse inputCourse = new InputCourse();
    inputCourse.setId(course.getId());
    inputCourse.setLanguage(course.getLanguage());

    List<InputTopic> topics = course.getTopics().stream()
        .map(t -> {
          InputTopic topic = new InputTopic();
          topic.setId(t.getId());
          topic.setTopic(t.getTopic());
          topic.setDescription(t.getDescription());
          topic.setOrder(t.getOrder());
          return topic;
        }).collect(Collectors.toList());
    inputCourse.setTopics(topics);

    assertTrue(topics.remove(topics.getLast()));

    EntityCourse result = underTest.save(inputCourse);
    assertNotNull(result);
    assertEquals(2, result.getTopics().size());
  }

  @Test
  void deleteCourse(){
    EntityCourse course = em.persist(testUtils.getEntityCourse(false));
    String id = course.getId();
    assertNotNull(course);

    boolean result = underTest.delete(id);

    assertTrue(result);
  }

  @Test
  void deleteCourseWithNestedTopics(){
    EntityCourse course = em.persist(testUtils.getEntityCourse(false));
    course.setTopics(Stream.generate(() -> testUtils.getEntityTopic(false))
        .limit(5)
        .map(em::persist)
        .collect(Collectors.toList()));

    assertNotNull(course);
    assertFalse(course.getTopics().isEmpty());

    boolean result = underTest.delete(course.getId());
    assertTrue(result);
  }
}