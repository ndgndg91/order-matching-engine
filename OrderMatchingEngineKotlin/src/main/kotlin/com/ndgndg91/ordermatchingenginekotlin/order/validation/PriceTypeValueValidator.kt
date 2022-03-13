package com.ndgndg91.ordermatchingenginekotlin.order.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class PriceTypeValueValidator: ConstraintValidator<PriceTypeValue, String> {
    private lateinit var acceptedValues: List<String>
    private lateinit var message: String

    override fun initialize(constraintAnnotation: PriceTypeValue?) {
        this.acceptedValues = constraintAnnotation!!.enumClass.java.enumConstants.asSequence()
            .map { it.name }.toList()
        this.message = constraintAnnotation.enumClass.simpleName + " must any of {" + acceptedValues.joinToString(",") + "}"
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