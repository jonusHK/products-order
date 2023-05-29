package com.bhkpo.productsorder.core.dto

class OrderInfoDto(
    val order: OrderDto,
    val products: List<OrderProductDto>
)
