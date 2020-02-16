package com.silverbars.marketplace

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

internal interface OrderStorage {
    fun save(order: Order, expectedVersion: Version): Boolean
    fun get(orderId: OrderId): Order?
    fun next(): Order?
}

internal class InMemoryOrderStorage : OrderStorage {
    private val ordersByOrderId = ConcurrentHashMap<OrderId, Order>()

    private val modifiedOrders = ConcurrentLinkedQueue<Order>()

    override fun save(order: Order, expectedVersion: Version): Boolean {
        val oldOrder = ordersByOrderId.putIfAbsent(order.orderId, order)
        if (oldOrder == null || (oldOrder.version == expectedVersion && ordersByOrderId.replace(order.orderId, oldOrder, order))) {
            /*
                offer should never return false as queue is unbounded and
                modifiedOrders queue doesnt provide any insertion ordering guarantee because of concurrency
                as CancelOrder may appear before RegisterOrder for same order id, but for simplicity we will not do anything
                as for live order board implementation no such guarantee is necessary

                this is a dummy solution of a storage, in real life we can use probably some db and message systems to make sure it doesnt happen
             */
            return modifiedOrders.offer(order)
        }
        return false
    }

    override fun get(orderId: OrderId): Order? = ordersByOrderId[orderId]

    override fun next(): Order? = modifiedOrders.poll()
}