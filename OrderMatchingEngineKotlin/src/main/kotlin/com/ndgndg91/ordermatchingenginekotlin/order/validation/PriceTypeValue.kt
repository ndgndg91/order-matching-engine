package com.ndgndg91.ordermatchingenginekotlin.order.validation

import com.ndgndg91.ordermatchingenginekotlin.order.PriceType
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [PriceTypeValueValidator::class])
annotation class PriceTypeValue(
    val enumClass: KClass<PriceType> = PriceType::class,
    val message: String = "must any of {enumClass}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
