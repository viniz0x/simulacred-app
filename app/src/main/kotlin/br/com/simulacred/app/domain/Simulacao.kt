package br.com.simulacred.app.domain

import java.math.BigDecimal
import java.time.Instant

// Modelo de domínio. Dinheiro sempre em BigDecimal com 2 casas: Double perde
// precisão (0.1 + 0.2 != 0.3) e centavos errados num cronograma são bug de negócio.

/** O que o cliente pede. Já validado pela tela (ver ValidadorProposta). */
data class Proposta(
    val valor: BigDecimal,
    val prazo: Int,
    val rendaMensal: BigDecimal,
    val idade: Int,
)

/** Mesmos valores do contrato (seção 2). */
enum class StatusSimulacao { PRE_APROVADA, RECUSADA }

/** Mesmos valores e **mesma ordem** do contrato (seção 2): a ordem é usada na lista de motivos. */
enum class MotivoRecusa {
    VALOR_FORA_DA_FAIXA,
    PRAZO_NAO_PERMITIDO,
    COMPROMETIMENTO_ACIMA_DO_LIMITE,
    IDADE_LIMITE_EXCEDIDA,
}

data class Parcela(
    val numero: Int,
    val valor: BigDecimal,
    val juros: BigDecimal,
    val amortizacao: BigDecimal,
    val saldo: BigDecimal,
)

/**
 * Resultado do cálculo e das regras de elegibilidade. Os campos de cálculo
 * vêm nulos (e [parcelas] vazia) quando o prazo está fora da tabela de taxas.
 */
data class ResultadoCalculo(
    val status: StatusSimulacao,
    val motivosRecusa: List<MotivoRecusa>,
    val taxaMensal: BigDecimal?,
    val valorParcela: BigDecimal?,
    val totalPago: BigDecimal?,
    val totalJuros: BigDecimal?,
    val parcelas: List<Parcela>,
)

/** Uma simulação completa, vinda da API ou calculada no aparelho. */
data class Simulacao(
    val id: String,
    val valor: BigDecimal,
    val prazo: Int,
    val resultado: ResultadoCalculo,
    val criadaEm: Instant,
    val calculadaNoAparelho: Boolean,
)
