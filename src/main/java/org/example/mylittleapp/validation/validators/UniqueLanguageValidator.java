package org.example.mylittleapp.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.mylittleapp.model.entity.EntityCourse;
import org.example.mylittleapp.model.input.InputCourse;
import org.example.mylittleapp.service.repos.EntityCourseRepo;
import org.example.mylittleapp.validation.annotations.UniqueLanguage;

import java.util.Optional;

public class UniqueLanguageValidator implements ConstraintValidator<UniqueLanguage, InputCourse> {

  private final EntityCourseRepo entityCourseRepo;

  public UniqueLanguageValidator(EntityCourseRepo entityCourseRepo) {
    this.entityCourseRepo = entityCourseRepo;
  }


  public boolean isValid(InputCourse course, ConstraintValidatorContext context) {
    if(course == null || course.getLanguage() == null || course.getLanguage().isBlank()) return true;

    Optional<EntityCourse> existing = entityCourseRepo.findByLanguage(course.getLanguage());

    boolean isPost = course.getId() == null;

    if(isPost && existing.isPresent()){
      return fail(context, "Language already exists");
    }

    if(!isPost && existing.isPresent() && !existing.get().getId().equals(course.getId())){
      return fail(context, "Course alread exists for a different entity");
    }

    return true;
  }

  private boolean fail(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message)
      .addPropertyNode("language")
      .addConstraintViolation();
    return false;
  }
}
