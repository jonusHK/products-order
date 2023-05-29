package com.bhkpo.productsorder.app.service

import com.bhkpo.productsorder.core.dto.PaymentDto

interface PaymentService {

    fun pay(orderId: Long, price: Int): PaymentDto

    fun initPayment()
}
