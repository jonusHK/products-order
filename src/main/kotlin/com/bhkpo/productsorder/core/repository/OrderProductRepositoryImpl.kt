package com.bhkpo.productsorder.core.repository

import com.bhkpo.productsorder.core.entity.OrderProduct
import com.bhkpo.productsorder.core.repository.condition.OrderProductCondition
import org.springframework.stereotype.Repository

@Repository
class OrderProductRepositoryImpl : OrderProductRepository {

    private val orderProducts = arrayListOf<OrderProduct>()

    /**
     * 주문 별 상품 정보 대량 생성
     */
    override fun bulkSave(orderProducts: List<OrderProduct>) {
        this.orderProducts.addAll(orderProducts)
    }

    /**
     * 특정 조건으로 주문 별 상품 정보 검색
     */
    override fun searchOrderProducts(condition: OrderProductCondition?): List<OrderProduct> {
        return condition?.let {
            getOrderProductsByCondition(it)
        } ?: orderProducts
    }

    /**
     * 주문 별 상품 정보 초기화
     */
    override fun initOrderProducts() {
        orderProducts.clear()
    }

    private fun getOrderProductsByCondition(condition: OrderProductCondition): List<OrderProduct> {
        var filtered = arrayListOf<OrderProduct>()
        filtered.addAll(orderProducts)

        condition.orderId?.let { orderId ->
            filtered = ArrayList(filtered.filter { it.orderId == orderId })
        }
        condition.orderIds?.let { ids ->
            filtered = ArrayList(filtered.filter { it.orderId in ids })
        }

        return filtered
    }
}
