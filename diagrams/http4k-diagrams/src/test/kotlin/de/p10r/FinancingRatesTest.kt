package de.p10r

import de.p10r.outgoing.WarehouseResponse
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExtendWith(ApprovalTest::class)
class FinancingRatesTest {
    val api = TestApp(
        stock = listOf(
            WarehouseResponse(
                id = "123",
                modelName = "Cannondale X",
                price = 799.0
            )
        )
    )
    @Test
    fun `lists financing rates`(approver: Approver) {
        approver.assertApproved(api(Request(GET, "/bikes/123/rates")))
    }

    @Test
    fun `returns 404 NOT FOUND if bike can't be found`() {
        expectThat(api(Request(GET, "/bikes/666/rates")).status).isEqualTo(NOT_FOUND)
    }
}
