package de.p10r

import de.p10r.outgoing.WarehouseResponse
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DiagramTests : RecordTraces() {
    val checkout = TestApp(
        listOf(
            WarehouseResponse(
                id = "111",
                modelName = "Fuji Track",
                price = 550.0,
            )
        ),
        events = events
    )

    @Test
    fun `orders a bike`() {
        val request = Request(Method.POST, "/bikes/111/order")
            .with(orderBikeRequest of OrderBikeRequest(userId = "1", aFinancingRate))

        expectThat(checkout(request).status).isEqualTo(Status.CREATED)
    }
}
