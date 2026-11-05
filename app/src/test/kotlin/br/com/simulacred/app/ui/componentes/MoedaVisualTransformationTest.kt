package br.com.simulacred.app.ui.componentes

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * O Compose confere o OffsetMapping a cada tecla e derruba o app se um offset
 * sair do texto. Este teste pega isso na JVM, sem emulador.
 */
class MoedaVisualTransformationTest {

    private fun transformar(digitos: String) = MoedaVisualTransformation.filter(AnnotatedString(digitos))

    @Test
    fun `mostra os centavos como reais`() {
        assertEquals("R$ 5.000,00", transformar("500000").text.text)
        assertEquals("R$ 0,07", transformar("7").text.text)
        assertEquals("", transformar("").text.text)
    }

    @Test
    fun `todo offset fica dentro do texto e o cursor vai para o fim`() {
        listOf("", "7", "500000", "999999999").forEach { digitos ->
            val transformado = transformar(digitos)
            val formatado = transformado.text.text

            (0..digitos.length).forEach { offset ->
                assertEquals(formatado.length, transformado.offsetMapping.originalToTransformed(offset))
            }
            (0..formatado.length).forEach { offset ->
                assertEquals(digitos.length, transformado.offsetMapping.transformedToOriginal(offset))
            }
        }
    }
}
