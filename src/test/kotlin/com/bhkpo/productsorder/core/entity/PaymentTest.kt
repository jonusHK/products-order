package com.bhkpo.productsorder.core.entity

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.bhkpo.productsorder.core.enum.PaymentStatus

internal class PaymentTest : BehaviorSpec({

    given("pay") {
        val orderId = 1L

        `when`("배송료가 발생하는 금액으로 결제를 진행한 경우") {
            val payment = Payment(orderId = orderId)
            val price = Payment.DELIVERY_FEE_ACCRUING_UNDER - 100

            payment.pay(price)

            then("결제 정보가 정상적으로 저장된다.") {
                payment.orderId shouldBe orderId
                payment.price shouldBe price
                payment.deliveryFee shouldBe Payment.DELIVERY_FEE
                payment.totalFee shouldBe price + payment.deliveryFee
                payment.status shouldBe PaymentStatus.COMPLETED
                payment.created shouldNotBe null
            }
        }

        `when`("배송료가 발생하지 않는 금액으로 결제를 진행한 경우") {
            val payment = Payment(orderId = orderId)
            val price = Payment.DELIVERY_FEE_ACCRUING_UNDER

            payment.pay(price)

            then("결제 정보가 정상적으로 저장된다.") {
                payment.orderId shouldBe orderId
                payment.price shouldBe price
                payment.deliveryFee shouldBe 0
                payment.totalFee shouldBe price
                payment.status shouldBe PaymentStatus.COMPLETED
                payment.created shouldNotBe null
            }
        }
    }
})
