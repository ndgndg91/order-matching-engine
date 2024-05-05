package com.ndgndg91.ordermatchingengine.order.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {
    private List<String> acceptedValue;
    private String message;

    @Override
    public void initialize(ValueOfEnum constraintAnnotation) {
        acceptedValue = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());

        message = constraintAnnotation.enumClass().getSimpleName()
                + " must be any of enum {" + String.join(",", acceptedValue) + "}";
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (acceptedValue.contains(value)) {
            return true;
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
