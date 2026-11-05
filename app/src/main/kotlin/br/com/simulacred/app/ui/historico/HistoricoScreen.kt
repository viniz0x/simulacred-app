package br.com.simulacred.app.ui.historico

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.simulacred.app.R
import br.com.simulacred.app.ui.componentes.TelaBase
import br.com.simulacred.app.ui.theme.SimulaCredTheme

/**
 * Tela Histórico. Esqueleto com o estado vazio: a lista vinda do Room entra no
 * bloco "histórico offline". [onAbrirSimulacao] já fica ligado à navegação.
 */
@Composable
fun HistoricoScreen(
    onAbrirSimulacao: (simulacaoId: String) -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TelaBase(
        titulo = stringResource(R.string.titulo_historico),
        onVoltar = onVoltar,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.historico_vazio),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistoricoScreenPreview() {
    SimulaCredTheme { HistoricoScreen(onAbrirSimulacao = {}, onVoltar = {}) }
}
