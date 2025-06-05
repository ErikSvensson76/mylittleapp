package org.example.mylittleapp.model.dto;

import java.io.Serializable;

public record Markdown (
    String id,
    Integer order,
    String markdownContent
) implements Serializable {
}
