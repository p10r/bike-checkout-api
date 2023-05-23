package de.p10r.outgoing

import de.p10r.BikeId
import de.p10r.fakes.FakeWarehouse
import org.http4k.core.Uri
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotNull
import strikt.assertions.isNull

class WarehouseTest {
    val warehouse = HttpWarehouse(
        warehouseUri = Uri.of("local"),
        http = FakeWarehouse(
            stock = listOf(
                WarehouseResponse(
                    id = "111",
                    modelName = "Fuji Track",
                    price = 550.0,
                )
            )
        )
    )

    @Test
    fun `returns given bike`() {
        expectThat(warehouse.getBike(BikeId("111"))).isNotNull()
    }

    @Test
    fun `returns not found if bike is not in stock`() {
        expectThat(warehouse.getBike(BikeId("666"))).isNull()
    }
}
