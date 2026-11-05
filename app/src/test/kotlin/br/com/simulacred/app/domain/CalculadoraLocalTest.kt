package br.com.simulacred.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

/**
 * Casos do contrato (docs/contrato-api.md, seção 3.1). Os valores do exemplo A
 * são obrigatórios nos testes do app e do backend: os dois lados precisam
 * produzir exatamente os mesmos números.
 */
class CalculadoraLocalTest {

    private val calculadora = CalculadoraLocal()

    // Exemplo A: R$ 5.000,00 em 12x, renda R$ 3.200,00, 58 anos.
    private val propostaA = Proposta(
        valor = BigDecimal("5000.00"),
        prazo = 12,
        rendaMensal = BigDecimal("3200.00"),
        idade = 58,
    )

    @Test
    fun `exemplo A - pre-aprovada com os valores do contrato`() {
        val r = calculadora.calcular(propostaA)

        assertEquals(StatusSimulacao.PRE_APROVADA, r.status)
        assertEquals(emptyList<MotivoRecusa>(), r.motivosRecusa)
        assertEquals(BigDecimal("0.0349"), r.taxaMensal)
        assertEquals(BigDecimal("517.12"), r.valorParcela)
        assertEquals(BigDecimal("6205.38"), r.totalPago)
        assertEquals(BigDecimal("1205.38"), r.totalJuros)
    }

    @Test
    fun `exemplo A - parcelas 1, 2 e 12 do cronograma`() {
        val parcelas = calculadora.calcular(propostaA).parcelas

        assertEquals(12, parcelas.size)
        assertEquals(parcela(1, "517.12", "174.50", "342.62", "4657.38"), parcelas[0])
        assertEquals(parcela(2, "517.12", "162.54", "354.58", "4302.80"), parcelas[1])
        assertEquals(parcela(12, "517.06", "17.44", "499.62", "0.00"), parcelas[11])
    }

    @Test
    fun `exemplo A - cronograma fecha e soma o total pago`() {
        val r = calculadora.calcular(propostaA)

        assertEquals((1..12).toList(), r.parcelas.map { it.numero })
        assertEquals(r.totalPago, r.parcelas.fold(BigDecimal.ZERO) { acc, p -> acc + p.valor })
        assertEquals(propostaA.valor, r.parcelas.fold(BigDecimal.ZERO) { acc, p -> acc + p.amortizacao })
        r.parcelas.forEach { p ->
            assertEquals(2, p.valor.scale())
            assertEquals(p.valor, p.juros + p.amortizacao)
        }
    }

    @Test
    fun `exemplo B - recusada por comprometimento de renda, com o calculo completo`() {
        val r = calculadora.calcular(propostaA.copy(rendaMensal = BigDecimal("1500.00")))

        assertEquals(StatusSimulacao.RECUSADA, r.status)
        assertEquals(listOf(MotivoRecusa.COMPROMETIMENTO_ACIMA_DO_LIMITE), r.motivosRecusa)
        assertEquals(BigDecimal("517.12"), r.valorParcela)
        assertEquals(12, r.parcelas.size)
    }

    @Test
    fun `exemplo C - prazo fora da tabela zera o calculo e lista motivos na ordem do contrato`() {
        val r = calculadora.calcular(propostaA.copy(valor = BigDecimal("40000.00"), prazo = 48))

        assertEquals(StatusSimulacao.RECUSADA, r.status)
        assertEquals(
            listOf(MotivoRecusa.VALOR_FORA_DA_FAIXA, MotivoRecusa.PRAZO_NAO_PERMITIDO),
            r.motivosRecusa,
        )
        assertNull(r.taxaMensal)
        assertNull(r.valorParcela)
        assertNull(r.totalPago)
        assertNull(r.totalJuros)
        assertTrue(r.parcelas.isEmpty())
    }

    @Test
    fun `prazo fora da tabela nao avalia comprometimento, mas avalia idade`() {
        // Renda baixíssima: se o comprometimento fosse avaliado, entraria na lista.
        val r = calculadora.calcular(
            propostaA.copy(prazo = 48, rendaMensal = BigDecimal("1.00"), idade = 77),
        )

        assertEquals(
            listOf(MotivoRecusa.PRAZO_NAO_PERMITIDO, MotivoRecusa.IDADE_LIMITE_EXCEDIDA),
            r.motivosRecusa,
        )
    }

    @Test
    fun `todos os motivos aparecem juntos e na ordem da tabela`() {
        val r = calculadora.calcular(
            Proposta(BigDecimal("400.00"), prazo = 36, rendaMensal = BigDecimal("50.00"), idade = 79),
        )

        assertEquals(
            listOf(
                MotivoRecusa.VALOR_FORA_DA_FAIXA,
                MotivoRecusa.COMPROMETIMENTO_ACIMA_DO_LIMITE,
                MotivoRecusa.IDADE_LIMITE_EXCEDIDA,
            ),
            r.motivosRecusa,
        )
    }

    @Test
    fun `faixa de valor - 500 e 30000 sao aceitos, fora disso recusa`() {
        fun motivos(valor: String) = calculadora.calcular(
            propostaA.copy(valor = BigDecimal(valor), rendaMensal = BigDecimal("100000.00")),
        ).motivosRecusa

        assertEquals(emptyList<MotivoRecusa>(), motivos("500.00"))
        assertEquals(emptyList<MotivoRecusa>(), motivos("30000.00"))
        assertEquals(listOf(MotivoRecusa.VALOR_FORA_DA_FAIXA), motivos("499.99"))
        assertEquals(listOf(MotivoRecusa.VALOR_FORA_DA_FAIXA), motivos("30000.01"))
    }

    @Test
    fun `tabela de taxas por faixa de prazo`() {
        fun taxa(prazo: Int) = calculadora.calcular(propostaA.copy(prazo = prazo)).taxaMensal

        assertNull(taxa(5))
        assertEquals(BigDecimal("0.0349"), taxa(6))
        assertEquals(BigDecimal("0.0349"), taxa(12))
        assertEquals(BigDecimal("0.0399"), taxa(13))
        assertEquals(BigDecimal("0.0399"), taxa(24))
        assertEquals(BigDecimal("0.0449"), taxa(25))
        assertEquals(BigDecimal("0.0449"), taxa(36))
        assertNull(taxa(37))
    }

    @Test
    fun `comprometimento - recusa so quando a parcela passa de 30 por cento da renda`() {
        // 30% de 1723,73 = 517,119 → a parcela de 517,12 passa do limite.
        // 30% de 1723,74 = 517,122 → a parcela fica dentro.
        fun motivos(renda: String) =
            calculadora.calcular(propostaA.copy(rendaMensal = BigDecimal(renda))).motivosRecusa

        assertEquals(listOf(MotivoRecusa.COMPROMETIMENTO_ACIMA_DO_LIMITE), motivos("1723.73"))
        assertEquals(emptyList<MotivoRecusa>(), motivos("1723.74"))
    }

    @Test
    fun `limite de idade - termina exatamente aos 80 passa, depois disso recusa`() {
        // 68 anos + 12 meses = 69; 79 + 12 meses = 80 (no limite); 79 + 13 meses = 80,08.
        fun motivos(idade: Int, prazo: Int) =
            calculadora.calcular(propostaA.copy(idade = idade, prazo = prazo)).motivosRecusa

        assertEquals(emptyList<MotivoRecusa>(), motivos(79, 12))
        assertEquals(listOf(MotivoRecusa.IDADE_LIMITE_EXCEDIDA), motivos(79, 13))
    }

    private fun parcela(numero: Int, valor: String, juros: String, amortizacao: String, saldo: String) =
        Parcela(numero, BigDecimal(valor), BigDecimal(juros), BigDecimal(amortizacao), BigDecimal(saldo))
}
