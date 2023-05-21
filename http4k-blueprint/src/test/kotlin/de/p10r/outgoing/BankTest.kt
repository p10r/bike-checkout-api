package de.p10r.outgoing

import de.p10r.CreditId
import de.p10r.FinancingRate
import de.p10r.aBike
import de.p10r.aFinancingRate
import de.p10r.fakes.FakeBank
import dev.forkhandles.result4k.Success
import org.http4k.core.Uri
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA

class BankTest {

    @Test
    fun `forwards financing rates`() {
        val bankClient = HttpBankClient(Uri.of("local"), FakeBank())

        expectThat(bankClient.getFinancingRates(aBike))
            .isA<Success<List<FinancingRate>>>()
    }

    @Test
    fun `finalizes a credit`() {
        val bankClient = HttpBankClient(Uri.of("local"), FakeBank())

        expectThat(bankClient.finalize(aFinancingRate))
            .isA<Success<CreditId>>()
    }
}
