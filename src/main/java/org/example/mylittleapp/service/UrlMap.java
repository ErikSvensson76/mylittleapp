package org.example.mylittleapp.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.mylittleapp.model.event.UrlMapUpdateEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Validated
public class UrlMap implements ApplicationListener<UrlMapUpdateEvent> {

  private ConcurrentMap<String, List<String>> urlMap;


  public Supplier<List<String>> getUrls(final String markdownId){
    return () -> urlMap.getOrDefault(markdownId, new ArrayList<>());
  }

  public void init(
      @Valid @NotNull(message = "Error: parentIdListMap was null") final Map<String, List<String>> parentIdUrlListMap
  ){
    this.urlMap = Stream.of(parentIdUrlListMap.entrySet())
        .flatMap(Collection::stream)
        .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue ));
  }

  private void remove(String id, List<String> urls) {
    if (urls == null) return;

    urlMap.compute(id, (key, existing) -> {
      if (existing == null) return null;

      List<String> updated = existing.stream()
        .filter(url -> !urls.contains(url))
        .collect(Collectors.toList());

      return updated.isEmpty() ? null : updated;
    });
  }



  @Override
  public void onApplicationEvent(UrlMapUpdateEvent event) {
    switch (event.getType()){
			case UPDATE -> urlMap.merge(event.getParentId(), new ArrayList<>(event.getUrls()), (existing, incoming) -> {
				existing.addAll(incoming);
				return existing;
			});
			case REMOVE -> remove(event.getParentId(), event.getUrls());
    }
  }

}
