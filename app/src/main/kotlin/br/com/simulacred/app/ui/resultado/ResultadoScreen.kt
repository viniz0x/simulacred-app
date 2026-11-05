package br.com.simulacred.app.ui.resultado

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.simulacred.app.R
import br.com.simulacred.app.ui.componentes.BotaoGrande
import br.com.simulacred.app.ui.componentes.BotaoGrandeSecundario
import br.com.simulacred.app.ui.componentes.TelaBase
import br.com.simulacred.app.ui.theme.SimulaCredTheme

/** Tela Resultado ligada ao ViewModel. */
@Composable
fun ResultadoScreen(
    viewModel: ResultadoViewModel,
    onVoltar: () -> Unit,
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    ResultadoConteudo(estado = estado, onVoltar = onVoltar)
}

/**
 * Esqueleto: status, parcela em destaque, totais e cronograma entram no
 * bloco "tela de resultado". Por ora só mostra o código e trata o não encontrado.
 */
@Composable
fun ResultadoConteudo(
    estado: ResultadoUiState,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TelaBase(
        titulo = stringResource(R.string.titulo_resultado),
        onVoltar = onVoltar,
        modifier = modifier,
    ) {
        when (estado) {
            ResultadoUiState.Carregando -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
            ) {
                CircularProgressIndicator()
            }

            is ResultadoUiState.Encontrada -> {
                Text(
                    text = stringResource(R.string.resultado_em_construcao),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.resultado_codigo, estado.simulacao.id),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                BotaoGrandeSecundario(texto = stringResource(R.string.acao_nova_simulacao), onClick = onVoltar)
            }

            ResultadoUiState.NaoEncontrada -> {
                Text(
                    text = stringResource(R.string.resultado_nao_encontrada),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
                Text(
                    text = stringResource(R.string.resultado_nao_encontrada_explicacao),
                    style = MaterialTheme.typography.bodyLarge,
                )
                BotaoGrande(texto = stringResource(R.string.acao_voltar), onClick = onVoltar)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResultadoNaoEncontradaPreview() {
    SimulaCredTheme { ResultadoConteudo(estado = ResultadoUiState.NaoEncontrada, onVoltar = {}) }
}
