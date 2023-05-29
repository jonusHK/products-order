package com.bhkpo.productsorder.app.service

import com.bhkpo.productsorder.core.dto.CartDto
import com.bhkpo.productsorder.core.dto.OrderInfoDto
import com.bhkpo.productsorder.core.repository.condition.OrderCondition

interface OrderService {

    fun createOrder(carts: List<CartDto>): OrderInfoDto

    fun getOrderById(orderId: Long): OrderInfoDto

    fun searchOrders(condition: OrderCondition? = null): List<OrderInfoDto>

    fun initOrders()
}
