package org.example.mylittleapp.model.dto;

import java.util.List;

public record UrlMapUpdateEventPayload(
    String parentId,
    List<String> urls
) {
}
