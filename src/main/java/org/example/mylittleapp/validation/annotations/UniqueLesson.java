package org.example.mylittleapp.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.mylittleapp.validation.validators.UniqueLessonValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueLessonValidator.class)
public @interface UniqueLesson {
  String message() default "Invalid lesson, this lesson is already present";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
