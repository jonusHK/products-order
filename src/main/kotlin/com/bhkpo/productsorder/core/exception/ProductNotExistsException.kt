package com.bhkpo.productsorder.core.exception

class ProductNotExistsException(
    override val message: String = "상품 정보가 존재하지 않습니다."
) : RuntimeException()
