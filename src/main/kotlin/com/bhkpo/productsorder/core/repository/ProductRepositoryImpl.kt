package com.bhkpo.productsorder.core.repository

import com.bhkpo.productsorder.core.entity.Product
import com.bhkpo.productsorder.core.reader.BaseFileReader
import com.bhkpo.productsorder.core.repository.condition.ProductCondition
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    @Qualifier("productFileReader") private val productFileReader: BaseFileReader
) : ProductRepository {

    @Value("\${products.path}")
    lateinit var productsPath: String

    private val products = arrayListOf<Product>()

    /**
     * 상품 정보 초기화
     */
    override fun initProducts() {
        products.clear()

        val lines = productFileReader.read(productsPath)
        var index = 1L
        for (line in lines) {
            val rows = line.split(",")

            if (rows.size != 4) continue
            if (!Regex("\\d{6,}").matches(rows.first())) continue

            products.add(
                Product(
                    id = index,
                    number = rows[0].toInt(),
                    name = rows[1],
                    price = rows[2].toInt(),
                    quantity = rows[3].toInt()
                )
            )
            index++
        }
    }

    /**
     * 특정 조건으로 상품 검색
     */
    override fun searchProducts(condition: ProductCondition?): List<Product> {
        return condition?.let {
            getProductsByCondition(it)
        } ?: products
    }

    private fun getProductsByCondition(condition: ProductCondition): List<Product> {
        var filtered = arrayListOf<Product>()
        filtered.addAll(products)

        condition.ids?.let { ids ->
            filtered = ArrayList(filtered.filter { it.id in ids })
        }
        condition.numbers?.let { numbers ->
            filtered = ArrayList(filtered.filter { it.number in numbers })
        }

        return filtered
    }
}
