package org.example.mylittleapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mylittleapp.Application;
import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.input.InputMarkdown;
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

import java.util.ArrayList;
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
class MarkdownControllerTest {

	@Autowired MockMvc mvc;
	@Autowired TestEntityManager em;
	final TestUtils testUtils = TestUtils.getInstance();
	@Autowired ObjectMapper objectMapper;


	@Test
	void getMarkdown() throws Exception {
		var entities = Stream.generate(() -> testUtils.getEntityMarkdown(false))
			.limit(5)
			.map(em::persist)
			.collect(Collectors.toList());

		mvc.perform(get("/markdown").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(5)));
	}

	@Test
	void createMarkdown() throws Exception {
		var input = testUtils.getInputMarkdown();
		assertNotNull(input);

		var json = objectMapper.writeValueAsString(input);

		mvc.perform(post("/markdown").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isString())
			.andExpect(jsonPath("$.order").isNumber())
			.andExpect(jsonPath("$.markdownContent").isString())
			.andExpect(jsonPath("$.cursorPos").isNumber())
			.andExpect(jsonPath("$.urls").isArray());

		em.flush();
	}


	@Test
	void getMarkdownById() throws Exception {
		var entityMarkdown = em.persist(testUtils.getEntityMarkdown(false));
		assertNotNull(entityMarkdown);

		mvc.perform(get("/markdown/{id}", entityMarkdown.getId()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(entityMarkdown.getId()));
	}

	@Test
	void updateMarkdown() throws Exception {
		var entityMarkdown = em.persist(testUtils.getEntityMarkdown(false));
		assertNotNull(entityMarkdown);

		String markdownContent = "# Hello World\n## Hello Markdown";

		var input = new InputMarkdown(
			entityMarkdown.getId(),
			entityMarkdown.getOrder(),
			markdownContent,
			entityMarkdown.getCursorPos(),
			new ArrayList<>()
		);

		var jsonInput = objectMapper.writeValueAsString(input);

		mvc.perform(put("/markdown/{id}", entityMarkdown.getId())
			.contentType(MediaType.APPLICATION_JSON).content(jsonInput))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(entityMarkdown.getId()))
			.andExpect(jsonPath("$.markdownContent").value(markdownContent));

	}

	@Test
	void deleteMarkdown() throws Exception {
		var toRemove = em.persist(testUtils.getEntityMarkdown(false));
		assertNotNull(toRemove);

		mvc.perform(delete("/markdown/{id}", toRemove.getId()))
			.andExpect(status().isNoContent());
	}
}