package br.com.simulacred.app.ui.resultado

import app.cash.turbine.test
import br.com.simulacred.app.MainDispatcherRule
import br.com.simulacred.app.data.SimulacaoRepositoryLocal
import br.com.simulacred.app.domain.Proposta
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class ResultadoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = SimulacaoRepositoryLocal(novoId = { "id-1" })

    @Test
    fun `comeca carregando e mostra a simulacao encontrada`() = runTest {
        val simulacao = repository.simular(Proposta(BigDecimal("5000.00"), 12, BigDecimal("3200.00"), 58))

        ResultadoViewModel("id-1", repository).uiState.test {
            assertEquals(ResultadoUiState.Carregando, awaitItem())
            assertEquals(ResultadoUiState.Encontrada(simulacao), awaitItem())
        }
    }

    @Test
    fun `id que nao existe mais vira NaoEncontrada - caso do processo recriado`() = runTest {
        // Repositório vazio = o que sobra depois que o Android mata o processo.
        val vm = ResultadoViewModel("id-de-antes", repository)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.uiState.value is ResultadoUiState.NaoEncontrada)
    }
}
