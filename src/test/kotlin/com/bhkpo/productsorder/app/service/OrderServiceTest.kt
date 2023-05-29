package com.bhkpo.productsorder.app.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.bhkpo.productsorder.core.dto.CartDto
import com.bhkpo.productsorder.core.dto.OrderInfoDto
import com.bhkpo.productsorder.core.dto.ProductDto
import com.bhkpo.productsorder.core.enum.OrderStatus
import com.bhkpo.productsorder.core.exception.SoldOutException
import com.bhkpo.productsorder.core.repository.condition.OrderCondition
import com.bhkpo.productsorder.core.repository.condition.ProductCondition
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newFixedThreadPool

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class OrderServiceTest @Autowired constructor(
    private val orderService: OrderService,
    private val productService: ProductService
) : BehaviorSpec({

    given("createOrder") {
        orderService.initOrders()
        productService.initProducts()

        val service: ExecutorService = newFixedThreadPool(100)

        `when`("재고량보다 적게 주문을 진행한 경우") {
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)

            val productIds = arrayListOf<Long>(1, 2, 3)
            val products: List<ProductDto> = productService.searchProducts(
                condition = ProductCondition(ids = productIds)
            )
            val carts = arrayListOf<CartDto>()
            val quantity = 3

            products.forEach {
                carts.add(
                    CartDto(productNumber = it.number, quantity = quantity)
                )
            }

            repeat(numberOfThreads) {
                service.execute {
                    orderService.createOrder(carts)
                    latch.countDown()
                }
            }
            latch.await()

            val ordersInfo: List<OrderInfoDto> = orderService.searchOrders()
            val productsAfterOrder: List<ProductDto> = productService.searchProducts(
                condition = ProductCondition(ids = productIds)
            )

            then("각 상품은 스레드 수 * 주문 수량 만큼 차감된다.") {
                productsAfterOrder.forEach { productAfterOrder ->
                    val product = products.find { it.id == productAfterOrder.id }
                    productAfterOrder.quantity shouldBe product!!.quantity.minus(numberOfThreads * quantity)
                }
            }

            then("주문이 중복 없이 생성된다.") {
                ordersInfo.size shouldBe numberOfThreads
                ordersInfo.size shouldBe ordersInfo.map { it.order.id }.toSet().size
            }

            then("주문 총 금액이 정상적으로 저장된다.") {
                var totalPrice = 0
                carts.forEach { cart ->
                    val product = products.find { it.number == cart.productNumber }
                    totalPrice += product!!.price * cart.quantity
                }
                ordersInfo.forEach {
                    it.order.totalPrice shouldBe totalPrice
                    it.order.status shouldBe OrderStatus.COMPLETED
                }
            }

            then("주문 상품이 정상적으로 저장된다.") {
                ordersInfo.forEach { orderInfo ->
                    orderInfo.products.size shouldBe products.size
                    orderInfo.products.forEach { orderProduct ->
                        val product = products.find { it.id == orderProduct.productId }
                        product shouldNotBe null

                        val cart = carts.find { it.productNumber == product!!.number }
                        cart shouldNotBe null

                        orderProduct.orderId shouldBe orderInfo.order.id
                        orderProduct.quantity shouldBe cart!!.quantity
                        orderProduct.price shouldBe product!!.price
                    }
                }
            }
        }

        `when`("재고량보다 많은 주문을 진행한 경우") {
            val productIds = arrayListOf<Long>(1)
            val product: ProductDto = productService.searchProducts(
                condition = ProductCondition(ids = productIds)
            ).first()

            val numberOfThreads = product.quantity + 1
            val latch = CountDownLatch(numberOfThreads)

            val carts = arrayListOf<CartDto>()
            val quantity = 1

            carts.add(
                CartDto(productNumber = product.number, quantity = quantity)
            )

            var isSoldOut = false

            repeat(numberOfThreads) {
                service.execute {
                    try {
                        orderService.createOrder(carts)
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

            then("재고량은 0이 된다.") {
                val productAfterOrder: ProductDto = productService.searchProducts(
                    condition = ProductCondition(ids = productIds)
                ).first()
                productAfterOrder.quantity shouldBe 0
            }
        }
    }

    given("getOrder") {
        orderService.initOrders()
        productService.initProducts()

        val productIds = arrayListOf<Long>(1, 2, 3)
        val products: List<ProductDto> = productService.searchProducts(
            condition = ProductCondition(ids = productIds)
        )

        val carts = arrayListOf<CartDto>()
        val quantity = 5

        products.forEach {
            carts.add(
                CartDto(productNumber = it.number, quantity = quantity)
            )
        }
        orderService.createOrder(carts)

        `when`("특정 ID 에 대한 주문 정보를 가져오는 경우") {
            val orderInfo = orderService.getOrderById(1)

            then("주문과 주문 상품 정보를 확인할 수 있다.") {
                orderInfo shouldNotBe null
                orderInfo.order.id shouldBe 1

                var totalPrice = 0
                products.forEach { totalPrice += it.price * quantity }

                orderInfo.order.totalPrice shouldBe totalPrice
                orderInfo.order.created shouldNotBe null

                orderInfo.products.size shouldBe products.size
                orderInfo.products.forEach { orderProduct ->
                    val product = products.find { it.id == orderProduct.productId }
                    product shouldNotBe null
                    orderProduct.price shouldBe product!!.price
                    orderProduct.orderId shouldBe orderInfo.order.id
                    orderProduct.quantity shouldBe quantity
                }
            }
        }
    }

    given("searchOrders") {
        orderService.initOrders()
        productService.initProducts()

        val productIds = arrayListOf<Long>(1)
        val product: ProductDto = productService.searchProducts(
            condition = ProductCondition(ids = productIds)
        ).first()

        val carts = arrayListOf<CartDto>()
        val quantity = 5

        carts.add(
            CartDto(productNumber = product.number, quantity = quantity)
        )
        repeat(5) { orderService.createOrder(carts) }

        `when`("ID 리스트에 해당하는 주문을 검색하는 경우") {
            val ids = arrayListOf<Long>(1, 2, 3)
            val ordersInfo: List<OrderInfoDto> = orderService.searchOrders(
                condition = OrderCondition(ids = ids)
            )
            then("해당 주문 건들이 조회된다.") {
                ordersInfo.size shouldBe ids.size
                ordersInfo.forEach {
                    ids shouldContain it.order.id
                    it.products.size shouldBe productIds.size
                }
            }
        }
    }
})
