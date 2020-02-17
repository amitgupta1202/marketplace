package com.silverbars.marketplace

import com.silverbars.marketplace.OrderState.Active
import com.silverbars.marketplace.OrderState.Cancelled
import com.silverbars.marketplace.OrderType.Buy
import com.silverbars.marketplace.OrderType.Sell
import java.util.concurrent.ConcurrentHashMap

internal class LiveOrderBoardStorage(private val orderStorage: OrderStorage) {

    private val quantityByPrice: ConcurrentHashMap<Double, Double> = ConcurrentHashMap()

    /**
     * This method is responsible to keep the qualityByPrice upto date
     */
    private tailrec fun updateLiveOrderBoard() {
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

    fun liveOrderBoard(): Map<Double, Double> {
        /*
            Ideally this is expected to be called either scheduler every 50 to 100ms which can pull messages or possibly can be invoked by message arrival in push model
            for simplicity just calling to before returning live order board for demonstration and ease of testing, generally should not be part of production code
            as its blocking and slowing down response to live order board
         */
        updateLiveOrderBoard()

        return quantityByPrice.toMap()
    }

}

