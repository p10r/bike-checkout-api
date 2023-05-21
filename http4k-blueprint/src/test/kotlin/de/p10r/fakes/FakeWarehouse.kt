package de.p10r.fakes

import de.p10r.id
import de.p10r.outgoing.WarehouseResponse
import de.p10r.outgoing.warehouseResponse
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters
import org.http4k.routing.bind
import org.http4k.routing.routes

fun FakeWarehouse(
    stock: List<WarehouseResponse>,
): HttpHandler = DebuggingFilters.PrintRequestAndResponse().then(
    routes(
        "/bikes/{id}" bind GET to { req ->
            val bikeId = id(req)
            stock.firstOrNull { it.id == bikeId.value }
                ?.let { stock ->
                    Response(OK).with(warehouseResponse of stock)
                } ?: Response(Status.NOT_FOUND)
        }
    )
)
