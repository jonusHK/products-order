package com.bhkpo.productsorder.core.repository

import com.bhkpo.productsorder.core.entity.Order
import com.bhkpo.productsorder.core.repository.condition.OrderCondition

interface OrderRepository {

    fun save(order: Order): Order

    fun getOrderById(orderId: Long): Order?

    fun searchOrders(condition: OrderCondition? = null): List<Order>

    fun initOrders()
}
