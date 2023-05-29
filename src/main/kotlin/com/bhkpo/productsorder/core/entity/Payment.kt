package com.bhkpo.productsorder.core.entity

import com.bhkpo.productsorder.core.enum.PaymentStatus
import java.time.LocalDateTime

class Payment(
    val orderId: Long,
    price: Int = 0,
    deliveryFee: Int = 0,
    totalFee: Int = 0,
    status: PaymentStatus? = null,
    created: LocalDateTime? = null
) {

    var price: Int = price
        private set

    var deliveryFee: Int = deliveryFee
        private set

    var totalFee: Int = totalFee
        private set

    var status: PaymentStatus? = status
        private set

    var created: LocalDateTime? = created
        private set

    companion object {
        const val DELIVERY_FEE_ACCRUING_UNDER = 50000
        const val DELIVERY_FEE = 2500
    }

    fun pay(price: Int) {
        // 배송료 발생 조건 여부 확인
        if (price in 1 until DELIVERY_FEE_ACCRUING_UNDER) {
            this.deliveryFee = DELIVERY_FEE
        }
        this.price = price
        this.totalFee += this.price + this.deliveryFee
        this.status = PaymentStatus.COMPLETED
        this.created = LocalDateTime.now()
    }
}
