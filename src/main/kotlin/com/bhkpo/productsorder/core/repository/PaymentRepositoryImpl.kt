package com.bhkpo.productsorder.core.repository

import com.bhkpo.productsorder.core.entity.Payment
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl : PaymentRepository {

    private val payments = arrayListOf<Payment>()

    /**
     * 결제 생성
     */
    override fun save(paymentHistory: Payment): Payment {
        payments.add(paymentHistory)

        return paymentHistory
    }

    /**
     * 특정 ID 에 해당하는 결제 조회
     */
    override fun getByOrderId(orderId: Long): Payment? {
        return payments.find { it.orderId == orderId }
    }

    /**
     * 결제 정보 초기화
     */
    override fun initPayment() {
        payments.clear()
    }
}
