package com.bhkpo.productsorder.core.enum

enum class PaymentStatus(val code: String, val label: String) {

    COMPLETED("COMPLETED", "완료"),

    FAILED("FAILED", "실패"),

    CANCELED("CANCELED", "취소");
}
