package org.example.mylittleapp.model.event;

import org.example.mylittleapp.service.event.EventOperationType;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UrlMapUpdateEvent extends ApplicationEvent {

  private final String parentId;
  private final List<String> urls;
  private final EventOperationType type;

  public UrlMapUpdateEvent(Object source, String parentId, EventOperationType type, String...urls) {
    super(source);
    this.parentId = parentId;
    this.urls = urls == null || urls.length == 0 ? new ArrayList<>() : Stream.of(urls)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    this.type = type;
  }

  public String getParentId() {
    return parentId;
  }

  public List<String> getUrls() {
    return urls;
  }

  public EventOperationType getType() {
    return type;
  }
}
