package de.p10r.outgoing

import de.p10r.Bike
import de.p10r.BikeId
import de.p10r.CreditId
import de.p10r.FinancingRate
import de.p10r.InterestRate
import de.p10r.Price
import de.p10r.Term
import de.p10r.ratesLens
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters
import org.http4k.format.Jackson.auto
import org.http4k.lens.Query
import org.http4k.lens.double


interface Bank {
    fun getFinancingRates(bike: Bike): Result<List<FinancingRate>, Exception>

    fun finalize(financingRate: FinancingRate): Result<CreditId, Exception>
}

val rateLens = Body.auto<FinancingRate>().toLens()
val creditIdLens = Body.auto<CreditId>().toLens()

class HttpBankClient(
    bankUri: Uri,
    http: HttpHandler,
) : Bank {
    private val bankHttp = ClientFilters.SetHostFrom(bankUri).then(http)

    override fun getFinancingRates(bike: Bike): Result<List<FinancingRate>, Exception> {
        val priceQuery = Query.double().required("price")
        val request = Request(GET, "/rates").with(priceQuery of bike.price.value)
        val response = bankHttp(request)

        return if (response.status.successful)
            Success(ratesLens(response))
        else
            Failure(Exception(response.status.description))
    }

    override fun finalize(financingRate: FinancingRate): Result<CreditId, Exception> {
        val response = bankHttp(Request(POST, "/finalize").with(rateLens of financingRate))

        return if (response.status.successful)
            Success(creditIdLens(response))
        else
            Failure(Exception(response.status.description))
    }
}
