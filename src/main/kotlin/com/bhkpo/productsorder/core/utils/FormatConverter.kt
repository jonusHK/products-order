package com.bhkpo.productsorder.core.utils

import java.text.NumberFormat
import java.util.*

object FormatConverter {

    fun formatNumber(target: Any, locale: Locale = Locale.getDefault()): String {
        return NumberFormat.getInstance(locale).format(target)
    }
}
