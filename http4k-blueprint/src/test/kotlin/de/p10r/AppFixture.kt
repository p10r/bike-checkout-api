package de.p10r

import de.p10r.fakes.FakeBank
import de.p10r.fakes.FakeOrderRepository
import de.p10r.fakes.FakeWarehouse
import de.p10r.outgoing.HttpBankClient
import de.p10r.outgoing.HttpWarehouse
import de.p10r.outgoing.WarehouseResponse
import org.http4k.core.Uri

fun CheckoutApi(
    stock: List<WarehouseResponse> = listOf(
        WarehouseResponse(
            id = "111",
            modelName = "Fuji Track",
            price = 550.0,
        )
    ),
) = CheckoutApi(
    HttpBankClient(Uri.of("local"), FakeBank()),
    HttpWarehouse(Uri.of("local"), FakeWarehouse(stock)),
    FakeOrderRepository()
).let(::App)
