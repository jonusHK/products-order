package com.bhkpo.productsorder.app.service

import com.bhkpo.productsorder.core.dto.PaymentDto
import com.bhkpo.productsorder.core.entity.Payment
import com.bhkpo.productsorder.core.mapper.PaymentMapper
import com.bhkpo.productsorder.core.repository.PaymentRepository
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl(private val paymentRepository: PaymentRepository) : PaymentService {

    /**
     * 결제 진행
     */
    override fun pay(orderId: Long, price: Int): PaymentDto {
        // 결제 객체 생성
        val payment = createPayment(orderId)

        // 결제 상태 업데이트
        payment.pay(price)
        // 결제 정보 저장
        paymentRepository.save(payment)

        return PaymentMapper.toDto(payment)
    }

    /**
     * 결제 정보 초기화
     */
    override fun initPayment() {
        paymentRepository.initPayment()
    }

    private fun createPayment(orderId: Long): Payment {
        return Payment(orderId = orderId)
    }
}
