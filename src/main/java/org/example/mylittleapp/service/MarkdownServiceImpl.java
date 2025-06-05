package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Markdown;
import org.example.mylittleapp.model.input.InputMarkdown;
import org.example.mylittleapp.service.converter.DtoConverterService;
import org.example.mylittleapp.service.dao.EntityMarkdownService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MarkdownServiceImpl implements MarkdownService {

  private final EntityMarkdownService markdownService;
  private final DtoConverterService converterService;

  public MarkdownServiceImpl(EntityMarkdownService markdownService, DtoConverterService converterService) {
    this.markdownService = markdownService;
    this.converterService = converterService;
  }

  @Override
  public Markdown findById(String id) {
    return converterService.entityToMarkdown(
        markdownService.findById(id)
    );
  }

  @Override
  public List<Markdown> findAll() {
    return markdownService.findAll().stream()
      .filter(Objects::nonNull)
      .map(converterService::entityToMarkdown)
      .toList();
  }

  @Override
  public InputMarkdown save(InputMarkdown inputMarkdown) {
    return converterService.entityToInputMarkdown(markdownService.save(inputMarkdown));
  }

  @Override
  public List<InputMarkdown> saveAll(List<InputMarkdown> inputMarkdowns) {
    return markdownService.saveAll(inputMarkdowns).stream()
        .filter(Objects::nonNull)
        .map(converterService::entityToInputMarkdown)
        .sorted(Comparator.comparing(InputMarkdown::getOrder))
        .toList();
  }

  @Override
  public boolean delete(String id) {
    return markdownService.delete(id);
  }

  @Override
  public boolean deleteAll(List<String> ids) {
    return markdownService.deleteAll(ids);
  }
}
