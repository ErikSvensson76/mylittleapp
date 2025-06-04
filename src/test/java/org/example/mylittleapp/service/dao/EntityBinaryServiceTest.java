package org.example.mylittleapp.service.dao;

import org.example.mylittleapp.TestUtils;
import org.example.mylittleapp.model.entity.EntityBinary;
import org.example.mylittleapp.service.UrlMap;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@DirtiesContext
class EntityBinaryServiceTest {

  private static final Logger log = LoggerFactory.getLogger(EntityBinaryServiceTest.class);
  @Autowired
  EntityBinaryService underTest;
  @Autowired TestEntityManager em;
  TestUtils testUtils = TestUtils.getInstance();
  @Autowired
  UrlMap urlMap;



  Resource resource = new ClassPathResource("test.jpg");

  MultipartFile getMultiPartFile() {
    try{
      return new MockMultipartFile(
          "testFile",
          "test.jpg",
          MediaType.IMAGE_JPEG_VALUE,
          resource.getInputStream()
      );
    }catch (IOException e){
      log.error(e.getMessage());
    }
    return null;
  }


  @Test
  void saveSingleFile(){
    MultipartFile file = getMultiPartFile();
    assertNotNull(file);

    String parentId = em.persist(testUtils.getEntityMarkdown(false)).getId();
    String url = "http://localhost:8080/api/resource/test.jpg";

    EntityBinary result = underTest.save(getMultiPartFile(), parentId);
    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals("test.jpg", result.getFileName());
    assertEquals(url, result.getUrl());
    assertNotNull(result.getBinaryData());
    assertNotNull(result.getParent());
    assertTrue(urlMap.getUrls(parentId).get().contains(url));
  }

}