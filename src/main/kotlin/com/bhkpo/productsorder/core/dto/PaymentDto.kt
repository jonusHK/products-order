package com.bhkpo.productsorder.core.dto

import com.bhkpo.productsorder.core.enum.PaymentStatus
import java.time.LocalDateTime

data class PaymentDto(
    val orderId: Long,
    val price: Int,
    val deliveryFee: Int,
    val totalFee: Int,
    val status: PaymentStatus? = null,
    val created: LocalDateTime? = null
)
