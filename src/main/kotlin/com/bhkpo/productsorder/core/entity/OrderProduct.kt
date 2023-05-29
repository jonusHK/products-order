package com.bhkpo.productsorder.core.entity

class OrderProduct(
    orderId: Long? = null,
    val productId: Long,
    val quantity: Int,
    val price: Int
) {

    var orderId: Long? = orderId
        private set

    fun create(orderId: Long) {
        this.orderId = orderId
    }
}
