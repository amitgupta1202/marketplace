package com.silverbars.marketplace

/*
    Design principles, since its been asked to be designed as shipped as library for client, then assumptions made are client is trusted and library needs to be java friendly
 */
interface OrderService {

    /**
     * Registers an order.
     *
     * throws InvalidStateException when its order is does not exists or order is already cancelled.
     */
    fun registerOrder(orderId: OrderId, userId: UserId, quantity: Double, pricePerKg: Double, orderType: OrderType)

    /**
     * Cancels an order.
     *
     * throws InvalidStateException when its order is does not exists or order is already cancelled.
     */
    fun cancelOrder(orderId: OrderId)

}

internal class DefaultOrderService(private val orderStorage: OrderStorage) :
    OrderService {

    override fun registerOrder(
        orderId: OrderId,
        userId: UserId,
        quantity: Double,
        pricePerKg: Double,
        orderType: OrderType
    ) {
        val existingOrder = orderStorage.get(orderId)
        if (existingOrder == null) {
            val order = Order(
                orderId,
                userId,
                quantity,
                pricePerKg,
                orderType,
                OrderState.Active,
                Version.INITIAL
            )
            if (!orderStorage.save(order, order.version)) throw OrderAlreadyExists()
        }
    }

    override tailrec fun cancelOrder(orderId: OrderId) {
        val order = orderStorage.get(orderId)
        if (order != null && order.orderState == OrderState.Active) {
            if (!orderStorage.save(order.copy(orderState = OrderState.Cancelled, version = order.version.next()), order.version)) cancelOrder(orderId)
        } else throw InvalidStateException()
    }
}

