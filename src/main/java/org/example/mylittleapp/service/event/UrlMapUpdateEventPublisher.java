package org.example.mylittleapp.service.event;

import org.example.mylittleapp.model.event.UrlMapUpdateEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UrlMapUpdateEventPublisher {
  private final ApplicationEventPublisher applicationEventPublisher;

  public UrlMapUpdateEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publish(String parentId, EventOperationType type, String...urls){
    UrlMapUpdateEvent event = new UrlMapUpdateEvent(this, parentId, type, urls);
    applicationEventPublisher.publishEvent(event);
  }
}
