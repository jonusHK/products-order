package com.bhkpo.productsorder.core.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.bhkpo.productsorder.core.entity.Order
import com.bhkpo.productsorder.core.repository.condition.OrderCondition
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class OrderRepositoryTest @Autowired constructor(
    private val orderRepository: OrderRepository
) : BehaviorSpec({

    given("save") {
        orderRepository.initOrders()

        val service: ExecutorService = Executors.newFixedThreadPool(100)
        val numberOfThreads = 50
        val latch = CountDownLatch(numberOfThreads)

        `when`("주문이 한꺼번에 생성되는 경우") {
            val totalPrice = 1000
            repeat(numberOfThreads) {
                service.execute {
                    orderRepository.save(Order(totalPrice = totalPrice))
                    latch.countDown()
                }
            }
            latch.await()

            then("중복되는 ID 없이 주문이 생성된다.") {
                val orders: List<Order> = orderRepository.searchOrders()
                println("order size - ${orders.size}")
                orders.forEach {
                    println(it.id)
                }
                orders.map { it.id }.toSet().size shouldBe numberOfThreads
                orders.forEach {
                    it.id shouldNotBe null
                    it.totalPrice shouldBe totalPrice
                    it.created shouldNotBe null
                }
            }
        }
    }

    given("getOrderById") {
        orderRepository.initOrders()

        val totalPrices = arrayListOf(1000, 2000, 3000)
        val orders = arrayListOf<Order>()

        totalPrices.forEach {
            orders.add(orderRepository.save(Order(totalPrice = it)))
        }
        val order = orders.first()

        `when`("특정 ID 에 해당하는 주문을 조회하는 경우") {
            val searchedOrder: Order? = orderRepository.getOrderById(order.id!!)

            then("정상적으로 조회된다.") {
                searchedOrder shouldNotBe null
                searchedOrder!!.id shouldBe order.id
                searchedOrder.totalPrice shouldBe order.totalPrice
                searchedOrder.created shouldNotBe null
            }
        }
    }

    given("searchOrders") {
        orderRepository.initOrders()

        val totalPrices = arrayListOf(1000, 2000, 3000)
        val orders = arrayListOf<Order>()

        totalPrices.forEach {
            orders.add(orderRepository.save(Order(totalPrice = it)))
        }

        `when`("ID 리스트에 해당하는 주문 정보를 검색하는 경우") {
            val orderIds = arrayListOf<Long>(1, 2)
            val targetOrders = orders.filter { it.id in orderIds }
            val searchedOrders: List<Order> = orderRepository.searchOrders(
                condition = OrderCondition(ids = orderIds)
            )

            then("정상적으로 조회된다.") {
                searchedOrders.size shouldBe orderIds.size
                searchedOrders.map { it.id }.toSet().size shouldBe orderIds.size
                searchedOrders.forEach {o ->
                    val order = targetOrders.find { it.id == o.id }
                    order shouldNotBe null
                    o.totalPrice shouldBe order!!.totalPrice
                    o.created shouldBe order.created
                }
            }
        }
    }

    given("initOrder") {
        orderRepository.initOrders()

        val totalPrices = arrayListOf(1000, 2000, 3000)
        val orders = arrayListOf<Order>()

        totalPrices.forEach {
            orders.add(orderRepository.save(Order(totalPrice = it)))
        }

        `when`("주문 정보를 초기화 하는 경우") {
            orderRepository.initOrders()
            val searchedOrders: List<Order> = orderRepository.searchOrders()

            then("정상적으로 초기화 된다.") {
                searchedOrders.size shouldBe 0
            }
        }
    }
})
