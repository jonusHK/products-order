package com.bhkpo.productsorder.core.mapper

import com.bhkpo.productsorder.core.dto.OrderDto
import com.bhkpo.productsorder.core.entity.Order

object OrderMapper {

    fun toDto(entity: Order): OrderDto {
        return OrderDto(
            id = entity.id,
            totalPrice = entity.totalPrice,
            status = entity.status,
            created = entity.created
        )
    }

    fun toEntity(dto: OrderDto): Order {
        return Order(
            id = dto.id,
            totalPrice = dto.totalPrice,
            status = dto.status,
            created = dto.created
        )
    }
}
