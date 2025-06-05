package org.example.mylittleapp.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.mylittleapp.model.entity.EntityTopic;
import org.example.mylittleapp.model.input.InputTopic;
import org.example.mylittleapp.service.repos.EntityTopicRepo;
import org.example.mylittleapp.validation.annotations.UniqueTopic;

import java.util.Optional;

public class UniqueTopicValidator implements ConstraintValidator<UniqueTopic, InputTopic> {

  private final EntityTopicRepo entityTopicRepo;

  public UniqueTopicValidator(EntityTopicRepo entityTopicRepo) {
    this.entityTopicRepo = entityTopicRepo;
  }

  @Override
  public boolean isValid(InputTopic topic, ConstraintValidatorContext context) {
    if(topic == null || topic.getTopic() == null || topic.getTopic().isBlank()) return true;

    Optional<EntityTopic> existing = entityTopicRepo.findByTopic(topic.getTopic());

    boolean isPost = topic.getId() == null;

    if(isPost && existing.isPresent()){
      return fail(context, "Topic already exists");
    }

    if(!isPost && existing.isPresent() && !existing.get().getId().equals(topic.getId())){
      return fail(context, "Topic already exists for a different entity");
    }

    return true;
  }

  private boolean fail(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message)
      .addPropertyNode("topic")
      .addConstraintViolation();
    return false;
  }
}
