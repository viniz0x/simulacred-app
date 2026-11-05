package br.com.simulacred.app.data

import br.com.simulacred.app.domain.CalculadoraLocal
import br.com.simulacred.app.domain.Proposta
import br.com.simulacred.app.domain.Simulacao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.util.UUID

/** Único ponto de acesso às simulações para os ViewModels. */
interface SimulacaoRepository {
    suspend fun simular(proposta: Proposta): Simulacao
    suspend fun buscar(id: String): Simulacao?
}

/**
 * Implementação provisória: calcula no aparelho e guarda em memória.
 * Nos próximos blocos ela dá lugar à versão que tenta a API, cai para a
 * [CalculadoraLocal] quando a API falha e salva no Room.
 */
class SimulacaoRepositoryLocal(
    private val calculadora: CalculadoraLocal = CalculadoraLocal(),
    private val agora: () -> Instant = Instant::now,
    private val novoId: () -> String = { UUID.randomUUID().toString() },
) : SimulacaoRepository {

    private val mutex = Mutex()
    private val simulacoes = mutableMapOf<String, Simulacao>()

    override suspend fun simular(proposta: Proposta): Simulacao {
        val simulacao = Simulacao(
            id = novoId(),
            valor = proposta.valor,
            prazo = proposta.prazo,
            resultado = calculadora.calcular(proposta),
            criadaEm = agora(),
            calculadaNoAparelho = true,
        )
        mutex.withLock { simulacoes[simulacao.id] = simulacao }
        return simulacao
    }

    override suspend fun buscar(id: String): Simulacao? = mutex.withLock { simulacoes[id] }
}
