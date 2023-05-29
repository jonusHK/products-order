package com.bhkpo.productsorder.core.exception

class InvalidProductNumberException(
    override val message: String = "상품번호 값이 올바르지 않습니다."
) : RuntimeException()
