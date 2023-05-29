package com.bhkpo.productsorder.core.exception

class InvalidProductQuantityException(
    override val message: String = "수량 값이 올바르지 않습니다."
) : RuntimeException()
