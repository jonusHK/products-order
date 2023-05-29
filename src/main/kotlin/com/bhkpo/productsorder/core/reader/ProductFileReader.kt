package com.bhkpo.productsorder.core.reader

import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.FileReader

@Component("productFileReader")
class ProductFileReader : BaseFileReader {

    override var extension: String = "csv"

    override fun read(path: String): List<String> {
        // 파일 경로 유효성 검사
        this.validate(path)

        val br = BufferedReader(FileReader(path))
        var line: String?
        val rows = arrayListOf<String>()

        while (
            run {
                line = br.readLine()
                line
            } != null
        ) {
            rows.add(line!!)
        }

        return rows
    }
}
