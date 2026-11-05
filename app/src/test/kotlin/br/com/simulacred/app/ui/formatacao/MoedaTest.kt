package br.com.simulacred.app.ui.formatacao

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class MoedaTest {

    @Test
    fun `formata no padrao brasileiro com prefixo R$`() {
        assertEquals("R$ 517,12", formatarMoeda(BigDecimal("517.12")))
        assertEquals("R$ 6.205,38", formatarMoeda(BigDecimal("6205.38")))
        assertEquals("R$ 1.000.000,00", formatarMoeda(BigDecimal("1000000")))
        assertEquals("R$ 0,05", formatarMoeda(BigDecimal("0.05")))
        assertEquals("R$ 999,00", formatarMoeda(BigDecimal("999")))
        assertEquals("R$ 100.000,00", formatarMoeda(BigDecimal("100000")))
    }

    @Test
    fun `arredonda com HALF_EVEN e mantem o sinal`() {
        assertEquals("R$ 0,12", formatarMoeda(BigDecimal("0.125"))) // empate: vai para o par
        assertEquals("R$ 0,14", formatarMoeda(BigDecimal("0.135")))
        assertEquals("-R$ 1.205,38", formatarMoeda(BigDecimal("-1205.38")))
    }

    @Test
    fun `converte digitos de centavos em BigDecimal com 2 casas`() {
        assertEquals(BigDecimal("5000.00"), centavosParaReais("500000"))
        assertEquals(BigDecimal("0.07"), centavosParaReais("7"))
        assertEquals(null, centavosParaReais(""))
    }

    @Test
    fun `campo de dinheiro mostra os centavos digitados como reais`() {
        assertEquals("R$ 5.000,00", textoCampoMoeda("500000"))
        assertEquals("R$ 0,07", textoCampoMoeda("7"))
        assertEquals("", textoCampoMoeda(""))
    }
}
