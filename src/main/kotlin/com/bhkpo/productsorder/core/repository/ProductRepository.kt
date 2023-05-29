package com.bhkpo.productsorder.core.repository

import com.bhkpo.productsorder.core.entity.Product
import com.bhkpo.productsorder.core.repository.condition.ProductCondition

interface ProductRepository {
    fun initProducts()

    fun searchProducts(condition: ProductCondition? = null): List<Product>
}
