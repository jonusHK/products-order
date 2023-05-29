package com.bhkpo.productsorder.core.reader

interface BaseFileReader {

    var extension: String

    fun read(path: String): List<String>

    fun validate(path: String) {
        if (path.split(".").last() != extension) {
            throw RuntimeException("확장자가 올바르지 않습니다.")
        }
    }
}
