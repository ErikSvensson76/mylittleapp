package org.example.mylittleapp.service.converter;

import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.dto.Course;
import org.example.mylittleapp.model.dto.Lesson;
import org.example.mylittleapp.model.dto.Markdown;
import org.example.mylittleapp.model.dto.Topic;
import org.example.mylittleapp.model.entity.*;
import org.example.mylittleapp.model.input.InputMarkdown;
import org.example.mylittleapp.service.event.EventOperationType;
import org.example.mylittleapp.service.event.UrlMapUpdateEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@DirtiesContext
@Transactional
class DtoConverterServiceTest {

  @Autowired
  DtoConverterService underTest;
	@Autowired
	UrlMapUpdateEventPublisher publisher;
  @Autowired
	TestEntityManager em;

  TestUtils testUtils = TestUtils.getInstance();

  @Test
  void entityToCourse() {
    EntityCourse entity = testUtils.getEntityCourse(true);
    Course result = underTest.entityToCourse(entity);
    assertNotNull(result);
    assertEquals(entity.getId(), result.id());
    assertEquals(entity.getLanguage(), result.language());
    assertTrue(entity.getTopics().isEmpty());
  }

  @Test
  void entityToCourseWithTopics(){
    EntityCourse entity = testUtils.getEntityCourse(true);
    entity.setTopics(Stream.generate(() -> testUtils.getEntityTopic(true))
        .limit(5)
        .collect(Collectors.toList()));

    Course result = underTest.entityToCourse(entity);
    assertEquals(5, result.topics().size());
  }

  @Test
  void entityToCourseWithNull(){
    assertNull(underTest.entityToCourse(null));
  }

  @Test
  void entityToTopic() {
    EntityTopic entity = testUtils.getEntityTopic(true);

    Topic result = underTest.entityToTopic(entity);
    assertNotNull(result);
    assertEquals(entity.getId(), result.id());
    assertEquals(entity.getTopic(), result.topic());
    assertEquals(entity.getOrder(), result.order());
    assertEquals(entity.getDescription(), result.description());
    assertTrue(result.lessons().isEmpty());
  }

  @Test
  void entityToTopicWithLessons(){
    EntityTopic entity = testUtils.getEntityTopic(true);
    entity.setLessons(Stream.generate(() -> testUtils.getEntityLesson(true))
        .limit(5)
        .collect(Collectors.toList()));

    Topic result = underTest.entityToTopic(entity);
    assertEquals(5, result.lessons().size());
  }

  @Test
  void entityToLesson() {
    EntityLesson entity = testUtils.getEntityLesson(true);

    Lesson result = underTest.entityToLesson(entity);
    assertEquals(entity.getId(), result.id());
    assertEquals(entity.getLesson(), result.lesson());
    assertEquals(entity.getOrder(), result.order());
    assertTrue(entity.getSlides().isEmpty());
  }

  @Test
  void entityToLessonWithMarkdown() {
    EntityLesson entity = testUtils.getEntityLesson(true);
    entity.setSlides(Stream.generate(() -> testUtils.getEntityMarkdown(true))
        .limit(5)
        .collect(Collectors.toList()));

    Lesson result = underTest.entityToLesson(entity);
    assertEquals(5, result.slides().size());
  }


  @Test
  void entityToMarkdown() {
    EntityMarkdown entity = testUtils.getEntityMarkdown(true);

    Markdown result = underTest.entityToMarkdown(entity);

    assertNotNull(result);
    assertEquals(entity.getId(), result.id());
    assertEquals(entity.getOrder(), result.order());
    assertEquals(entity.getMarkdownContent(), result.markdownContent());
    assertNotNull(entity.getCursorPos());
  }

  @Test
  void entityToMarkdownWithUrls() throws IOException {
    EntityMarkdown entity = em.persist(testUtils.getEntityMarkdown(false));

    String url = "http://localhost:8080/api/resource/test.jpg";

    EntityBinary binary = new EntityBinary();
    binary.setUrl(url);
    binary.setMimeType("image/jpeg");
    binary.setParent(entity);
    binary.setFileName("test.jpg");
		Resource image = new ClassPathResource("test.jpg");
		binary.setBinaryData(image.getContentAsByteArray());
		em.persist(binary);

		publisher.publish(entity.getId(), EventOperationType.UPDATE, binary.getUrl());


		InputMarkdown result = underTest.entityToInputMarkdown(entity);

    em.flush();

    assertNotNull(result);
    assertEquals(1, result.getUrls().size());
    assertEquals(url, result.getUrls().getFirst());
  }
}