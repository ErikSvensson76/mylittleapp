package org.example.mylittleapp.api;

import org.example.mylittleapp.model.dto.Binary;
import org.example.mylittleapp.service.BinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
@Validated
@RequestMapping("/resource")
public class BinaryController {

  private final BinaryService binaryService;
  Logger logger = LoggerFactory.getLogger(BinaryController.class);



	public BinaryController(BinaryService binaryService) {
    this.binaryService = binaryService;
	}

  @PostMapping("/{markdownId}/upload")
  public ResponseEntity<String> addAsset(
    @PathVariable("markdownId") String markdownId,
    @RequestParam("file") MultipartFile file
  ) {
    if (file.isEmpty()) throw new IllegalArgumentException("File was empty");
    if (file.getContentType().isEmpty()) throw new IllegalArgumentException("Content type was empty");
    if (!file.getContentType().startsWith("image")) throw new IllegalArgumentException("Content was not an image");

    return ResponseEntity.ok(
      binaryService.saveBinary(markdownId, file)
    );
  }

  @GetMapping("/{filename:.+}")
  public ResponseEntity<Resource> getBinary(@PathVariable String filename) {
    MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
    Binary binary = binaryService.findByFileName(filename);
    try{
      mediaType = MediaType.valueOf(binary.mimeType());
    }catch (InvalidMediaTypeException e){
      logger.warn("Invalid mime type {}", binary.mimeType());
    }

    try{
      byte[] data = binary.binaryData().getContentAsByteArray();
      ByteArrayResource resource = new ByteArrayResource(data);
      return ResponseEntity.ok()
        .contentLength(data.length)
        .contentType(mediaType)
        .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
        .body(resource);
    }catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}






