package com.bhkpo.productsorder.app.service

import com.bhkpo.productsorder.core.dto.CartDto
import com.bhkpo.productsorder.core.dto.OrderInfoDto
import com.bhkpo.productsorder.core.entity.Order
import com.bhkpo.productsorder.core.entity.OrderProduct
import com.bhkpo.productsorder.core.entity.Product
import com.bhkpo.productsorder.core.exception.LockAcquireException
import com.bhkpo.productsorder.core.exception.OrderNotExistsException
import com.bhkpo.productsorder.core.exception.OrderTooManyException
import com.bhkpo.productsorder.core.exception.ProductNotExistsException
import com.bhkpo.productsorder.core.external.redisson.RedissonTemplate
import com.bhkpo.productsorder.core.mapper.OrderMapper
import com.bhkpo.productsorder.core.mapper.OrderProductMapper
import com.bhkpo.productsorder.core.repository.OrderProductRepository
import com.bhkpo.productsorder.core.repository.OrderRepository
import com.bhkpo.productsorder.core.repository.ProductRepository
import com.bhkpo.productsorder.core.repository.condition.OrderCondition
import com.bhkpo.productsorder.core.repository.condition.OrderProductCondition
import com.bhkpo.productsorder.core.repository.condition.ProductCondition
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val orderProductRepository: OrderProductRepository,
    private val paymentService: PaymentService,
    private val redissonTemplate: RedissonTemplate
) : OrderService {

    /**
     * 주문 생성
     */
    override fun createOrder(carts: List<CartDto>): OrderInfoDto {
        val products: List<Product> = productRepository.searchProducts(
            condition = ProductCondition(numbers = carts.map { it.productNumber })
        )
        var totalPrice = 0
        val orderProducts = arrayListOf<OrderProduct>()

        carts.forEach { cart ->
            val product = products.find { it.number == cart.productNumber }

            // 주문한 수량 만큼 상품 수량 감소
            product?.let {
                try {
                    redissonTemplate.lock(it.lockKey, {
                        it.decrease(cart.quantity)
                    })
                } catch (e: LockAcquireException) {
                    throw OrderTooManyException()
                } catch (e: Exception) {
                    throw e
                }
            } ?: throw ProductNotExistsException("해당 상품은 존재하지 않습니다. number=${cart.productNumber}")

            // 주문 별 상품 정보 추가
            orderProducts.add(
                OrderProduct(
                    productId = product.id!!,
                    quantity = cart.quantity,
                    price = product.price
                )
            )
            totalPrice += product.price * cart.quantity
        }

        // 주문 및 주문 별 상품 정보 저장
        val order: Order = orderRepository.save(Order(totalPrice = totalPrice))
        orderProducts.forEach { it.create(order.id!!) }
        orderProductRepository.bulkSave(orderProducts)

        // 결제 진행
        paymentService.pay(
            orderId = order.id!!,
            price = order.totalPrice
        )

        return OrderInfoDto(
            order = OrderMapper.toDto(order),
            products = orderProducts.map { OrderProductMapper.toDto(it) }
        )
    }

    /**
     * 주문 ID 에 해당하는 주문 건 조회
     */
    override fun getOrderById(orderId: Long): OrderInfoDto {
        val order: Order? = orderRepository.getOrderById(orderId)

        return order?.let { o ->
            val orderProducts: List<OrderProduct> = orderProductRepository.searchOrderProducts(
                condition = OrderProductCondition(orderId = o.id)
            )
            OrderInfoDto(
                order = OrderMapper.toDto(o),
                products = orderProducts.map { OrderProductMapper.toDto(it) }
            )
        } ?: throw OrderNotExistsException()
    }

    /**
     * 특정 조건으로 주문 검색
     */
    override fun searchOrders(condition: OrderCondition?): List<OrderInfoDto> {
        val orders: List<Order> = orderRepository.searchOrders(condition)
        val orderProducts: List<OrderProduct> = orderProductRepository.searchOrderProducts(
            condition = OrderProductCondition(orderIds = orders.map { it.id!! })
        )

        return orders.map { o ->
            val orderProductsByOrderId = orderProducts.filter { it.orderId == o.id }
            OrderInfoDto(
                order = OrderMapper.toDto(o),
                products = orderProductsByOrderId.map { OrderProductMapper.toDto(it) }
            )
        }
    }

    /**
     * 주문 및 주문 별 상품 정보 초기화
     */
    override fun initOrders() {
        orderRepository.initOrders()
        orderProductRepository.initOrderProducts()
    }
}
