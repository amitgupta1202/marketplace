package com.silverbars.marketplace

import com.silverbars.marketplace.OrderState.Active
import com.silverbars.marketplace.OrderState.Cancelled
import com.silverbars.marketplace.OrderType.Buy
import com.silverbars.marketplace.OrderType.Sell
import java.util.concurrent.ConcurrentHashMap

internal interface LiveOrderBoardStorage {
    /**
     * This method is responsible to keep the live order board upto date with changing orders
     */
    fun update()

    /**
     * returns current state of live order board in raw format
     */
    fun get(): Map<Double, Double>
}

internal class InMemoryLiveOrderBoardStorage(private val orderQueue: OrderQueue): LiveOrderBoardStorage {

    private val quantityByPrice: ConcurrentHashMap<Double, Double> = ConcurrentHashMap()

    override tailrec fun update() {
        val order = orderQueue.next()

        if (order != null) {
            with(order) {
                val operand =
                    if ((orderType == Buy && orderState == Active) || (orderType == Sell && orderState == Cancelled)) 1 else -1

                quantityByPrice.merge(
                    pricePerKg,
                    quantity * operand
                ) { oldQuantity, newQuantity -> oldQuantity + newQuantity }
            }

            update()
        }
    }

    override fun get(): Map<Double, Double> {
        /*
            Ideally in my definition of classic CQRS, this is expected to be called either scheduler every 50 to 100ms
            which can pull messages or possibly can be invoked by message arrival in push model for simplicity just
            calling to before returning live order board for demonstration and ease of testing, generally should not
            be part of production code as its blocking and slowing down response to live order board
         */
        update()

        return quantityByPrice.toMap()
    }

}

