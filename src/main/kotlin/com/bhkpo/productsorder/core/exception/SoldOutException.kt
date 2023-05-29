package com.bhkpo.productsorder.core.exception

class SoldOutException(
    override val message: String = "주문한 상품량이 재고량보다 큽니다."
) : RuntimeException()
