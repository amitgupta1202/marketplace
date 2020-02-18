package com.silverbars.marketplace

import com.silverbars.marketplace.OrderType.Buy
import com.silverbars.marketplace.OrderType.Sell
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class OrderServiceTest {

    private val orderService = OrderService.newInstance()

    @Test
    internal fun `register a sell order`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a sell order and then cancel it`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderId = OrderId.generate()
        orderService.registerOrder(
            orderId,
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        orderService.cancelOrder(orderId)

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEmpty()
    }

    @Test
    internal fun `register two same sell order with same price`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKg, Sell
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity * 2,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register two same sell order with same price and cancel one of them`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderIdA = OrderId.generate()
        orderService.registerOrder(
            orderIdA,
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        val orderIdB = OrderId.generate()
        orderService.registerOrder(
            orderIdB,
            UserId("user 2"), quantity, pricePerKg, Sell
        )
        orderService.cancelOrder(orderIdA)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register two sell order with different price where lowest should be at top`() {
        val quantity = 3.5
        val pricePerKgHigher = 307.0
        val pricePerKgLower = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKgHigher, Sell
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKgLower, Sell
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKgLower
            ), LiveOrderBoardItem(Sell, quantity, pricePerKgHigher)
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a buy order`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Buy
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a buy order and then cancel it`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderId = OrderId.generate()
        orderService.registerOrder(
            orderId,
            UserId("user 1"), quantity, pricePerKg, Buy
        )
        orderService.cancelOrder(orderId)

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEmpty()
    }

    @Test
    internal fun `register two buy order of same price`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Buy
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKg, Buy
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity * 2,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register two buy order of same price and cancel one of them`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderIdA = OrderId.generate()
        orderService.registerOrder(
            orderIdA,
            UserId("user 1"), quantity, pricePerKg, Buy
        )
        val orderIdB = OrderId.generate()
        orderService.registerOrder(
            orderIdB,
            UserId("user 2"), quantity, pricePerKg, Buy
        )
        orderService.cancelOrder(orderIdB)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register two buy order of different price where highest should be at top`() {
        val quantity = 3.5
        val pricePerKgLower = 306.0
        val pricePerKgHigher = 307.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKgLower, Buy
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKgHigher, Buy
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKgHigher
            ), LiveOrderBoardItem(Buy, quantity, pricePerKgLower)
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a sell & a buy order of same price and quantity`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKg, Buy
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEmpty()
    }

    @Test
    internal fun `register a sell & a buy order of same price and quantity and cancel buy order`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderIdA = OrderId.generate()
        orderService.registerOrder(
            orderIdA,
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        val orderIdB = OrderId.generate()
        orderService.registerOrder(
            orderIdB,
            UserId("user 2"), quantity, pricePerKg, Buy
        )
        orderService.cancelOrder(orderIdB)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a sell & a buy order of same price and quantity and cancel sell order`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        val orderIdA = OrderId.generate()
        orderService.registerOrder(
            orderIdA,
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        val orderIdB = OrderId.generate()
        orderService.registerOrder(
            orderIdB,
            UserId("user 2"), quantity, pricePerKg, Buy
        )
        orderService.cancelOrder(orderIdA)
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a sell and then a buy and then a sell order of same price and quantity`() {
        val quantity = 3.5
        val pricePerKg = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, pricePerKg, Buy
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, pricePerKg, Sell
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Sell,
                quantity,
                pricePerKg
            )
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a buy & a sell order fo different price buy price being higher, buy order at top`() {
        val quantity = 3.5
        val buyPricePerKgHigher = 307.0
        val sellPricePerKgLower = 306.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, buyPricePerKgHigher, Buy
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, sellPricePerKgLower, Sell
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                buyPricePerKgHigher
            ), LiveOrderBoardItem(Sell, quantity, sellPricePerKgLower)
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }

    @Test
    internal fun `register a buy & a sell order fo different price sell price being higher, buy order at top`() {
        val quantity = 3.5
        val buyPricePerKgHigher = 306.0
        val sellPricePerKgLower = 307.0
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 1"), quantity, buyPricePerKgHigher, Buy
        )
        orderService.registerOrder(
            OrderId.generate(),
            UserId("user 2"), quantity, sellPricePerKgLower, Sell
        )
        val expectedLiveBoard = listOf(
            LiveOrderBoardItem(
                Buy,
                quantity,
                buyPricePerKgHigher
            ), LiveOrderBoardItem(Sell, quantity, sellPricePerKgLower)
        )

        val liveBoard = orderService.liveOrderBoard()

        assertThat(liveBoard).isEqualTo(expectedLiveBoard)
    }
}