package com.bhkpo.productsorder.core.exception

class OrderTooManyException(
    override val message: String = "주문이 너무 많아 진행이 어렵습니다."
) : RuntimeException()
