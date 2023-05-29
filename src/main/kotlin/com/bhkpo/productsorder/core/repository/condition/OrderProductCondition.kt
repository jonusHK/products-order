package com.bhkpo.productsorder.core.repository.condition

data class OrderProductCondition(
    val orderId: Long? = null,
    val orderIds: List<Long>? = null
)
