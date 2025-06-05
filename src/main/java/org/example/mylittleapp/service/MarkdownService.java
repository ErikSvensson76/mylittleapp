package org.example.mylittleapp.service;

import org.example.mylittleapp.model.dto.Markdown;
import org.example.mylittleapp.model.input.InputMarkdown;

import java.util.List;

public interface MarkdownService {

	Markdown findById(String id);

	List<Markdown> findAll();

	InputMarkdown save(InputMarkdown inputMarkdown);

	List<InputMarkdown> saveAll(List<InputMarkdown> inputMarkdowns);

	boolean delete(String id);

	boolean deleteAll(List<String> ids);

}
