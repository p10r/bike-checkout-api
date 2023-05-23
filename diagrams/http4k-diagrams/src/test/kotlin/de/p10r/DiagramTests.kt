package de.p10r

import de.p10r.outgoing.WarehouseResponse
import org.http4k.core.Status
import org.http4k.core.Uri
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DiagramTests : RecordTraces() {
    val bikeId = BikeId("111")
    val checkout = TestApp(
        listOf(
            WarehouseResponse(
                id = bikeId.value,
                modelName = "Fuji Track",
                price = 550.0,
            )
        ),
        events = events
    )

    val customer = Customer(Uri.of("http://checkout-api"), events, checkout)

    @Test
    fun `orders a bike`() {
        val response = customer.orderBike(bikeId)

        expectThat(response.status).isEqualTo(Status.CREATED)
    }
}
