package de.p10r.outgoing

import de.p10r.Bike
import de.p10r.BikeId
import de.p10r.Model
import de.p10r.Price
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.format.Jackson.auto

interface Warehouse {
    fun getBike(bikeId: BikeId): Bike?
}

val warehouseResponse = Body.auto<WarehouseResponse>().toLens()

class HttpWarehouse(
    warehouseUri: Uri,
    http: HttpHandler,
) : Warehouse {
    private val warehouseHttp = ClientFilters.SetHostFrom(warehouseUri).then(http)
    override fun getBike(bikeId: BikeId): Bike? {
        val response = warehouseHttp(Request(GET, "/bikes/${bikeId.value}"))

        return if (response.status.successful)
            warehouseResponse(response).toBike()
        else
            null
    }
}

data class WarehouseResponse(
    val id: String,
    val modelName: String,
    val price: Double,
) {
    fun toBike() = Bike(BikeId(id), Model(modelName), Price(price))
}

