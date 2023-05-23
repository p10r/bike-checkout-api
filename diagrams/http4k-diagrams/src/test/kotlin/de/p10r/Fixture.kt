package de.p10r

import de.p10r.fakes.FakeBank
import de.p10r.fakes.FakeOrderRepository
import de.p10r.fakes.FakeWarehouse
import de.p10r.outgoing.WarehouseResponse
import org.http4k.core.Uri
import org.http4k.events.Events

fun TestApp(
    stock: List<WarehouseResponse> = listOf(
        WarehouseResponse(
            id = "111",
            modelName = "Fuji Track",
            price = 550.0,
        )
    ),
    events: Events = {}
) = App(
    events,
    bankUri = Uri.of("http://bank"), bankHttp = FakeBank(),
    warehouseUri = Uri.of("http://warehouse"), warehouseHttp = FakeWarehouse(stock),
    db = FakeOrderRepository()
)
