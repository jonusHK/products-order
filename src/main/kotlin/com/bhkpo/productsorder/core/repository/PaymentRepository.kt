package com.bhkpo.productsorder.core.repository

import com.bhkpo.productsorder.core.entity.Payment

interface PaymentRepository {

    fun save(paymentHistory: Payment): Payment

    fun getByOrderId(orderId: Long): Payment?

    fun initPayment()
}
