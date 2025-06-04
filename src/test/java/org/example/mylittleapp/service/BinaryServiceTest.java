package org.example.mylittleapp.service;

import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.dto.Binary;
import org.example.mylittleapp.model.entity.EntityBinary;
import org.example.mylittleapp.model.entity.EntityMarkdown;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@DirtiesContext
class BinaryServiceTest {

	@Autowired
	BinaryService underTest;
	@Autowired UrlMap urlMap;
	@Autowired
	TestEntityManager em;

	final TestUtils testUtils = TestUtils.getInstance();
	final String fileName = "test.jpg";
	final String expectedUrl = "http://localhost:8080/api/resource/test.jpg";

	Logger logger = LoggerFactory.getLogger(BinaryServiceTest.class);

	Resource loadFile(){
		Resource resource;
		Optional<Resource> optionalResource = Optional.empty();
		try{
			optionalResource = Optional.of(new ClassPathResource(fileName));
		}catch (Exception ex){
			logger.error(ex.getMessage());
		}
		return optionalResource.orElseThrow();
	}

	@Test
	void findByFileName() throws IOException {
		EntityMarkdown entityMarkdown = em.persist(testUtils.getEntityMarkdown(false));
		assertNotNull(entityMarkdown);


		MultipartFile multipartFile = new MockMultipartFile(
			fileName,
			fileName,
			MediaType.IMAGE_JPEG_VALUE,
			loadFile().getContentAsByteArray()
		);
		assertNotNull(multipartFile);
		String url = expectedUrl;
		EntityBinary entityBinary = new EntityBinary();
		entityBinary.setFileName(fileName);
		entityBinary.setBinaryData(multipartFile.getBytes());

		entityBinary.setUrl(url);
		entityBinary.setMimeType(multipartFile.getContentType());
		entityBinary.setParent(entityMarkdown);
		EntityBinary persisted = em.persist(entityBinary);

		assertNotNull(persisted);

		Binary binary = underTest.findByFileName(fileName);
		assertNotNull(binary);
		assertEquals("test.jpg", binary.fileName());
		assertEquals(url, binary.url());
		assertNotNull(binary.id());
		assertEquals(multipartFile.getContentType(), binary.mimeType());
	}

	@Test
	void saveBinary() throws IOException {
		String parentId = em.persist(testUtils.getEntityMarkdown(false)).getId();

		MockMultipartFile multipartFile = new MockMultipartFile(
			fileName,
			fileName,
			MediaType.IMAGE_JPEG_VALUE,
			loadFile().getContentAsByteArray()
		);

		String result = underTest.saveBinary(parentId, multipartFile);

		assertNotNull(result);
		assertEquals(expectedUrl, result);
		assertTrue(urlMap.getUrls(parentId).get().contains(expectedUrl), "Expected url was not found");
	}
}