package org.example.mylittleapp.service.dao;

import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.entity.EntityLesson;
import org.example.mylittleapp.model.input.InputLesson;
import org.example.mylittleapp.model.input.InputMarkdown;
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
class EntityLessonServiceDaoTest {

  @Autowired private EntityLessonServiceDao underTest;
  @Autowired private TestEntityManager em;
  private final TestUtils testUtils = TestUtils.getInstance();

  @Test
  void saveSimpleLesson() {
    InputLesson input = testUtils.getInputLesson();

    EntityLesson result = underTest.save(input);

    assertNotNull(result);
    assertNotNull(result.getId());
    assertNotNull(result.getLesson());
    assertNotNull(result.getOrder());
    assertNull(result.getTopic());
    assertTrue(result.getSlides().isEmpty());
  }

  @Test
  void updateSimpleLesson() {
    EntityLesson lesson = em.persist(testUtils.getEntityLesson(false));
    InputLesson input = new InputLesson();
    input.setId(lesson.getId());
    input.setOrder(1);
    input.setLesson("Lesson 1");
    input.setSlides(
        Stream.generate(testUtils::getInputMarkdown)
            .limit(5)
            .collect(Collectors.toList())
    );

    EntityLesson result = underTest.save(input);
    assertNotNull(result);
    assertEquals(input.getId(), result.getId());
    assertEquals(input.getOrder(), result.getOrder());
    assertEquals(input.getLesson(), result.getLesson());
    assertEquals(5, result.getSlides().size());

  }

  @Test
  void saveAllLessons() {
    List<InputLesson> inputs = Stream.generate(testUtils::getInputLesson)
        .limit(5)
        .collect(Collectors.toList());

    List<EntityLesson> result = underTest.saveAll(inputs);
    assertNotNull(result);
    assertEquals(5, result.size());
  }

  @Test
  void updateLessonWithNestedMarkdown() {
    InputLesson input = testUtils.getInputLesson();
    input.setSlides(
        Stream.generate(testUtils::getInputMarkdown)
            .limit(5)
            .collect(Collectors.toList())
    );
    EntityLesson entity = underTest.save(input);
    assertNotNull(entity);

    List<InputMarkdown> slides = entity.getSlides().stream()
        .map(markdown -> {
          InputMarkdown m = new InputMarkdown();
          m.setId(markdown.getId());
          m.setOrder(markdown.getOrder());
          m.setMarkdownContent(markdown.getMarkdownContent());
          return m;
        }).collect(Collectors.toList());
      input.setSlides(slides);

      assertTrue(slides.remove(slides.getLast()));

      EntityLesson result = underTest.save(input);
      assertNotNull(result);
      assertEquals(4, result.getSlides().size());
  }

  @Test
  void deleteLesson() {
    EntityLesson lesson = em.persist(testUtils.getEntityLesson(false));
    assertNotNull(lesson);
    String id = lesson.getId();

    assertTrue(underTest.delete(id));
  }

  @Test
  void deleteLessonWithNestedMarkdown() {
    EntityLesson lesson = em.persist(testUtils.getEntityLesson(false));
    assertNotNull(lesson);
    lesson.setSlides(Stream.generate(() -> testUtils.getEntityMarkdown(false))
        .limit(5)
        .map(em::persist)
        .collect(Collectors.toList())
    );

    assertFalse(lesson.getSlides().isEmpty());
    assertTrue(underTest.delete(lesson.getId()));
  }

}