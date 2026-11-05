package br.com.simulacred.app.data

import br.com.simulacred.app.domain.Proposta
import br.com.simulacred.app.domain.StatusSimulacao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class SimulacaoRepositoryLocalTest {

    private val agora = Instant.parse("2026-11-04T23:15:42Z")
    private var proximoId = 0
    private val repository = SimulacaoRepositoryLocal(
        agora = { agora },
        novoId = { "id-${++proximoId}" },
    )
    private val proposta = Proposta(BigDecimal("5000.00"), 12, BigDecimal("3200.00"), 58)

    @Test
    fun `simular calcula no aparelho, marca a origem e preenche id e data`() = runTest {
        val simulacao = repository.simular(proposta)

        assertEquals("id-1", simulacao.id)
        assertEquals(agora, simulacao.criadaEm)
        assertTrue(simulacao.calculadaNoAparelho)
        assertEquals(BigDecimal("5000.00"), simulacao.valor)
        assertEquals(StatusSimulacao.PRE_APROVADA, simulacao.resultado.status)
        assertEquals(BigDecimal("517.12"), simulacao.resultado.valorParcela)
    }

    @Test
    fun `buscar devolve a simulacao salva pelo id`() = runTest {
        val primeira = repository.simular(proposta)
        val segunda = repository.simular(proposta.copy(prazo = 24))

        assertEquals(primeira, repository.buscar("id-1"))
        assertEquals(segunda, repository.buscar("id-2"))
    }

    @Test
    fun `buscar id inexistente devolve null`() = runTest {
        assertNull(repository.buscar("nao-existe"))
    }
}
