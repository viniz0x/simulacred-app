package br.com.simulacred.app.ui.formatacao

import java.math.BigDecimal
import java.math.RoundingMode

// Formatação manual, sem NumberFormat/DecimalFormat:
// - o NumberFormat de moeda do sistema usa espaço não separável entre "R$" e o
//   número, e isso muda entre versões do Android (quebraria testes e textos);
// - o DecimalFormat não é thread-safe, então teria de ser recriado a cada
//   chamada, e esta função roda a cada tecla e em cada linha do cronograma.

/** "R$ 6.205,38". Arredonda para 2 casas com HALF_EVEN, como o resto do app. */
fun formatarMoeda(valor: BigDecimal): String {
    val texto = valor.setScale(2, RoundingMode.HALF_EVEN).abs().toPlainString()
    val inteiros = texto.substringBefore('.')
    val centavos = texto.substringAfter('.')
    val sinal = if (valor.signum() < 0) "-" else ""
    return "${sinal}R$ ${agruparMilhares(inteiros)},$centavos"
}

/** "1234567" → "1.234.567". */
private fun agruparMilhares(digitos: String): String = buildString(digitos.length + digitos.length / 3) {
    digitos.forEachIndexed { indice, digito ->
        if (indice > 0 && (digitos.length - indice) % 3 == 0) append('.')
        append(digito)
    }
}

/** Dígitos digitados no campo de dinheiro (em centavos) → reais. "500000" → 5000.00. */
fun centavosParaReais(digitos: String): BigDecimal? =
    digitos.takeIf { it.isNotEmpty() }?.let { BigDecimal(it).movePointLeft(2).setScale(2) }

/** Texto que o campo de dinheiro mostra: "500000" → "R$ 5.000,00"; vazio continua vazio. */
fun textoCampoMoeda(digitos: String): String = centavosParaReais(digitos)?.let(::formatarMoeda).orEmpty()
