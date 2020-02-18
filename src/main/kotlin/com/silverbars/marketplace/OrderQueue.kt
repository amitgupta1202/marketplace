package com.silverbars.marketplace

import java.util.concurrent.ConcurrentLinkedQueue

internal interface OrderQueue {
    fun send(order: Order)
    fun next(): Order?
}

internal class InMemoryOrderQueue : OrderQueue {

    private val modifiedOrders =
        ConcurrentLinkedQueue<Order>()

    override fun send(order: Order) {
        //offer should never return false as queue is unbounded
        modifiedOrders.offer(order)
    }

    override fun next(): Order? = modifiedOrders.poll()
}