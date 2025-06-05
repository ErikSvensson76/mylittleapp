package org.example.mylittleapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mylittleapp.Application;
import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.input.InputTopic;
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
class TopicControllerTest {

	@Autowired MockMvc mvc;
	@Autowired TestEntityManager em;
	final TestUtils testUtils = TestUtils.getInstance();
	@Autowired ObjectMapper objectMapper;

	@Test
	void getTopics() throws Exception {

		var persistedTopics = Stream.generate(() -> testUtils.getEntityTopic(false))
			.limit(5)
			.filter(Objects::nonNull)
			.map(em::persist)
			.collect(Collectors.toList());

		mvc.perform(get("/topic").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(5)));
	}

	@Test
	void  addTopic() throws Exception{

		var input = testUtils.getInputTopic();

		var json = objectMapper.writeValueAsString(input);

		mvc.perform(post("/topic").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isString())
			.andExpect(jsonPath("$.topic").isString())
			.andExpect(jsonPath("$.order").isNumber())
			.andExpect(jsonPath("$.lessons").isArray());
	}

	@Test
	void findById() throws Exception {
		String id = em.persist(testUtils.getEntityTopic(false)).getId();
		assertNotNull(id);

		mvc.perform(get("/topic/{id}", id).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(id));
	}

	@Test
	void updateTopic() throws Exception{
		var entity = em.persist(testUtils.getEntityTopic(false));
		assertNotNull(entity);

		String topic = "Spring Boot";
		InputTopic input = new InputTopic(
			entity.getId(),
			entity.getOrder(),
			topic,
			entity.getDescription(),
			List.of()
		);

		var jsonInput = objectMapper.writeValueAsString(input);

		mvc.perform(put("/topic/{id}", entity.getId())
			.contentType(MediaType.APPLICATION_JSON).content(jsonInput))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(entity.getId()))
			.andExpect(jsonPath("$.topic").value(topic));
	}


	@Test
	void deleteTopic() throws Exception {

		var toRemove = em.persist(testUtils.getEntityTopic(false));
		assertNotNull(toRemove);

		mvc.perform(delete("/topic/{id}", toRemove.getId()))
			.andExpect(status().isNoContent());
	}

}
