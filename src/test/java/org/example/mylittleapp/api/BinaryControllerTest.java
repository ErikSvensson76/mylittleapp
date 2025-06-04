package org.example.mylittleapp.api;


import org.example.mylittleapp.Application;
import org.example.mylittleapp.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class BinaryControllerTest {

	Logger logger = LoggerFactory.getLogger(BinaryControllerTest.class);
	@Autowired MockMvc mvc;
	@Autowired TestEntityManager em;
	final TestUtils testUtils = TestUtils.getInstance();
	final String fileName = "test.jpg";

	MockMultipartFile getMultiPartFile(){
		byte[] bytes = new byte[]{};
		try{
			Resource resource = new ClassPathResource("test.jpg");
			bytes = resource.getContentAsByteArray();

		}catch (Exception ex){
			logger.error(ex.getMessage());
		}
		if(bytes.length > 0){
			return new MockMultipartFile(
				"file",
				fileName,
				MediaType.IMAGE_JPEG_VALUE,
				bytes
			);
		}
		return null;
	}

	@Test
	void addAsset() throws Exception {
		MockMultipartFile file = getMultiPartFile();
		assertNotNull(file);

		String markdownId = em.persist(testUtils.getEntityMarkdown(false)).getId();
		assertNotNull(markdownId);

		mvc.perform(multipart("/resource/{markdownId}/upload", markdownId)
			.file(file))
			.andExpect(status().isOk())
			.andExpect(content().string("http://localhost:8080/api/resource/test.jpg"));

	}

	@Test
	void getBinary() {
	}
}