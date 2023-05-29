package com.bhkpo.productsorder.core.repository

import com.bhkpo.productsorder.core.entity.Order
import com.bhkpo.productsorder.core.exception.LockAcquireException
import com.bhkpo.productsorder.core.exception.OrderTooManyException
import com.bhkpo.productsorder.core.external.redisson.RedissonTemplate
import com.bhkpo.productsorder.core.repository.condition.OrderCondition
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(private val redissonTemplate: RedissonTemplate) : OrderRepository {

    val lockKey = "orders"
    private val orders = arrayListOf<Order>()

    /**
     * 주문 생성
     */
    override fun save(order: Order): Order {
        try {
            redissonTemplate.lock(lockKey, {
                val id: Long = if (orders.isNotEmpty()) orders.last().id!! + 1 else 1
                order.create(id)
                orders.add(order)
            })
        } catch (e: LockAcquireException) {
            throw OrderTooManyException()
        }

        return order
    }

    /**
     * 특정 ID 에 해당하는 주문 건 조회
     */
    override fun getOrderById(orderId: Long): Order? {
        return orders.find { it.id == orderId }
    }

    /**
     * 특정 조건으로 주문 검색
     */
    override fun searchOrders(condition: OrderCondition?): List<Order> {
        return condition?.let {
            getOrdersByCondition(it)
        } ?: orders
    }

    /**
     * 주문 정보 초기화
     */
    override fun initOrders() {
        orders.clear()
    }

    private fun getOrdersByCondition(condition: OrderCondition): List<Order> {
        var filtered = arrayListOf<Order>()
        filtered.addAll(orders)

        condition.ids?.let { ids ->
            filtered = ArrayList(filtered.filter { it.id in ids })
        }

        return filtered
    }
}
