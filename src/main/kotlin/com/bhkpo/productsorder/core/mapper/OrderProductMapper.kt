package com.bhkpo.productsorder.core.mapper

import com.bhkpo.productsorder.core.dto.OrderProductDto
import com.bhkpo.productsorder.core.entity.OrderProduct

object OrderProductMapper {

    fun toDto(entity: OrderProduct): OrderProductDto {
        return OrderProductDto(
            orderId = entity.orderId,
            productId = entity.productId,
            quantity = entity.quantity,
            price = entity.price
        )
    }

    fun toEntity(dto: OrderProductDto): OrderProduct {
        return OrderProduct(
            orderId = dto.orderId,
            productId = dto.productId,
            quantity = dto.quantity,
            price = dto.price
        )
    }
}
