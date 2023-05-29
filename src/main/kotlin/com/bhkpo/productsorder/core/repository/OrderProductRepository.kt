package com.bhkpo.productsorder.core.repository

import com.bhkpo.productsorder.core.entity.OrderProduct
import com.bhkpo.productsorder.core.repository.condition.OrderProductCondition

interface OrderProductRepository {

    fun bulkSave(orderProducts: List<OrderProduct>)

    fun searchOrderProducts(condition: OrderProductCondition? = null): List<OrderProduct>

    fun initOrderProducts()
}
