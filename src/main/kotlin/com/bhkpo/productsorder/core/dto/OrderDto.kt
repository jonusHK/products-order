package com.bhkpo.productsorder.core.dto

import com.bhkpo.productsorder.core.enum.OrderStatus
import java.time.LocalDateTime

data class OrderDto(
    val id: Long? = null,
    var totalPrice: Int,
    val status: OrderStatus? = null,
    val created: LocalDateTime? = null
)
