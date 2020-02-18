package com.silverbars.marketplace

import java.util.concurrent.ConcurrentHashMap

internal interface OrderStorage {
    fun save(order: Order, expectedVersion: Version): Boolean
    fun get(orderId: OrderId): Order?
}

internal class InMemoryOrderStorage(private val orderQueue: OrderQueue) : OrderStorage {
    private val ordersByOrderId = ConcurrentHashMap<OrderId, Order>()

    override fun save(order: Order, expectedVersion: Version): Boolean {
        val oldOrder = ordersByOrderId.putIfAbsent(order.orderId, order)
        if (oldOrder == null || (oldOrder.version == expectedVersion && ordersByOrderId.replace(
                order.orderId,
                oldOrder,
                order
            ))
        ) {
            /*
                it will not guarantee the ordering so we may see cancel order arriving before register even when
                register order is place happens before cancel in exception cases due in concurrent environment,
                it should not affect the outcome of live order board, demonstrated in OrderServiceExceptionBehaviourTest

                but in real live this needs to be taken care of ideally should be pipelined form the storage, depends upon storage
                we choose like in RDBMS possibly use CDC (change data capture), if using kafka as storage then problem in already solved

                I am sure there can be billions of solutions

                for simplicity I have not solved this issue in this in-memory implementation
             */
            orderQueue.send(order)
            return true
        }
        return false
    }

    override fun get(orderId: OrderId): Order? = ordersByOrderId[orderId]


}