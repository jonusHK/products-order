package com.bhkpo.productsorder.core.entity

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import com.bhkpo.productsorder.core.exception.SoldOutException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


internal class ProductTest : BehaviorSpec({

    given("decrease") {

        val service: ExecutorService = Executors.newFixedThreadPool(100)
        val numberOfThreads = 50
        val latch = CountDownLatch(numberOfThreads)

        val initialQuantity = 1000

        `when`("동시에 감소하는 수량이 상품의 재고량을 초과하지 않는 경우") {
            val product = Product(id = 1, number = 123456, name = "상품A", price = 1000, quantity = initialQuantity)
            val quantity = initialQuantity / numberOfThreads

            repeat(numberOfThreads) {
                service.execute {
                    product.decrease(quantity)
                    latch.countDown()
                }
            }
            latch.await()

            then("상품 수량은 {스레드수 * 수량} 만큼 감소한다.") {
                product.quantity shouldBe initialQuantity - (numberOfThreads * quantity)
            }
        }

        `when`("동시에 감소하는 수량이 상품의 재고량을 초과하는 경우") {
            val product = Product(id = 1, number = 123456, name = "상품A", price = 1000, quantity = initialQuantity)
            val quantity = initialQuantity / numberOfThreads + 1

            var isSoldOut = false

            repeat(numberOfThreads) {
                service.execute {
                    try {
                        product.decrease(quantity)
                    } catch (e: SoldOutException) {
                        isSoldOut = true
                    } finally {
                        latch.countDown()
                    }
                }
            }
            latch.await()

            then("SoldOutException 예외가 발생한다.") {
                isSoldOut shouldBe true
            }
        }
    }
})
