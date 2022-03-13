package com.ndgndg91.ordermatchingenginekotlin.order.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class OrderTypeValueValidator: ConstraintValidator<OrderTypeValue, String> {
    private lateinit var acceptedValues: List<String>
    private lateinit var message: String

    override fun initialize(constraintAnnotation: OrderTypeValue?) {
        acceptedValues = constraintAnnotation!!.enumClass.java.enumConstants.asSequence()
            .map { it.name }.toList()
        message = constraintAnnotation.enumClass.simpleName + " must be any of enum {" + acceptedValues.joinToString(",") + "}"
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (acceptedValues.contains(value)) {
            return true
        }

        context!!.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation()
        return false
    }
}