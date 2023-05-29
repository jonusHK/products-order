package com.bhkpo.productsorder.core.external.redisson.callback

fun interface RedissonCallback<T> {

    fun call(): T
}
