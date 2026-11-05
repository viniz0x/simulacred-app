package br.com.simulacred.app.ui.simular

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import br.com.simulacred.app.MainDispatcherRule
import br.com.simulacred.app.data.SimulacaoRepository
import br.com.simulacred.app.domain.CalculadoraLocal
import br.com.simulacred.app.domain.Proposta
import br.com.simulacred.app.domain.Simulacao
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class SimularViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSimulacaoRepository()

    private fun criarViewModel(handle: SavedStateHandle = SavedStateHandle()) =
        SimularViewModel(handle, repository)

    private fun SimularViewModel.preencherExemploA() {
        onValorChange("500000") // R$ 5.000,00 em centavos
        onPrazoChange(12)
        onRendaChange("320000") // R$ 3.200,00
        onIdadeChange("58")
    }

    @Test
    fun `campos vazios geram erro de obrigatorio e nao chamam o repositorio`() = runTest {
        val vm = criarViewModel()

        vm.onSimular()
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            mapOf(
                Campo.VALOR to ErroValidacao.OBRIGATORIO,
                Campo.RENDA to ErroValidacao.OBRIGATORIO,
                Campo.IDADE to ErroValidacao.OBRIGATORIO,
            ),
            vm.uiState.value.erros,
        )
        assertTrue(repository.propostasRecebidas.isEmpty())
    }

    @Test
    fun `valores fora dos limites do contrato geram o erro de cada campo`() = runTest {
        val vm = criarViewModel()
        vm.onValorChange("100000001") // R$ 1.000.000,01
        vm.onRendaChange("0") // zeros à esquerda somem: "0" vira campo vazio
        vm.onIdadeChange("17")

        vm.onSimular()
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            mapOf(
                Campo.VALOR to ErroValidacao.VALOR_ACIMA_DO_MAXIMO,
                Campo.RENDA to ErroValidacao.OBRIGATORIO,
                Campo.IDADE to ErroValidacao.IDADE_FORA_DA_FAIXA,
            ),
            vm.uiState.value.erros,
        )
    }

    @Test
    fun `validacao - limites exatos do contrato`() {
        val valido = SimularUiState(valor = "100000000", renda = "1", idade = "18")
        assertTrue(valido.validar().isEmpty()) // R$ 1.000.000,00 e R$ 0,01 passam
        assertTrue(valido.copy(idade = "100").validar().isEmpty())
        assertEquals(
            mapOf(Campo.VALOR to ErroValidacao.VALOR_ZERO, Campo.IDADE to ErroValidacao.IDADE_FORA_DA_FAIXA),
            valido.copy(valor = "000", idade = "101").validar(),
        )
    }

    @Test
    fun `valor fora da faixa de credito e valido - quem recusa e a regra de elegibilidade`() = runTest {
        val vm = criarViewModel()
        vm.preencherExemploA()
        vm.onValorChange("4000000") // R$ 40.000,00

        vm.onSimular()
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.uiState.value.erros.isEmpty())
        assertEquals(BigDecimal("40000.00"), repository.propostasRecebidas.single().valor)
    }

    @Test
    fun `editar um campo limpa so o erro dele`() = runTest {
        val vm = criarViewModel()
        vm.onSimular()
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        vm.onIdadeChange("58")

        assertNull(vm.uiState.value.erros[Campo.IDADE])
        assertEquals(ErroValidacao.OBRIGATORIO, vm.uiState.value.erros[Campo.VALOR])
    }

    @Test
    fun `campos aceitam so digitos e respeitam o tamanho maximo`() {
        val vm = criarViewModel()

        vm.onValorChange("00R$ 5.000,00")
        vm.onIdadeChange("5a8 9")

        assertEquals("500000", vm.uiState.value.valor)
        assertEquals("589", vm.uiState.value.idade)
        vm.onIdadeChange("1234")
        assertEquals("589", vm.uiState.value.idade) // passou de 3 dígitos: ignora
    }

    @Test
    fun `proposta enviada converte centavos em BigDecimal com 2 casas`() = runTest {
        val vm = criarViewModel()
        vm.preencherExemploA()

        vm.onSimular()
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            Proposta(BigDecimal("5000.00"), 12, BigDecimal("3200.00"), 58),
            repository.propostasRecebidas.single(),
        )
    }

    @Test
    fun `mostra carregando durante o calculo e depois volta ao normal`() = runTest {
        repository.segurarResposta = CompletableDeferred()
        val vm = criarViewModel()
        vm.preencherExemploA()

        vm.uiState.test {
            assertFalse(awaitItem().carregando)
            vm.onSimular()
            assertTrue(awaitItem().carregando)
            repository.segurarResposta!!.complete(Unit)
            assertFalse(awaitItem().carregando)
        }
    }

    @Test
    fun `apos simular envia evento para ir ao resultado com o id`() = runTest {
        val vm = criarViewModel()
        vm.preencherExemploA()

        vm.eventos.test {
            vm.onSimular()
            assertEquals(SimularEvento.IrParaResultado(FakeSimulacaoRepository.ID), awaitItem())
        }
    }

    @Test
    fun `falha inesperada envia evento de erro e libera o botao`() = runTest {
        repository.falhar = true
        val vm = criarViewModel()
        vm.preencherExemploA()

        vm.eventos.test {
            vm.onSimular()
            assertEquals(SimularEvento.ErroInesperado, awaitItem())
        }
        assertFalse(vm.uiState.value.carregando)
    }

    @Test
    fun `segundo toque durante o carregamento e ignorado`() = runTest {
        repository.segurarResposta = CompletableDeferred()
        val vm = criarViewModel()
        vm.preencherExemploA()

        vm.onSimular()
        mainDispatcherRule.dispatcher.scheduler.runCurrent()
        vm.onSimular()
        repository.segurarResposta!!.complete(Unit)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, repository.propostasRecebidas.size)
    }

    @Test
    fun `campos sao restaurados do SavedStateHandle apos a morte do processo`() {
        val handle = SavedStateHandle()
        criarViewModel(handle).preencherExemploA()

        // Novo ViewModel com o mesmo handle = processo recriado pelo Android.
        val restaurado = criarViewModel(handle).uiState.value

        assertEquals("500000", restaurado.valor)
        assertEquals(12, restaurado.prazo)
        assertEquals("320000", restaurado.renda)
        assertEquals("58", restaurado.idade)
    }
}

private class FakeSimulacaoRepository : SimulacaoRepository {
    val propostasRecebidas = mutableListOf<Proposta>()
    var segurarResposta: CompletableDeferred<Unit>? = null
    var falhar = false

    override suspend fun simular(proposta: Proposta): Simulacao {
        propostasRecebidas += proposta
        segurarResposta?.await()
        if (falhar) error("falha simulada")
        return Simulacao(
            id = ID,
            valor = proposta.valor,
            prazo = proposta.prazo,
            resultado = CalculadoraLocal().calcular(proposta),
            criadaEm = Instant.parse("2026-11-04T23:15:42Z"),
            calculadaNoAparelho = true,
        )
    }

    override suspend fun buscar(id: String): Simulacao? = null

    companion object {
        const val ID = "6f1c2a9e-3b7d-4e1a-9c2f-8d5e4b3a2f10"
    }
}
