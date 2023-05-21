package de.p10r.fakes

import de.p10r.CreditId
import de.p10r.FinancingRate
import de.p10r.InterestRate
import de.p10r.Price
import de.p10r.Term
import de.p10r.outgoing.creditIdLens
import de.p10r.ratesLens
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters
import org.http4k.format.Jackson.auto
import org.http4k.lens.Query
import org.http4k.lens.double
import org.http4k.lens.int
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun FakeBank(
    availableRates: List<FinancingRate> = listOf(FinancingRate(InterestRate(3.99), Term(48))),
): HttpHandler = DebuggingFilters.PrintRequestAndResponse().then(
    routes(
        "/rates" bind GET to { req ->
            val price = Query.double().required("price")(req)

            if (price < 1000.0) Response(OK).with(ratesLens of availableRates)
            else Response(Status.NOT_FOUND)
        },
        "/finalize" bind POST to {
            Response(OK).with(creditIdLens of CreditId("bank-1234"))
        }
    )
)

fun main() {
    FakeBank().asServer(SunHttp(9000))
}
