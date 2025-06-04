package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Binary;
import org.example.mylittleapp.service.converter.DtoConverterService;
import org.example.mylittleapp.service.dao.EntityBinaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class BinaryService {

  private final EntityBinaryService entityBinaryService;
  private final DtoConverterService converterService;


  public BinaryService(
    EntityBinaryService entityBinaryService,
    DtoConverterService converterService)
  {
    this.entityBinaryService = entityBinaryService;
    this.converterService = converterService;
  }

  public Binary findByFileName(String fileName) {
    return converterService.entityToBinary(entityBinaryService.findByFileName(fileName));
  }

  public String saveBinary(String parentId, MultipartFile file) {
    return entityBinaryService.save(file,parentId).getUrl();
  }


  public Binary findById(String id) {
    return converterService.entityToBinary(entityBinaryService.findById(id));
  }


  public List<Binary> findAll() {
    return entityBinaryService.findAll().stream()
        .filter(Objects::nonNull)
        .map(converterService::entityToBinary)
        .toList();
  }

  public boolean delete(String id) {
    return entityBinaryService.delete(id);
  }


  public boolean deleteAll(List<String> ids) {
    return entityBinaryService.deleteAll(ids);
  }

}
