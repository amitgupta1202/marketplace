package com.silverbars.marketplace

/*
    Design principles, since its been asked to be designed as shipped as library for client,
    then assumptions made are client is trusted and library needs to be java friendly
    and need to support multiple thread invoking at same time.
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

    /**
     * return live order board
     */
    fun liveOrderBoard(): LiveOrderBoard

    companion object {
        private val orderStorage: OrderStorage = InMemoryOrderStorage()
        private val liveOrderBoardProjectionProcessor = LiveOrderBoardStorage(orderStorage)
        val INSTANCE: OrderService = DefaultOrderService(orderStorage, liveOrderBoardProjectionProcessor)
    }
}

internal class DefaultOrderService(
    private val orderStorage: OrderStorage,
    private val liveOrderBoardStorage: LiveOrderBoardStorage
) :
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
            if (!orderStorage.save(
                    order.copy(orderState = OrderState.Cancelled, version = order.version.next()),
                    order.version
                )
            ) cancelOrder(orderId)
        } else throw InvalidStateException()
    }

    override fun liveOrderBoard(): LiveOrderBoard =
        liveOrderBoardStorage.liveOrderBoard().entries
            .sortedBy { (price, qty) -> if (qty < 0) price else -price }
            .mapNotNull { (pricePerKey, qty) ->
                when {
                    qty < 0 -> LiveOrderBoardItem(OrderType.Sell, -qty, pricePerKey)
                    qty > 0 -> LiveOrderBoardItem(OrderType.Buy, qty, pricePerKey)
                    else -> null
                }
            }
}

