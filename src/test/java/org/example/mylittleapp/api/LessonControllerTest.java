package org.example.mylittleapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mylittleapp.Application;
import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.input.InputLesson;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
class LessonControllerTest {

	@Autowired MockMvc mvc;
	@Autowired TestEntityManager em;
	final TestUtils testUtils = TestUtils.getInstance();
	@Autowired ObjectMapper objectMapper;

	@Test
	void getAll() throws Exception {
		var persistedLessons = Stream.generate(() -> testUtils.getEntityLesson(false))
			.limit(5)
			.filter(Objects::nonNull)
			.map(em::persist)
			.collect(Collectors.toList());

		mvc.perform(get("/lesson").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(5)));
	}

	@Test
	void addLesson() throws Exception {
		var input = testUtils.getInputLesson();

		var json = objectMapper.writeValueAsString(input);

		mvc.perform(post("/lesson").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isString())
			.andExpect(jsonPath("$.lesson").value(input.getLesson()))
			.andExpect(jsonPath("$.order").value(input.getOrder()))
			.andExpect(jsonPath("$.slides").isArray());
	}

	@Test
	void findById() throws Exception {

		String id = em.persist(testUtils.getEntityLesson(false)).getId();
		assertNotNull(id);

		mvc.perform(get("/lesson/" + id).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(id));
	}

	@Test
	void findByIdNotFound() throws Exception{
		mvc.perform(get("/lesson/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isString())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.error").isString())
			.andExpect(jsonPath("$.path").isString());
	}

	@Test
	void updateLesson() throws Exception{
		var entity = em.persist(testUtils.getEntityLesson(false));
		assertNotNull(entity);

		String lesson = "Foo";
		InputLesson input = new InputLesson(
			entity.getId(),
			lesson,
			entity.getOrder(),
			List.of()
		);

		var jsonInput = objectMapper.writeValueAsString(input);

		mvc.perform(put("/lesson/{id}", entity.getId())
			.contentType(MediaType.APPLICATION_JSON).content(jsonInput))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(entity.getId()))
			.andExpect(jsonPath("$.lesson").value(lesson));
	}

	@Test
	void deleteLesson() throws Exception {
		var toRemove = em.persist(testUtils.getEntityLesson(false));
		assertNotNull(toRemove);

		mvc.perform(delete("/lesson/{id}", toRemove.getId()))
			.andExpect(status().isNoContent());
	}


}