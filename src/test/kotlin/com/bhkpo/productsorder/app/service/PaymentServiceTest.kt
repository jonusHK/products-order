package com.bhkpo.productsorder.app.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import com.bhkpo.productsorder.core.dto.PaymentDto
import com.bhkpo.productsorder.core.entity.Payment
import com.bhkpo.productsorder.core.enum.PaymentStatus
import com.bhkpo.productsorder.core.repository.PaymentRepository
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class PaymentServiceTest : BehaviorSpec({

    afterContainer {
        clearAllMocks()
    }

    val paymentRepository = mockk<PaymentRepository>()

    val paymentService = spyk(
        objToCopy = PaymentServiceImpl(paymentRepository),
        recordPrivateCalls = true
    )

    given("pay") {
        val orderId = 1L

        `when`("배달료 발생하는 주문 건에 대해 결제하는 경우") {
            val payment = Payment(orderId = orderId)

            every { paymentService["createPayment"](orderId) } returns payment
            every { paymentRepository.save(payment) } returns payment

            val price = Payment.DELIVERY_FEE_ACCRUING_UNDER - 1000
            val paymentDto: PaymentDto = paymentService.pay(orderId, price)

            then("정상적으로 결제된다.") {
                paymentDto.orderId shouldBe orderId
                paymentDto.deliveryFee shouldBe Payment.DELIVERY_FEE
                paymentDto.totalFee shouldBe price + Payment.DELIVERY_FEE
                paymentDto.status shouldBe PaymentStatus.COMPLETED
            }
        }

        `when`("배달료 발생하지 않는 주문 건에 대해 결제하는 경우") {
            val payment = Payment(orderId = orderId)

            every { paymentService["createPayment"](orderId) } returns payment
            every { paymentRepository.save(payment) } returns payment

            val price = Payment.DELIVERY_FEE_ACCRUING_UNDER + 1000
            val paymentDto: PaymentDto = paymentService.pay(orderId, price)

            then("정상적으로 결제된다.") {
                paymentDto.orderId shouldBe orderId
                paymentDto.deliveryFee shouldBe 0
                paymentDto.totalFee shouldBe price
                paymentDto.status shouldBe PaymentStatus.COMPLETED
            }
        }
    }
})
