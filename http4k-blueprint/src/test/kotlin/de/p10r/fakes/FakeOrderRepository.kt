package de.p10r.fakes

import de.p10r.NewOrder
import de.p10r.OrderId
import de.p10r.StoredOrder
import de.p10r.UserId
import de.p10r.outgoing.OrderRepository
import de.p10r.toStoredOrder
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import java.time.Instant
import java.util.UUID

fun FakeOrderRepository() = object : OrderRepository {
    private val storedOrders = mutableMapOf<String, StoredOrder>()
    override fun create(newOrder: NewOrder): StoredOrder {
        val id = UUID.randomUUID().toString()

        return newOrder.toStoredOrder(
            OrderId(value = id),
            Instant.now()
        ).also { storedOrders[id] = it }
    }

    override fun findAll(): List<StoredOrder> =
        storedOrders.values.toList()

    override fun findBy(userId: UserId): List<StoredOrder> =
        storedOrders.values.filter { it.userId == userId }
}
