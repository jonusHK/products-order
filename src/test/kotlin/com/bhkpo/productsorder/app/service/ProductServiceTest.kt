package com.bhkpo.productsorder.app.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.bhkpo.productsorder.core.dto.ProductDto
import com.bhkpo.productsorder.core.repository.condition.ProductCondition
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class ProductServiceTest @Autowired constructor(
    private val orderService: OrderService,
    private val productService: ProductService
): BehaviorSpec({

    given("searchOrders") {
        orderService.initOrders()
        productService.initProducts()

        val products: List<ProductDto> = productService.searchProducts()
        val productIds = arrayListOf<Long>(1, 2, 3)
        val targetProducts = products.filter { it.id in productIds }

        `when`("ID 리스트에 해당하는 주문을 검색하는 경우") {
            val productsByIds: List<ProductDto> = productService.searchProducts(
                condition = ProductCondition(ids = productIds)
            )

            then("해당 주문 건들이 정상적으로 조회된다.") {
                productsByIds.map { it.id }.toSet().size shouldBe productIds.size
                productsByIds.forEach { p ->
                    val product = targetProducts.find { it.id == p.id }
                    product shouldNotBe null
                    p.number shouldBe product!!.number
                    p.name shouldBe product.name
                    p.quantity shouldBe product.quantity
                    p.price shouldBe product.price
                }
            }
        }

        `when`("number 리스트에 해당하는 주문을 검색하는 경우") {
            val productsByNumbers = productService.searchProducts(
                condition = ProductCondition(numbers = targetProducts.map { it.number })
            )

            then("해당 주문 건들이 정상적으로 조회된다.") {
                productsByNumbers.size shouldBe targetProducts.size
                productsByNumbers.forEach { p ->
                    val product = targetProducts.find { it.number == p.number }
                    product shouldNotBe null
                    p.number shouldBe product!!.number
                    p.name shouldBe product.name
                    p.quantity shouldBe product.quantity
                    p.price shouldBe product.price
                }
            }
        }
    }
})
