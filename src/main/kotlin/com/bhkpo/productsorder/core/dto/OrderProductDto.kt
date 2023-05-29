package com.bhkpo.productsorder.core.dto

data class OrderProductDto(
    val orderId: Long? = null,
    val productId: Long,
    val quantity: Int,
    val price: Int
)
