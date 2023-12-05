package ru.netology.nmedia.util

import java.math.RoundingMode

object RoundingNumbers {

    fun scoreDisplay(number: Int): String {
        val divisionK = number / 1000
        val divisionKtoDouble = number / 1000.toDouble()
        val divisionM = number / 1000000
        val divisionMtoDouble = number / 1000000.toDouble()

        return when (number) {
            in 1_000..1_099 -> {
                "$divisionK" + "K"
            }

            in 1_100..9_999 -> {
                val k =
                    divisionKtoDouble.toBigDecimal().setScale(1, RoundingMode.DOWN).toDouble()
                "$k" + "K"
            }

            in 10_000..999_999 -> {
                "$divisionK" + "K"
            }

            in 1_000_000..1_099_999 -> {
                "$divisionM" + "M"
            }

            in 1_100_000..9_999_999 -> {
                val m =
                    divisionMtoDouble.toBigDecimal().setScale(1, RoundingMode.DOWN).toDouble()
                "$m" + "M"
            }

            in 10_000_000..999_999_999 -> {
                "$divisionM" + "M"
            }

            else -> number.toString()
        }
    }
}