package com.bhkpo.productsorder.core.enum

enum class OrderStatus(val code: String, val label: String) {

    COMPLETED("COMPLETED", "완료"),

    CANCELED("CANCELED", "취소");
}
