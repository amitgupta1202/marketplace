package com.silverbars.marketplace

import com.silverbars.marketplace.OrderState.Active
import com.silverbars.marketplace.OrderState.Cancelled
import com.silverbars.marketplace.OrderType.Buy
import com.silverbars.marketplace.OrderType.Sell
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LiveOrderBoardServiceTest {

    private val orderStorage: OrderStorage =
        InMemoryOrderStorage()
    private val orderService: OrderService =
        DefaultOrderService(orderStorage)
    private val liveOrderBoardService =
        InMemoryLiveOrderBoardService(orderStorage)

    @Test
    internal fun `register a sell order`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a sell order and then cancel it`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderId = OrderId.generate()
        orderService.registerOrder(orderId,
            UserId("user 1"), quantity, pricePerKg, Sell)
        orderService.cancelOrder(orderId)

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEmpty()
    }

    @Test
    internal fun `register two same sell order with same price`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKg, Sell)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity * 2,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register two same sell order with same price and cancel one of them`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderIdA = OrderId.generate()
        orderService.registerOrder(orderIdA,
            UserId("user 1"), quantity, pricePerKg, Sell)
        val orderIdB = OrderId.generate()
        orderService.registerOrder(orderIdB,
            UserId("user 2"), quantity, pricePerKg, Sell)
        orderService.cancelOrder(orderIdA)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register two sell order with different price where lowest should be at top`() {
        val quantity = 3.5
        val pricePerKgHigher = 307.0
        val pricePerKgLower = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKgHigher, Sell)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity , pricePerKgLower, Sell)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKgLower
            ), LiveOrderBoardItem(Sell, quantity, pricePerKgHigher)
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a buy order`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Buy)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a buy order and then cancel it`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderId = OrderId.generate()
        orderService.registerOrder(orderId,
            UserId("user 1"), quantity, pricePerKg, Buy)
        orderService.cancelOrder(orderId)

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEmpty()
    }

    @Test
    internal fun `register two buy order of same price`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Buy)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKg, Buy)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity * 2,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register two buy order of same price and cancel one of them`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderIdA = OrderId.generate()
        orderService.registerOrder(orderIdA,
            UserId("user 1"), quantity, pricePerKg, Buy)
        val orderIdB = OrderId.generate()
        orderService.registerOrder(orderIdB,
            UserId("user 2"), quantity, pricePerKg, Buy)
        orderService.cancelOrder(orderIdB)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register two buy order of different price where highest should be at top`() {
        val quantity = 3.5
        val pricePerKgLower = 306.0
        val pricePerKgHigher = 307.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKgLower, Buy)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKgHigher, Buy)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKgHigher
            ), LiveOrderBoardItem(Buy, quantity, pricePerKgLower)
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a sell & a buy order of same price and quantity`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKg, Buy)

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEmpty()
    }

    @Test
    internal fun `register a sell & a buy order of same price and quantity and cancel buy order`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderIdA = OrderId.generate()
        orderService.registerOrder(orderIdA,
            UserId("user 1"), quantity, pricePerKg, Sell)
        val orderIdB = OrderId.generate()
        orderService.registerOrder(orderIdB,
            UserId("user 2"), quantity, pricePerKg, Buy)
        orderService.cancelOrder(orderIdB)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a sell & a buy order of same price and quantity and cancel sell order`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderIdA = OrderId.generate()
        orderService.registerOrder(orderIdA,
            UserId("user 1"), quantity, pricePerKg, Sell)
        val orderIdB = OrderId.generate()
        orderService.registerOrder(orderIdB,
            UserId("user 2"), quantity, pricePerKg, Buy)
        orderService.cancelOrder(orderIdA)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a sell and then a buy and then a sell order of same price and quantity`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKg, Buy)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKg
            )
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a buy & a sell order fo different price buy price being higher, buy order at top`() {
        val quantity = 3.5
        val buyPricePerKgHigher = 307.0
        val sellPricePerKgLower = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, buyPricePerKgHigher, Buy)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, sellPricePerKgLower, Sell)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                buyPricePerKgHigher
            ), LiveOrderBoardItem(Sell, quantity, sellPricePerKgLower)
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a buy & a sell order fo different price sell price being higher, buy order at top`() {
        val quantity = 3.5
        val buyPricePerKgHigher = 306.0
        val sellPricePerKgLower = 307.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, buyPricePerKgHigher, Buy)
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, sellPricePerKgLower, Sell)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                buyPricePerKgHigher
            ), LiveOrderBoardItem(Sell, quantity, sellPricePerKgLower)
        )

        liveOrderBoardService.updateLiveOrderBoard()

        val liveBoard = liveOrderBoardService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

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
        liveOrderBoardService.updateLiveOrderBoard()
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKg
            )
        )

        val liveBoardAfterCancellation = liveOrderBoardService.liveOrderBoard()
        assertThat(liveBoardAfterCancellation).isEqualTo(expectedLiveBoard) //looks bad

        val activeOrder = cancelledOrder.copy(orderState = Active, version = Version.INITIAL)
        orderStorage.save(activeOrder, expectedVersion = activeOrder.version)

        liveOrderBoardService.updateLiveOrderBoard()
        assertThat(liveOrderBoardService.liveOrderBoard()).isEmpty() //fixes itself when original active order arrives
    }

}