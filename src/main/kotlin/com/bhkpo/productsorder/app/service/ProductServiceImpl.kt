package com.bhkpo.productsorder.app.service

import com.bhkpo.productsorder.core.dto.ProductDto
import com.bhkpo.productsorder.core.entity.Product
import com.bhkpo.productsorder.core.mapper.ProductMapper
import com.bhkpo.productsorder.core.repository.ProductRepository
import com.bhkpo.productsorder.core.repository.condition.ProductCondition
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(private val productRepository: ProductRepository) : ProductService {

    /**
     * 특정 조건으로 상품 검색
     */
    override fun searchProducts(condition: ProductCondition?): List<ProductDto> {
        val products: List<Product> = productRepository.searchProducts(condition)

        return products.map { ProductMapper.toDto(it) }
    }

    /**
     * 상품 목록 초기화 및 생성
     */
    override fun initProducts() {
        productRepository.initProducts()
    }
}
