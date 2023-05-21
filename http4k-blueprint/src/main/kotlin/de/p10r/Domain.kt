package de.p10r

import java.time.Instant

//this would usually be in separate files and would also include DTOs for the domain objects

data class Bike(
    val id: BikeId,
    val modelName: Model,
    val price: Price,
)

data class NewOrder(
    val bikeId: BikeId,
    val userId: UserId,
    val financingRate: FinancingRate,
    val creditId: CreditId,
)

fun NewOrder.toStoredOrder(
    id: OrderId,
    createdAt: Instant,
) = StoredOrder(
    orderId = id,
    bikeId = bikeId,
    userId = userId,
    financingRate = financingRate,
    createdAt = createdAt,
)

data class StoredOrder(
    val orderId: OrderId,
    val bikeId: BikeId,
    val userId: UserId,
    val financingRate: FinancingRate,
    val createdAt: Instant,
)

data class FinancingRate(
    val interestRate: InterestRate,
    val term: Term,
)

sealed interface OrderResult {
    object BikeNotInStock : OrderResult
    data class Success(val creditId: CreditId) : OrderResult

    object CannotBeFinanced : OrderResult

    object AlreadyOrdered : OrderResult

    object ServerError : OrderResult
}

sealed interface GetFinancingResult {
    object BikeNotFound : GetFinancingResult
    data class Rates(val rates: List<FinancingRate>) : GetFinancingResult

    object RatesNotAvailable : GetFinancingResult
}

data class OrderBikeRequest(
    val userId: String,
    val financingRate: FinancingRate,
)
