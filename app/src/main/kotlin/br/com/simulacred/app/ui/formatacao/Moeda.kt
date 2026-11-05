package br.com.simulacred.app.ui.formatacao

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

// Formatação manual em vez de NumberFormat.getCurrencyInstance: a versão do
// sistema usa espaço não separável entre "R$" e o número, e isso muda entre
// versões do Android, o que quebraria testes e comparação de textos.

private val simbolosBr = DecimalFormatSymbols(Locale.forLanguageTag("pt-BR"))

/** "R$ 6.205,38". Cria o DecimalFormat a cada chamada: ele não é thread-safe. */
fun formatarMoeda(valor: BigDecimal): String {
    val formato = DecimalFormat("#,##0.00", simbolosBr).apply { roundingMode = RoundingMode.HALF_EVEN }
    return "R$ " + formato.format(valor)
}

/** Dígitos digitados no campo de dinheiro (em centavos) → reais. "500000" → 5000.00. */
fun centavosParaReais(digitos: String): BigDecimal? =
    digitos.takeIf { it.isNotEmpty() }?.let { BigDecimal(it).movePointLeft(2).setScale(2) }

/** Texto que o campo de dinheiro mostra: "500000" → "R$ 5.000,00"; vazio continua vazio. */
fun textoCampoMoeda(digitos: String): String = centavosParaReais(digitos)?.let(::formatarMoeda).orEmpty()
