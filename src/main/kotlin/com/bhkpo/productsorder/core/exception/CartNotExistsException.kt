package com.bhkpo.productsorder.core.exception

class CartNotExistsException(
    override val message: String = "주문을 하려면 상품 입력이 필요합니다."
) : RuntimeException()
