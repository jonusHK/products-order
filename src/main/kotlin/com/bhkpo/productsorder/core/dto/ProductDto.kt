package com.bhkpo.productsorder.core.dto

data class ProductDto(
    val id: Long? = null,
    val number: Int,
    val name: String,
    val price: Int,
    val quantity: Int
)
