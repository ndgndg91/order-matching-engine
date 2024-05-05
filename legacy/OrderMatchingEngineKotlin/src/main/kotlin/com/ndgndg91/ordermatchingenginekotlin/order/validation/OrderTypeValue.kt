package com.ndgndg91.ordermatchingenginekotlin.order.validation

import com.ndgndg91.ordermatchingenginekotlin.order.OrderType
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [OrderTypeValueValidator::class])
annotation class OrderTypeValue(
    val enumClass: KClass<OrderType> = OrderType::class,
    val message: String = "must be any of enum {enumClass}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
