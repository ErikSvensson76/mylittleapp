package org.example.mylittleapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mylittleapp.Application;
import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.input.InputCourse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = Application.class
)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@DirtiesContext
class CourseControllerTest {

  @Autowired
  MockMvc mvc;
  @Autowired
  TestEntityManager em;
  final TestUtils testUtils = TestUtils.getInstance();

  @Autowired
  ObjectMapper mapper;


  @Test
  @DisplayName("When entity is persisted then it should be found by id")
  void test1() throws Exception {
    var entity = em.persist(testUtils.getEntityCourse(false));

    mvc.perform(get("/course/" + entity.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(entity.getId()));
  }

  @Test
  @DisplayName("When valid CourseInput is given then it should return expected result")
  void test2() throws Exception {
    var input = testUtils.getInputCourse();

    var json = mapper.writeValueAsString(input);

    mvc.perform(post("/course").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isString());
    em.flush();
  }

  @Test
  @DisplayName("When entity is persisted, then it can be updated and return updated dto")
  void test3() throws Exception {
    var entity = em.persist(testUtils.getEntityCourse(false));
    assertNotNull(entity);
    String id = entity.getId();

    var input = new InputCourse();

    String language = "Javascript";
    input.setLanguage(language);

    input.setTopics(Stream.generate(testUtils::getInputTopic)
        .limit(5).collect(Collectors.toList())
    );

    var json = mapper.writeValueAsString(input);

    mvc.perform(put("/course/{id}",id).contentType(MediaType.APPLICATION_JSON).content(json)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.language").value(language))
        .andExpect(jsonPath("$.topics", hasSize(5)));
  }

  @Test
  @DisplayName("Given 5 entities are persisted, then they should be found")
  void test4() throws Exception {
    var entities = Stream.generate(() -> testUtils.getEntityCourse(false))
        .limit(5)
        .map(c -> em.persist(c))
        .collect(Collectors.toList());

    mvc.perform(get("/course"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(5)));
  }

  @Test
  @DisplayName("Given 1 persisted entity, when deleted should return noContent")
  void test5() throws Exception {
    var entity = em.persist(testUtils.getEntityCourse(false));

    mvc.perform(delete("/course/{id}",entity.getId()))
        .andExpect(status().isNoContent());
  }

}