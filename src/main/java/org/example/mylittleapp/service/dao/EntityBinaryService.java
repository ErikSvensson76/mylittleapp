package org.example.mylittleapp.service.dao;

import jakarta.validation.constraints.NotNull;
import org.example.mylittleapp.config.BaseUrl;
import org.example.mylittleapp.exception.NoEntityFoundException;
import org.example.mylittleapp.exception.RuntimeIOException;
import org.example.mylittleapp.model.entity.EntityBinary;
import org.example.mylittleapp.model.entity.EntityMarkdown;
import org.example.mylittleapp.service.event.EventOperationType;
import org.example.mylittleapp.service.event.UrlMapUpdateEventPublisher;
import org.example.mylittleapp.service.repos.EntityBinaryRepo;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Validated
public class EntityBinaryService {

  private final EntityBinaryRepo repository;
  private final EntityMarkdownService markdownService;
  private final BaseUrl baseUrl;
  private final UrlMapUpdateEventPublisher publisher;

  public EntityBinaryService(
		EntityBinaryRepo repository,
		EntityMarkdownService markdownService,
		BaseUrl baseUrl, UrlMapUpdateEventPublisher publisher)
  {
    this.repository = repository;
    this.markdownService = markdownService;
		this.baseUrl = baseUrl;
		this.publisher = publisher;
	}


  public EntityBinary findById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoEntityFoundException("No entity of type EntityBinary found with id " + id));
  }


  public List<EntityBinary> findAll() {
    return repository.findAll();
  }


  public EntityBinary internalSave(@NotNull MultipartFile multipartFile) {
    if(multipartFile.isEmpty()) throw new IllegalArgumentException("MultipartFile is empty");
    String contentType = multipartFile.getContentType();
    if(!contentType.startsWith("image")) {
      throw new IllegalArgumentException("Only images supported");
    }

    Resource resource = multipartFile.getResource();
    byte[] bytes;
    EntityBinary binary;

    try(BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream())){
      bytes = inputStream.readAllBytes();
    }catch (Exception e){
      throw new RuntimeIOException(e.getMessage(),e);
    }

    binary = repository.findByFileName(multipartFile.getOriginalFilename())
        .orElse(null);
    if(binary == null){
      binary = new EntityBinary();
    }
    binary.setFileName(multipartFile.getOriginalFilename());
    binary.setMimeType(multipartFile.getContentType());
    binary.setBinaryData(bytes);
    String originalFilename = multipartFile.getOriginalFilename();

    if(!originalFilename.isEmpty()){
      binary.setFileName(originalFilename);
    }else{
      throw new IllegalArgumentException("Filename is null or empty");
    }
    String path = baseUrl.url() + "/api/resource/" + binary.getFileName();
    binary.setUrl(path);

    return repository.save(binary);
  }

  public EntityBinary save(@NotNull MultipartFile multipartFile, @NotNull String parentId){
    EntityBinary binary = internalSave(multipartFile);
    EntityMarkdown markdown = markdownService.findById(parentId);
    binary.setParent(markdown);
    publisher.publish(parentId, EventOperationType.UPDATE, binary.getUrl());
    return repository.save(binary);
  }


  public boolean delete(String id) {
    EntityBinary entityBinary = findById(id);
    String parentId = entityBinary.getParent().getId();
    String url = entityBinary.getUrl();
    entityBinary.setParent(null);

    publisher.publish(parentId, EventOperationType.REMOVE, url);

    repository.delete(entityBinary);
    return repository.findById(id).isEmpty();
  }


  public boolean deleteAll(List<String> ids) {
    if(ids == null) throw new IllegalArgumentException("ids cannot be null");
    return ids.stream()
        .filter(Objects::nonNull)
        .map(this::delete)
        .allMatch(bool -> bool.equals(true));
  }


  public EntityBinary findByFileName(String fileName) {
    return repository.findByFileName(fileName)
        .orElseThrow(() -> new NoEntityFoundException("No entity of type EntityBinary found with fileName: " + fileName));
  }
}
