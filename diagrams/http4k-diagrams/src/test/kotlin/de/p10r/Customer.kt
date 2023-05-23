package de.p10r

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.events.Events
import org.http4k.filter.ClientFilters

class Customer(baseUri: Uri, events: Events, http: HttpHandler) {
    val http = ClientFilters.ResetRequestTracing()
        .then(ClientFilters.SetHostFrom(baseUri))
        .then(OutgoingHttp(AppEvents("customer", events), http))

    fun orderBike(bikeId: BikeId): Response {
        val request = Request(Method.POST, "/bikes/${bikeId.value}/order")
            .with(orderBikeRequest of OrderBikeRequest(userId = "1", aFinancingRate))

        return http(request)
    }
}
