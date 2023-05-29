package com.bhkpo.productsorder.app.command

import com.bhkpo.productsorder.app.service.OrderService
import com.bhkpo.productsorder.app.service.PaymentService
import com.bhkpo.productsorder.app.service.ProductService
import com.bhkpo.productsorder.core.dto.CartDto
import com.bhkpo.productsorder.core.dto.OrderInfoDto
import com.bhkpo.productsorder.core.dto.PaymentDto
import com.bhkpo.productsorder.core.dto.ProductDto
import com.bhkpo.productsorder.core.exception.*
import com.bhkpo.productsorder.core.repository.condition.ProductCondition
import com.bhkpo.productsorder.core.utils.FormatConverter
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.system.exitProcess

@Profile("!test")
@Component
class OrderCommand(
    private val context: ConfigurableApplicationContext,
    private val productService: ProductService,
    private val orderService: OrderService,
    private val paymentService: PaymentService
) : CommandLineRunner {

    val orderCommand = arrayListOf("o", "order")
    val quitCommand = arrayListOf("q", "quit")
    val createOrderCommand = arrayListOf(" ")

    override fun run(vararg args: String) {

        // 주문 초기화
        orderService.initOrders()
        // 결제 초기화
        paymentService.initPayment()
        // 상품 초기화
        productService.initProducts()

        while (true) {
            print("\n입력(o[order]: 주문, q[quit]: 종료) : ")

            try {
                val br = BufferedReader(InputStreamReader(System.`in`))
                when (br.readLine()) {
                    in orderCommand -> order(br)
                    in quitCommand -> quit()
                    else -> throw IllegalArgumentException("입력값이 올바르지 않습니다.")
                }
            } catch (e: Exception) {
                println("${e::class.simpleName} 발생. ${e.message}")
            }
        }
    }

    /**
     * 주문 진행
     */
    private fun order(br: BufferedReader) {
        val products: List<ProductDto> = productService.searchProducts()
        val carts = arrayListOf<CartDto>()

        // 상품 목록 출력
        printProducts(products)
        // 상품 담기
        receiveInputProductInfo(br, products, carts)
        // 주문 및 결제
        val orderInfo: OrderInfoDto = createOrder(carts)
        val payment: PaymentDto = pay(orderInfo)
        // 결제 내역 출력
        printOrderResult(orderInfo, payment)
    }

    /**
     * 주문 종료
     */
    private fun quit() {
        println("고객님의 주문 감사합니다.")
        exitProcess(SpringApplication.exit(context))
    }

    /**
     * 상품 목록 출력
     */
    @Suppress("UNCHECKED_CAST")
    private fun printProducts(products: List<ProductDto>) {
        val columns = arrayListOf("상품번호", "상품명", "판매가격", "재고수")
        val members = arrayListOf("number", "name", "price", "quantity")
        val spaces = arrayListOf(10, 55, 15, 10)

        columns.forEachIndexed { index, column ->
            print("$column${" ".repeat(spaces[index] - column.length)}")
            if (index == columns.size - 1) println()
        }

        products.forEach { p ->
            members.forEachIndexed { index, member ->
                val property = p::class.memberProperties.find { it.name == member } as KProperty1<ProductDto, *>
                val value: Any? = property.get(p)
                value?.let {
                    print("$value${" ".repeat(spaces[index] - value.toString().length)}")
                }
                if (index == members.size - 1) println()
            }
        }
    }

    /**
     * 주문 결과 출력
     */
    private fun printOrderResult(orderInfo: OrderInfoDto, payment: PaymentDto) {
        val productIds: List<Long> = orderInfo.products.map { it.productId }
        val products: List<ProductDto> = productService.searchProducts(ProductCondition(ids = productIds))

        println("주문내역: ")
        println("-----------------------------------------------------------------")
        orderInfo.products.forEach { orderProduct ->
            val product = products.find { it.id == orderProduct.productId }
                ?: throw ProductNotExistsException("해당 상품이 존재하지 않습니다. id=${orderProduct.productId}")
            println("${product.name} - ${orderProduct.quantity}개")
        }

        println("-----------------------------------------------------------------")
        println("주문금액: ${FormatConverter.formatNumber(orderInfo.order.totalPrice)}원")
        println("-----------------------------------------------------------------")
        println("지불금액: ${FormatConverter.formatNumber(payment.totalFee)}원")
        println("-----------------------------------------------------------------")
    }

    /**
     * 상품 번호, 수량 입력
     */
    private fun receiveInputProductInfo(br: BufferedReader, products: List<ProductDto>, carts: ArrayList<CartDto>) {
        var isCreateOrder = false

        while (true) {
            var numberLine: String
            var quantityLine: String

            // 상품 번호 입력
            while (true) {
                print("상품번호 : ")
                numberLine = br.readLine()

                // 주문 생성 명령어를 입력 받았는지 확인
                if (numberLine in createOrderCommand) {
                    try {
                        validateBeforeCreateOrder(carts)
                        isCreateOrder = true
                        break
                    } catch (e: CartNotExistsException) {
                        println(e.message)
                        continue
                    }
                }

                // 상품 번호 유효성 검사
                try {
                    validateProductNumber(numberLine, products)
                    break
                } catch (e: InvalidProductNumberException) {
                    println(e.message)
                }
            }
            if (isCreateOrder) break

            // 상품 수량 입력
            while (true) {
                print("수량 : ")
                quantityLine = br.readLine()

                // 주문 생성 명령어를 입력 받았는지 확인
                if (quantityLine in createOrderCommand) {
                    try {
                        validateBeforeCreateOrder(carts)
                        isCreateOrder = true
                        break
                    } catch (e: CartNotExistsException) {
                        println(e.message)
                        continue
                    }
                }

                // 상품 수량 유효성 검사
                try {
                    validateProductQuantity(quantityLine)
                    break
                } catch (e: InvalidProductQuantityException) {
                    println(e.message)
                }
            }
            if (isCreateOrder) break

            // 장바구니 추가
            addCart(carts, numberLine.toInt(), quantityLine.toInt())
        }
    }

    /**
     * 장바구니 추가
     */
    private fun addCart(carts: ArrayList<CartDto>, number: Int, quantity: Int) {
        val cart = carts.find { it.productNumber == number }
        cart?.let {
            it.quantity += quantity
        } ?: run {
            carts.add(
                CartDto(
                    productNumber = number,
                    quantity = quantity
                )
            )
        }
    }

    /**
     * 주문 생성 전 유효성 검사
     */
    private fun validateBeforeCreateOrder(carts: ArrayList<CartDto>) {
        if (carts.isEmpty()) throw CartNotExistsException()
    }

    /**
     * 상품 번호 유효성 검사
     */
    private fun validateProductNumber(number: String, products: List<ProductDto>) {
        products.find { it.number == number.toIntOrNull() } ?: throw InvalidProductNumberException()
    }

    /**
     * 상품 수량 유효성 검사
     */
    private fun validateProductQuantity(quantityStr: String) {
        val quantityInt = quantityStr.toIntOrNull()
        if (quantityInt == null || quantityInt <= 0 ) throw InvalidProductQuantityException()
    }

    /**
     * 주문 생성
     */
    private fun createOrder(carts: ArrayList<CartDto>): OrderInfoDto {
        return orderService.createOrder(carts)
    }

    /**
     * 결제 진행
     */
    private fun pay(orderInfo: OrderInfoDto): PaymentDto {
        return paymentService.pay(
            orderId = orderInfo.order.id!!,
            price = orderInfo.order.totalPrice
        )
    }
}
