package com.bhkpo.productsorder.app.service

import com.bhkpo.productsorder.core.dto.ProductDto
import com.bhkpo.productsorder.core.repository.condition.ProductCondition

interface ProductService {
    fun searchProducts(condition: ProductCondition? = null): List<ProductDto>

    fun initProducts()
}
