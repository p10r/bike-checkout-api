package de.p10r

import de.p10r.GetFinancingResult.BikeNotFound
import de.p10r.GetFinancingResult.Rates
import de.p10r.GetFinancingResult.RatesNotAvailable
import de.p10r.OrderResult.AlreadyOrdered
import de.p10r.OrderResult.BikeNotInStock
import de.p10r.OrderResult.CannotBeFinanced
import de.p10r.OrderResult.ServerError
import de.p10r.OrderResult.Success
import de.p10r.outgoing.HttpBankClient
import de.p10r.outgoing.HttpWarehouse
import de.p10r.outgoing.OrderRepository
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.events.Events
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.routing.bind
import org.http4k.routing.routes

val ratesLens = Body.auto<List<FinancingRate>>().toLens()
val orderBikeRequest = Body.auto<OrderBikeRequest>().toLens()
val id = Path.map(::BikeId).of("id")

fun App(
    events: Events,
    bankUri: Uri,
    bankHttp: HttpHandler,
    warehouseUri: Uri,
    warehouseHttp: HttpHandler,
    db: OrderRepository,
): HttpHandler {
    val appEvents = AppEvents("checkout-api", events)

    val checkout = CheckoutApi(
        HttpBankClient(bankUri, OutgoingHttp(appEvents, bankHttp)),
        HttpWarehouse(warehouseUri, OutgoingHttp(appEvents, warehouseHttp)),
        db
    )

    return PrintRequest()
        .then(ServerFilters.CatchAll())
        .then(ServerFilters.CatchLensFailure())
        .then(IncomingHttp(events, ApiRoutes(checkout)))
}

private fun ApiRoutes(checkout: CheckoutApi) = routes(
    "/bikes/{id}/rates" bind GET to { req ->
        when (val result = checkout.listFinancingRates(id(req))) {
            is BikeNotFound -> Response(NOT_FOUND)
            is RatesNotAvailable -> Response(NOT_FOUND)
            is Rates -> Response(OK).with(ratesLens of result.rates)
        }
    },
    "/bikes/{id}/order" bind POST to { req: Request ->
        val order = orderBikeRequest(req)

        when (
            checkout.order(
                userId = UserId(order.userId),
                bikeId = id(target = req),
                chosenFinancingRate = order.financingRate
            )
        ) {
            is BikeNotInStock -> Response(NOT_FOUND)
            is CannotBeFinanced -> Response(CONFLICT)
            is Success -> Response(CREATED)
            is AlreadyOrdered -> Response(OK)
            is ServerError -> Response(INTERNAL_SERVER_ERROR)
        }
    }
)

