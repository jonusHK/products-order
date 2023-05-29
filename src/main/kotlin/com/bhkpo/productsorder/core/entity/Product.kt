package com.bhkpo.productsorder.core.entity

import com.bhkpo.productsorder.core.exception.SoldOutException

class Product(
    id: Long? = null,
    val number: Int,
    val name: String,
    val price: Int,
    quantity: Int
) {

    var id: Long? = id
        private set

    var quantity: Int = quantity
        private set

    fun decrease(quantity: Int) {
        // 주문한 상품량이 재고량보다 큰지 확인
        if (this.quantity < quantity) throw SoldOutException()
        // 주문한 상품량만큼 재고량 감소
        this.quantity -= quantity
    }

    val lockKey: String
        get() = "${this::class.simpleName}:${this.number}"
}
