package br.com.simulacred.app.ui.resultado

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.simulacred.app.R
import br.com.simulacred.app.ui.componentes.BotaoGrandeSecundario
import br.com.simulacred.app.ui.componentes.TelaBase
import br.com.simulacred.app.ui.theme.SimulaCredTheme

/** Tela Resultado. Esqueleto: status, parcela e cronograma entram no bloco "tela de resultado". */
@Composable
fun ResultadoScreen(
    simulacaoId: String,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TelaBase(
        titulo = stringResource(R.string.titulo_resultado),
        onVoltar = onVoltar,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.resultado_em_construcao),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.resultado_codigo, simulacaoId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        BotaoGrandeSecundario(texto = stringResource(R.string.acao_nova_simulacao), onClick = onVoltar)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResultadoScreenPreview() {
    SimulaCredTheme { ResultadoScreen(simulacaoId = "6f1c2a9e-3b7d-4e1a-9c2f-8d5e4b3a2f10", onVoltar = {}) }
}
