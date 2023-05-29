package com.bhkpo.productsorder.core.mapper

import com.bhkpo.productsorder.core.dto.PaymentDto
import com.bhkpo.productsorder.core.entity.Payment

object PaymentMapper {

    fun toDto(entity: Payment): PaymentDto {
        return PaymentDto(
            orderId = entity.orderId,
            price = entity.price,
            deliveryFee = entity.deliveryFee,
            totalFee = entity.totalFee,
            status = entity.status,
            created = entity.created
        )
    }

    fun toEntity(dto: PaymentDto): Payment {
        return Payment(
            orderId = dto.orderId,
            price = dto.price,
            deliveryFee = dto.deliveryFee,
            totalFee = dto.totalFee,
            status = dto.status,
            created = dto.created
        )
    }
}
