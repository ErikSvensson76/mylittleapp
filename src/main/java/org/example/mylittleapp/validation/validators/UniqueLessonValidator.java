package org.example.mylittleapp.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.mylittleapp.model.entity.EntityLesson;
import org.example.mylittleapp.model.input.InputLesson;
import org.example.mylittleapp.service.repos.EntityLessonRepo;
import org.example.mylittleapp.validation.annotations.UniqueLesson;

import java.util.Optional;

public class UniqueLessonValidator implements ConstraintValidator<UniqueLesson, InputLesson> {

  private final EntityLessonRepo entityLessonRepo;

  public UniqueLessonValidator(EntityLessonRepo entityLessonRepo) {
    this.entityLessonRepo = entityLessonRepo;
  }

  @Override
  public boolean isValid(InputLesson lesson, ConstraintValidatorContext context) {
    Optional<EntityLesson> existing = entityLessonRepo.findByLesson(lesson.getLesson());

    boolean isPost = lesson.getId() == null;

    if(isPost && existing.isPresent()){
      return fail(context, "Lesson already exists");
    }

    if(!isPost && existing.isPresent() && !existing.get().getId().equals(lesson.getId())){
      return fail(context, "Lesson already exists for a different entity");
    }

    return true;
  }

  private boolean fail(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message)
      .addPropertyNode("lesson")
      .addConstraintViolation();
    return false;
  }
}
