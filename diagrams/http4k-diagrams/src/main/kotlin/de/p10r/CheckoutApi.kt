package de.p10r

import de.p10r.outgoing.Bank
import de.p10r.outgoing.OrderRepository
import de.p10r.outgoing.Warehouse
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success

class CheckoutApi(
    private val bank: Bank,
    private val warehouse: Warehouse,
    private val orderRepository: OrderRepository,
) {
    fun listFinancingRates(bikeId: BikeId): GetFinancingResult {
        val bike = warehouse.getBike(bikeId) ?: return GetFinancingResult.BikeNotFound

        return when (val rates = bank.getFinancingRates(bike)) {
            is Failure -> GetFinancingResult.RatesNotAvailable
            is Success -> GetFinancingResult.Rates(rates.value)
        }
    }

    fun order(
        userId: UserId,
        bikeId: BikeId,
        chosenFinancingRate: FinancingRate,
    ): OrderResult {
        warehouse.getBike(bikeId) ?: return OrderResult.BikeNotInStock

        orderRepository.findBy(userId = userId)
            .exists(bikeId, chosenFinancingRate)
            ?.let { return OrderResult.AlreadyOrdered }

        return when (val creditId = bank.finalize(chosenFinancingRate)) {
            is Failure -> OrderResult.CannotBeFinanced
            is Success -> {
                val newOrder = NewOrder(bikeId, userId, chosenFinancingRate, creditId.value)
                orderRepository.create(newOrder)
                OrderResult.Success(creditId.value)
            }
        }
    }

    private fun List<StoredOrder>.exists(
        bikeId: BikeId,
        chosenFinancingRate: FinancingRate,
    ): StoredOrder? = this.firstOrNull { stored ->
        stored.bikeId == bikeId && stored.financingRate == chosenFinancingRate
    }
}
