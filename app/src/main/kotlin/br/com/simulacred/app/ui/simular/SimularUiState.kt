package br.com.simulacred.app.ui.simular

import br.com.simulacred.app.domain.Proposta
import br.com.simulacred.app.ui.formatacao.centavosParaReais
import java.math.BigDecimal

/** Prazos oferecidos no seletor, todos dentro da tabela de taxas (6 a 36 meses). */
val PRAZOS_DISPONIVEIS = listOf(6, 12, 18, 24, 30, 36)

/**
 * Estado da tela Simular. Valor e renda guardam só os dígitos em centavos
 * ("500000" = R$ 5.000,00): o campo funciona como caixa eletrônico, o cliente
 * só digita números e nunca precisa escolher entre vírgula e ponto.
 */
data class SimularUiState(
    val valor: String = "",
    val prazo: Int = 12,
    val renda: String = "",
    val idade: String = "",
    val erros: Map<Campo, ErroValidacao> = emptyMap(),
    val carregando: Boolean = false,
)

enum class Campo { VALOR, RENDA, IDADE }

/** Código do erro; o texto fica em strings.xml (o ViewModel não conhece Context). */
enum class ErroValidacao { OBRIGATORIO, VALOR_ZERO, VALOR_ACIMA_DO_MAXIMO, IDADE_FORA_DA_FAIXA }

sealed interface SimularEvento {
    data class IrParaResultado(val simulacaoId: String) : SimularEvento
    data object ErroInesperado : SimularEvento
}

/**
 * Mesmas regras de validação da API (contrato, seção 3.1), para o app não
 * mandar nada que volte com 400. Validação não é elegibilidade: R$ 40.000 é
 * válido aqui e é recusado depois pela regra VALOR_FORA_DA_FAIXA.
 */
internal fun SimularUiState.validar(): Map<Campo, ErroValidacao> = buildMap {
    when (val valor = centavosParaReais(valor)) {
        null -> put(Campo.VALOR, ErroValidacao.OBRIGATORIO)
        else -> when {
            valor.signum() <= 0 -> put(Campo.VALOR, ErroValidacao.VALOR_ZERO)
            valor > VALOR_MAXIMO -> put(Campo.VALOR, ErroValidacao.VALOR_ACIMA_DO_MAXIMO)
        }
    }
    when (val renda = centavosParaReais(renda)) {
        null -> put(Campo.RENDA, ErroValidacao.OBRIGATORIO)
        else -> if (renda.signum() <= 0) put(Campo.RENDA, ErroValidacao.VALOR_ZERO)
    }
    when (val idade = idade.toIntOrNull()) {
        null -> put(Campo.IDADE, ErroValidacao.OBRIGATORIO)
        !in IDADE_MINIMA..IDADE_MAXIMA -> put(Campo.IDADE, ErroValidacao.IDADE_FORA_DA_FAIXA)
    }
}

/** Chamar só depois de [validar] devolver vazio. */
internal fun SimularUiState.toProposta() = Proposta(
    valor = checkNotNull(centavosParaReais(valor)),
    prazo = prazo,
    rendaMensal = checkNotNull(centavosParaReais(renda)),
    idade = idade.toInt(),
)

private val VALOR_MAXIMO = BigDecimal("1000000.00")
internal const val IDADE_MINIMA = 18
internal const val IDADE_MAXIMA = 100
