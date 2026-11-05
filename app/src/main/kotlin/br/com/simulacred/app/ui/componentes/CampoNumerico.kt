package br.com.simulacred.app.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import br.com.simulacred.app.ui.formatacao.textoCampoMoeda

/**
 * Campo só de números, com teclado numérico. Rótulo sempre visível (não some ao
 * digitar), texto de apoio abaixo e erro anunciado pelo TalkBack.
 */
@Composable
fun CampoNumerico(
    valor: String,
    onValorChange: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    apoio: String? = null,
    erro: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onConcluir: () -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(rotulo) },
        supportingText = (erro ?: apoio)?.let { texto -> { Text(texto) } },
        isError = erro != null,
        singleLine = true,
        textStyle = MaterialTheme.typography.titleMedium,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onConcluir() }),
        modifier = modifier
            .fillMaxWidth()
            .then(if (erro != null) Modifier.semantics { error(erro) } else Modifier),
    )
}

/** Campo de dinheiro: guarda centavos ("500000") e mostra "R$ 5.000,00". */
@Composable
fun CampoMoeda(
    centavos: String,
    onCentavosChange: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    apoio: String? = null,
    erro: String? = null,
    imeAction: ImeAction = ImeAction.Next,
) {
    CampoNumerico(
        valor = centavos,
        onValorChange = onCentavosChange,
        rotulo = rotulo,
        modifier = modifier,
        apoio = apoio,
        erro = erro,
        imeAction = imeAction,
        visualTransformation = MoedaVisualTransformation,
    )
}

/**
 * Mostra os centavos digitados como reais. O cursor fica sempre no fim:
 * os números entram da direita, como em caixa eletrônico.
 */
private object MoedaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatado = textoCampoMoeda(text.text)
        val mapeamento = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = formatado.length
            override fun transformedToOriginal(offset: Int) = text.length
        }
        return TransformedText(AnnotatedString(formatado), mapeamento)
    }
}
