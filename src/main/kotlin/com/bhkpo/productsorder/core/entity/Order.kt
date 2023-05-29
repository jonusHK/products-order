package com.bhkpo.productsorder.core.entity

import com.bhkpo.productsorder.core.enum.OrderStatus
import java.time.LocalDateTime

class Order(
    id: Long? = null,
    val totalPrice: Int,
    status: OrderStatus? = null,
    created: LocalDateTime? = null
) {
    var id: Long? = id
        private set

    var status: OrderStatus? = status
        private set

    var created: LocalDateTime? = created
        private set

    fun create(id: Long) {
        this.id = id
        this.status = OrderStatus.COMPLETED
        this.created = LocalDateTime.now()
    }
}
