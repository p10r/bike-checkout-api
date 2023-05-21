package de.p10r.outgoing

import de.p10r.FinancingRate
import de.p10r.InterestRate
import de.p10r.NewOrder
import de.p10r.Term
import de.p10r.UserId
import de.p10r.aCreditId
import de.p10r.aBikeId
import de.p10r.fakes.FakeOrderRepository
import de.p10r.aUserId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.testcontainers.containers.MongoDBContainer
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEmpty

abstract class OrderRepositoryTest(val repository: OrderRepository) {
    val rate = FinancingRate(InterestRate(4.99), Term(48))
    val order = NewOrder(aBikeId, aUserId, rate, aCreditId)

    @Test
    fun `stores an order`() {
        repository.create(order)

        val orders = repository.findAll()

        expectThat(orders).hasSize(1)
        expectThat(orders.first().bikeId).isEqualTo(aBikeId)
        expectThat(orders.first().userId).isEqualTo(aUserId)
    }

    @Test
    fun `finds an order`() {
        val userId = UserId("333")

        repository.create(order.copy(userId = userId))

        expectThat(repository.findBy(userId)).isNotEmpty()
    }
}

class InMemoryRepositoryTest : OrderRepositoryTest(FakeOrderRepository())

@EnabledIfSystemProperty(named = "run-docker", matches = "true")
class MongoRepositoryTest : OrderRepositoryTest(
    MongoOrderRepository(
        DbConfig(
            dbUrl = mongoInstance.connectionString,
            dbName = "orders"
        )
    )
)

val mongoInstance: MongoDBContainer by lazy {
    MongoDBContainer("mongo:latest").withReuse(true)
        .apply { start() }
}
