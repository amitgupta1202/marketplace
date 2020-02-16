@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package com.silverbars.marketplace

import java.util.*

inline class OrderId(private val raw: UUID) {
    companion object {
        fun generate() = OrderId(UUID.randomUUID())
    }
}

inline class UserId(private val raw: String)

inline class Version(private val raw: Int) {
    fun next() = Version(raw + 1)

    companion object {
        val INITIAL = Version(1)
    }
}

enum class OrderType { Buy, Sell }

enum class OrderState { Active, Cancelled }

data class Order(
    val orderId: OrderId,
    val userId: UserId,
    val quantity: Double,
    val pricePerKg: Double,
    val orderType: OrderType,
    val orderState: OrderState,
    val version: Version
)

typealias LiveOrderBoard = List<LiveOrderBoardItem>
data class LiveOrderBoardItem(val orderType: OrderType, val quantity: Double, val pricePerKg: Double)

sealed class ApplicationException(override val message: String) : RuntimeException(message)
data class OrderAlreadyExists(override val message: String = "order already exists") : ApplicationException(message)
data class InvalidStateException(override val message: String = "Invalid State") : ApplicationException(message)