package br.com.simulacred.app.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Cálculo da Tabela Price e regras de elegibilidade, idênticos ao backend
 * (docs/contrato-api.md, seção 3.1). Usado quando a API não responde, para o
 * cliente não ficar sem resposta. Não depende de Android: roda em teste JVM.
 */
class CalculadoraLocal {

    fun calcular(proposta: Proposta): ResultadoCalculo {
        val taxa = taxaPara(proposta.prazo)
        val cronograma = taxa?.let { cronogramaPrice(proposta.valor, it, proposta.prazo) }
        val valorParcela = cronograma?.first()?.valor
        val totalPago = cronograma?.fold(BigDecimal.ZERO) { soma, p -> soma + p.valor }

        val motivos = buildList {
            if (proposta.valor < VALOR_MINIMO || proposta.valor > VALOR_MAXIMO) {
                add(MotivoRecusa.VALOR_FORA_DA_FAIXA)
            }
            if (taxa == null) add(MotivoRecusa.PRAZO_NAO_PERMITIDO)
            // Sem parcela (prazo fora da tabela) não há comprometimento para avaliar.
            if (valorParcela != null && valorParcela > proposta.rendaMensal * LIMITE_COMPROMETIMENTO) {
                add(MotivoRecusa.COMPROMETIMENTO_ACIMA_DO_LIMITE)
            }
            // idade + prazo/12 > 80, em meses para não arredondar: idade*12 + prazo > 960.
            if (proposta.idade * 12 + proposta.prazo > IDADE_LIMITE * 12) {
                add(MotivoRecusa.IDADE_LIMITE_EXCEDIDA)
            }
        }

        return ResultadoCalculo(
            status = if (motivos.isEmpty()) StatusSimulacao.PRE_APROVADA else StatusSimulacao.RECUSADA,
            motivosRecusa = motivos,
            taxaMensal = taxa,
            valorParcela = valorParcela,
            totalPago = totalPago,
            totalJuros = totalPago?.minus(proposta.valor),
            parcelas = cronograma.orEmpty(),
        )
    }

    /** Tabela de taxas fictícia do contrato; `null` fora de 6 a 36 meses. */
    private fun taxaPara(prazo: Int): BigDecimal? = when (prazo) {
        in 6..12 -> BigDecimal("0.0349")
        in 13..24 -> BigDecimal("0.0399")
        in 25..36 -> BigDecimal("0.0449")
        else -> null
    }

    /**
     * valorParcela = valor × i / (1 − (1 + i)^−n), arredondado com HALF_EVEN.
     * Da 1ª à penúltima: juros = saldo × i (arredondado) e amortização = parcela − juros.
     * Na última, a amortização é o saldo restante, para o saldo fechar em 0,00.
     */
    private fun cronogramaPrice(valor: BigDecimal, taxa: BigDecimal, prazo: Int): List<Parcela> {
        val fator = BigDecimal.ONE.divide((BigDecimal.ONE + taxa).pow(prazo, MC), MC)
        val valorParcela = dinheiro((valor * taxa).divide(BigDecimal.ONE - fator, MC))

        var saldo = dinheiro(valor)
        return (1..prazo).map { numero ->
            val juros = dinheiro(saldo * taxa)
            val ultima = numero == prazo
            val amortizacao = if (ultima) saldo else valorParcela - juros
            saldo -= amortizacao
            Parcela(
                numero = numero,
                valor = if (ultima) amortizacao + juros else valorParcela,
                juros = juros,
                amortizacao = amortizacao,
                saldo = saldo,
            )
        }
    }

    private fun dinheiro(valor: BigDecimal) = valor.setScale(2, RoundingMode.HALF_EVEN)

    private companion object {
        // 34 dígitos (IEEE 754 decimal128): sobra precisão antes do arredondamento final.
        val MC: MathContext = MathContext.DECIMAL128
        val VALOR_MINIMO = BigDecimal("500.00")
        val VALOR_MAXIMO = BigDecimal("30000.00")
        val LIMITE_COMPROMETIMENTO = BigDecimal("0.30")
        const val IDADE_LIMITE = 80
    }
}
