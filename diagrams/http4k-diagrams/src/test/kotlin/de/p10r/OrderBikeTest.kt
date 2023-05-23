package de.p10r

import de.p10r.outgoing.WarehouseResponse
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class OrderBikeTest {
    val checkout = TestApp(
        listOf(
            WarehouseResponse(
                id = "111",
                modelName = "Fuji Track",
                price = 550.0,
            )
        )
    )

    @Test
    fun `orders a bike`() {
        val request = Request(POST, "/bikes/111/order")
            .with(orderBikeRequest of OrderBikeRequest(userId = "1", aFinancingRate))
        val response = checkout(request)

        expectThat(response.status).isEqualTo(CREATED)
    }

    @Test
    fun `returns 404 if bike is not in stock`() {
        val request = Request(POST, "/bikes/1/order")
            .with(orderBikeRequest of OrderBikeRequest(userId = "1", aFinancingRate))
        val response = checkout(request)

        expectThat(response.status).isEqualTo(NOT_FOUND)
    }

    @Test
    fun `returns 400 BAD REQUEST when request is invalid`() {
        expectThat(
            checkout(Request(POST, "/bikes/1/order")).status
        ).isEqualTo(BAD_REQUEST)
    }

    @Test
    fun `returns 200 OK if order was already placed`() {
        val app = checkout
        val request = Request(POST, "/bikes/111/order")
            .with(orderBikeRequest of OrderBikeRequest(userId = "1", aFinancingRate))

        expectThat(app(request).status).isEqualTo(CREATED)
        expectThat(app(request).status).isEqualTo(OK)
    }
}
