package com.bhkpo.productsorder.core.exception

class OrderNotExistsException(
    override val message: String = "주문 정보가 존재하지 않습니다."
) : RuntimeException()
