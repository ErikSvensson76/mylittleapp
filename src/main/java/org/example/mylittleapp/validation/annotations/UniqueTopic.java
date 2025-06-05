package org.example.mylittleapp.validation.annotations;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.mylittleapp.validation.validators.UniqueTopicValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTopicValidator.class)
public @interface UniqueTopic {
  String message() default "Invalid topic, this topic is already present";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
