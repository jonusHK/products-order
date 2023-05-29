package com.bhkpo.productsorder.core.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.bhkpo.productsorder.core.entity.Product
import com.bhkpo.productsorder.core.repository.condition.ProductCondition
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class ProductRepositoryTest @Autowired constructor(
    private val productRepository: ProductRepository
) : BehaviorSpec({

    given("initProduct") {
        `when`("상품 정보를 초기화 하는 경우") {
            productRepository.initProducts()

            val products: List<Product> = productRepository.searchProducts()

            then("정상적으로 초기화 된다.") {
                products.size shouldBeGreaterThan 0
                products.forEach {
                    it.id shouldNotBe null
                    it.number shouldNotBe null
                    it.price shouldNotBe null
                    it.quantity shouldNotBe null
                }
            }
        }
    }

    given("searchProducts") {
        productRepository.initProducts()
        val products: List<Product> = productRepository.searchProducts()
        val productIds = arrayListOf<Long>(1, 2, 3)
        val targetProducts = products.filter { it.id in productIds }

        `when`("ID 리스트로 상품을 검색하는 경우") {
            val searchedProducts: List<Product> = productRepository.searchProducts(
                condition = ProductCondition(ids = productIds)
            )

            then("정상적으로 조회된다.") {
                searchedProducts.size shouldBe productIds.size
                searchedProducts.map { it.id }.toSet().size shouldBe productIds.size
                searchedProducts.forEach { p ->
                    val product = targetProducts.find { it.id == p.id }
                    product shouldNotBe null
                    p.number shouldBe product!!.number
                    p.price shouldBe product.price
                    p.quantity shouldBe product.quantity
                }
            }
        }

        `when`("number 리스트로 상품을 검색하는 경우") {
            val searchedProducts: List<Product> = productRepository.searchProducts(
                condition = ProductCondition(numbers = targetProducts.map { it.number })
            )

            then("정상적으로 조회된다.") {
                searchedProducts.size shouldBe targetProducts.size
                searchedProducts.map { it.number }.toSet().size shouldBe targetProducts.size
                searchedProducts.forEach { p ->
                    val product = targetProducts.find { it.number == p.number }
                    product shouldNotBe null
                    p.id shouldBe product!!.id
                    p.price shouldBe product.price
                    p.quantity shouldBe product.quantity
                }
            }
        }
    }
})
