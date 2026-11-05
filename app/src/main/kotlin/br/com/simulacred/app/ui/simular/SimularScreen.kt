package br.com.simulacred.app.ui.simular

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.simulacred.app.R
import br.com.simulacred.app.ui.componentes.BotaoGrande
import br.com.simulacred.app.ui.componentes.BotaoGrandeSecundario
import br.com.simulacred.app.ui.componentes.TelaBase
import br.com.simulacred.app.ui.theme.SimulaCredTheme

// Id do exemplo A do contrato (docs/contrato-api.md). Só para navegar enquanto
// o formulário, o ViewModel e o cálculo não existem.
internal const val ID_SIMULACAO_EXEMPLO = "6f1c2a9e-3b7d-4e1a-9c2f-8d5e4b3a2f10"

/** Tela Simular. Esqueleto: o formulário entra no bloco "tela de simulação". */
@Composable
fun SimularScreen(
    onIrParaResultado: (simulacaoId: String) -> Unit,
    onIrParaHistorico: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TelaBase(titulo = stringResource(R.string.titulo_simular), modifier = modifier) {
        Text(
            text = stringResource(R.string.simular_introducao),
            style = MaterialTheme.typography.bodyLarge,
        )
        BotaoGrande(
            texto = stringResource(R.string.acao_simular),
            onClick = { onIrParaResultado(ID_SIMULACAO_EXEMPLO) },
        )
        BotaoGrandeSecundario(
            texto = stringResource(R.string.acao_ver_historico),
            onClick = onIrParaHistorico,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SimularScreenPreview() {
    SimulaCredTheme { SimularScreen(onIrParaResultado = {}, onIrParaHistorico = {}) }
}
