package com.bhkpo.productsorder.core.mapper

import com.bhkpo.productsorder.core.dto.ProductDto
import com.bhkpo.productsorder.core.entity.Product

object ProductMapper {
    fun toDto(entity: Product): ProductDto {
        return ProductDto(
            id = entity.id,
            number = entity.number,
            name = entity.name,
            price = entity.price,
            quantity = entity.quantity
        )
    }

    fun toEntity(dto: ProductDto): Product {
        return Product(
            id = dto.id,
            number = dto.number,
            name = dto.name,
            price = dto.price,
            quantity = dto.quantity
        )
    }
}
