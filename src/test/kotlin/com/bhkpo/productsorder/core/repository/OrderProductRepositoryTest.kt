package com.bhkpo.productsorder.core.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.bhkpo.productsorder.core.entity.OrderProduct
import com.bhkpo.productsorder.core.repository.condition.OrderProductCondition
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class OrderProductRepositoryTest @Autowired constructor(
    private val orderProductRepository: OrderProductRepository
) : BehaviorSpec({

    given("bulkSave") {
        orderProductRepository.initOrderProducts()

        `when`("주문 상품 매핑 데이터를 한꺼번에 생성하는 경우") {
            val orderProducts = arrayListOf<OrderProduct>()
            val orderId = 1L
            val productIds = arrayListOf<Long>(1, 2, 3)
            val quantities = arrayListOf(10, 20, 30)
            val prices = arrayListOf(1000, 2000, 3000)

            productIds.forEachIndexed { index, value ->
                orderProducts.add(
                    OrderProduct(
                        orderId = orderId,
                        productId = value,
                        quantity = quantities[index],
                        price = prices[index]
                    )
                )
            }
            orderProductRepository.bulkSave(orderProducts)

            then("정상적으로 생성된다.") {
                val savedOrderProducts = orderProductRepository.searchOrderProducts()

                savedOrderProducts.size shouldBe orderProducts.size
                savedOrderProducts.map { it.productId }.toSet().size shouldBe productIds.size
                savedOrderProducts.forEach { op ->
                    val orderProduct = orderProducts.find { it.productId == op.productId }
                    orderProduct shouldNotBe null
                    op.orderId shouldBe orderId
                    op.price shouldBe orderProduct!!.price
                    op.quantity shouldBe orderProduct.quantity
                }
            }
        }
    }

    given("searchOrderProducts") {
        orderProductRepository.initOrderProducts()

        val orderProducts = arrayListOf<OrderProduct>()
        val orderIds = arrayListOf<Long>(1, 2, 3)
        val productIds = arrayListOf<Long>(1, 2, 3)
        val quantities = arrayListOf(10, 20, 30)
        val prices = arrayListOf(1000, 2000, 3000)

        orderIds.forEachIndexed { index, value ->
            orderProducts.add(
                OrderProduct(
                    orderId = value,
                    productId = productIds[index],
                    quantity = quantities[index],
                    price = prices[index]
                )
            )
        }
        orderProductRepository.bulkSave(orderProducts)

        `when`("특정 ID 에 해당하는 주문 상품 매핑 정보를 검색하는 경우") {
            val orderId = orderIds.first()
            val targetOrderProduct = orderProducts.find { it.orderId == orderId }
            val searchedOrderProducts: List<OrderProduct> = orderProductRepository.searchOrderProducts(
                condition = OrderProductCondition(orderId = orderId)
            )

            then("정상적으로 조회된다.") {
                searchedOrderProducts.size shouldBe 1
                val orderProduct = searchedOrderProducts.first()
                orderProduct.orderId shouldBe orderId
                orderProduct.productId shouldBe targetOrderProduct!!.productId
                orderProduct.price shouldBe targetOrderProduct.price
                orderProduct.quantity shouldBe targetOrderProduct.quantity
            }
        }
        `when`("ID 리스트에 해당하는 주문 상품 매핑 정보를 검색하는 경우") {
            val searchOrderIds = arrayListOf<Long>(1, 2)
            val targetOrderProducts = orderProducts.filter { it.orderId in searchOrderIds }
            val searchedOrderProducts: List<OrderProduct> = orderProductRepository.searchOrderProducts(
                condition = OrderProductCondition(orderIds = searchOrderIds)
            )
            then("정상적으로 조회된다.") {
                searchedOrderProducts.size shouldBe searchOrderIds.size
                searchedOrderProducts.map { it.orderId }.toSet().size shouldBe searchOrderIds.size
                searchedOrderProducts.forEach { op ->
                    val orderProduct = targetOrderProducts.find { it.orderId == op.orderId }
                    orderProduct shouldNotBe null
                    op.productId shouldBe orderProduct!!.productId
                    op.quantity shouldBe orderProduct.quantity
                    op.price shouldBe orderProduct.price
                }
            }
        }
    }

    given("initOrderProducts") {
        orderProductRepository.initOrderProducts()

        val orderProducts = arrayListOf<OrderProduct>()
        val orderId = 1L
        val productIds = arrayListOf<Long>(1, 2, 3)
        val quantities = arrayListOf(10, 20, 30)
        val prices = arrayListOf(1000, 2000, 3000)

        productIds.forEachIndexed { index, value ->
            orderProducts.add(
                OrderProduct(
                    orderId = orderId,
                    productId = value,
                    quantity = quantities[index],
                    price = prices[index]
                )
            )
        }
        orderProductRepository.bulkSave(orderProducts)

        `when`("주문 상품 매핑 정보를 초기화 하는 경우") {
            orderProductRepository.initOrderProducts()

            then("정상적으로 초기화 된다.") {
                val searchedOrderProducts: List<OrderProduct> = orderProductRepository.searchOrderProducts()
                searchedOrderProducts.size shouldBe 0
            }
        }
    }
})
