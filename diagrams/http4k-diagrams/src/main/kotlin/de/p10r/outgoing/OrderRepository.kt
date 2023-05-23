package de.p10r.outgoing

import de.p10r.NewOrder
import de.p10r.OrderId
import de.p10r.StoredOrder
import de.p10r.UserId
import de.p10r.toStoredOrder
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.resultFrom
import org.litote.kmongo.KMongo
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import java.time.Instant
import java.util.UUID

interface OrderRepository {
    fun create(newOrder: NewOrder): StoredOrder
    fun findAll(): List<StoredOrder>
    fun findBy(userId: UserId): List<StoredOrder>
}

class MongoOrderRepository(dbConfig: DbConfig) : OrderRepository {
    val collection = dbConfig.collection
    override fun create(newOrder: NewOrder): StoredOrder {
        val id = UUID.randomUUID().toString()
        val storedOrder = newOrder.toStoredOrder(OrderId(id), createdAt = Instant.now())

        return collection.insertOne(storedOrder)
            .let { storedOrder }
    }

    override fun findAll(): List<StoredOrder> =
        collection.find().toList()

    override fun findBy(userId: UserId): List<StoredOrder> {
        return collection.find(StoredOrder::userId eq userId).toList()
    }
}

data class DbConfig(
    val dbUrl: String,
    val dbName: String,
) {
    val client = KMongo.createClient(dbUrl)
    val db = client.getDatabase(dbName)
    val collection = db.getCollection<StoredOrder>(collectionName = dbName)
}
