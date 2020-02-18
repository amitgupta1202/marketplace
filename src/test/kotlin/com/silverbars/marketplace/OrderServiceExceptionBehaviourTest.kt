package com.silverbars.marketplace

import com.silverbars.marketplace.OrderState.Active
import com.silverbars.marketplace.OrderState.Cancelled
import com.silverbars.marketplace.OrderType.Buy
import com.silverbars.marketplace.OrderType.Sell
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class OrderServiceExceptionBehaviourTest {

    private val orderQueue: OrderQueue = InMemoryOrderQueue()
    private val orderStorage: OrderStorage = InMemoryOrderStorage(orderQueue)
    private val liveOrderBoardProjectionProcessor: LiveOrderBoardStorage = InMemoryLiveOrderBoardStorage(orderQueue)
    private val orderService: OrderService = DefaultOrderService(orderStorage, liveOrderBoardProjectionProcessor)

    @Test
    internal fun `an exception case (possible due to concurrency of in-memory store implementation), cancel order arrives before register, expected behaviour is accepted as correct for example purpose, might not be exactly desired`() {
        /*
            This test demonstrates how current implementation behaves, but it for documentation, probably in real life we will be dealing with db and messaging queue, might not be an applicable case
         */
        val quantity = 3.5
        val pricePerKg = 306.0
        val cancelledOrder = Order(
            OrderId.generate(),
            UserId("user 1"),
            quantity,
            pricePerKg,
            Sell,
            Cancelled,
            Version.INITIAL
        )
        orderStorage.save(cancelledOrder, expectedVersion = Version.INITIAL)

        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKg
            )
        )

        val liveBoardAfterCancellation = orderService.liveOrderBoard()
        assertThat(liveBoardAfterCancellation).isEqualTo(expectedLiveBoard) //looks bad

        val activeOrder = cancelledOrder.copy(orderState = Active, version = Version.INITIAL)
        orderStorage.save(activeOrder, expectedVersion = activeOrder.version)

        assertThat(orderService.liveOrderBoard()).isEmpty() //fixes itself when original active order arrives
    }
}