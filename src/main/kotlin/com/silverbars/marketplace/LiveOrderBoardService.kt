package com.silverbars.marketplace

import com.silverbars.marketplace.OrderState.Active
import com.silverbars.marketplace.OrderState.Cancelled
import com.silverbars.marketplace.OrderType.Buy
import com.silverbars.marketplace.OrderType.Sell
import java.util.concurrent.ConcurrentHashMap

interface LiveOrderBoardService {

    fun liveOrderBoard(): LiveOrderBoard
}

interface LiveOrderBoardProjectionProcessor {
    /*
        updates the live board to upto date state, designed to run as separate background task
    */
    fun updateLiveOrderBoard()
}

internal class InMemoryLiveOrderBoardService(private val orderStorage: OrderStorage) : LiveOrderBoardService,
    LiveOrderBoardProjectionProcessor {

    private val quantityByPrice: ConcurrentHashMap<Double, Double> = ConcurrentHashMap()

    override fun liveOrderBoard(): LiveOrderBoard {
        val immutablePriceAndQty = quantityByPrice.entries.toList().map { (k, v) -> k to v }

        return immutablePriceAndQty
            .sortedBy { (price, qty) -> if (qty < 0) price else -price }
            .mapNotNull { (pricePerKey, qty) ->
                when {
                    qty < 0 -> LiveOrderBoardItem(Sell, -qty, pricePerKey)
                    qty > 0 -> LiveOrderBoardItem(Buy, qty, pricePerKey)
                    else -> null
                }
            }
    }

    override tailrec fun updateLiveOrderBoard() {
        val order = orderStorage.next()

        if (order != null) {
            with(order) {
                val operand =
                    if ((orderType == Buy && orderState == Active) || (orderType == Sell && orderState == Cancelled)) 1 else -1

                quantityByPrice.merge(
                    pricePerKg,
                    quantity * operand
                ) { oldQuantity, newQuantity -> oldQuantity + newQuantity }
            }

            updateLiveOrderBoard()
        }
    }

}