package de.p10r.fakes

import de.p10r.AppEvents
import de.p10r.DbCall
import de.p10r.NewOrder
import de.p10r.OrderId
import de.p10r.StoredOrder
import de.p10r.UserId
import de.p10r.outgoing.OrderRepository
import de.p10r.toStoredOrder
import org.http4k.events.Events
import java.time.Instant
import java.util.UUID

fun FakeOrderRepository(events: Events = {}) = object : OrderRepository {
    private val storedOrders = mutableMapOf<String, StoredOrder>()
    override fun create(newOrder: NewOrder): StoredOrder {
        val id = UUID.randomUUID().toString()

        return newOrder.toStoredOrder(
            OrderId(value = id),
            Instant.now()
        ).also { storedOrders[id] = it }
            .also { event("create order") }
    }

    override fun findAll(): List<StoredOrder> =
        storedOrders.values.toList()
            .also { event("find all orders") }

    override fun findBy(userId: UserId): List<StoredOrder> =
        storedOrders.values.filter { it.userId == userId }
            .also { event("find by user id") }

    fun event(methodCall: String) {
        DbCall(methodCall)
        AppEvents("checkout-api", events)(DbCall(methodCall))
    }
}
