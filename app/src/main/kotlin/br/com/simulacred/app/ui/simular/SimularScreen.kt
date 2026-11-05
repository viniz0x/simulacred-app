package br.com.simulacred.app.ui.simular

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import br.com.simulacred.app.R
import br.com.simulacred.app.ui.componentes.BotaoGrande
import br.com.simulacred.app.ui.componentes.BotaoGrandeSecundario
import br.com.simulacred.app.ui.componentes.CampoMoeda
import br.com.simulacred.app.ui.componentes.CampoNumerico
import br.com.simulacred.app.ui.componentes.TelaBase
import br.com.simulacred.app.ui.theme.SimulaCredTheme

/** Tela Simular ligada ao ViewModel: coleta estado e eventos. */
@Composable
fun SimularScreen(
    viewModel: SimularViewModel,
    onIrParaResultado: (simulacaoId: String) -> Unit,
    onIrParaHistorico: () -> Unit,
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val focos = remember { Campo.entries.associateWith { FocusRequester() } }
    val mensagemErro = stringResource(R.string.simular_erro_inesperado)
    val lifecycleOwner = LocalLifecycleOwner.current
    // O efeito abaixo só reinicia se o ViewModel mudar; sem rememberUpdatedState
    // ele chamaria para sempre o lambda de navegação da primeira composição.
    val irParaResultado by rememberUpdatedState(onIrParaResultado)

    // Eventos só são consumidos com a tela visível (STARTED): navegar com o
    // app em segundo plano pode lançar exceção ou perder o evento.
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventos.collect { evento ->
                when (evento) {
                    is SimularEvento.IrParaResultado -> irParaResultado(evento.simulacaoId)
                    is SimularEvento.FocarCampo -> focos.getValue(evento.campo).requestFocus()
                    SimularEvento.ErroInesperado -> snackbar.showSnackbar(mensagemErro)
                }
            }
        }
    }

    SimularConteudo(
        estado = estado,
        snackbar = snackbar,
        focos = focos,
        onValorChange = viewModel::onValorChange,
        onPrazoChange = viewModel::onPrazoChange,
        onRendaChange = viewModel::onRendaChange,
        onIdadeChange = viewModel::onIdadeChange,
        onSimular = viewModel::onSimular,
        onIrParaHistorico = onIrParaHistorico,
    )
}

/** Versão sem ViewModel: só desenha o estado. Usada no Preview e nos testes de UI. */
@Composable
fun SimularConteudo(
    estado: SimularUiState,
    onValorChange: (String) -> Unit,
    onPrazoChange: (Int) -> Unit,
    onRendaChange: (String) -> Unit,
    onIdadeChange: (String) -> Unit,
    onSimular: () -> Unit,
    onIrParaHistorico: () -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
    focos: Map<Campo, FocusRequester> = remember { Campo.entries.associateWith { FocusRequester() } },
) {
    TelaBase(
        titulo = stringResource(R.string.titulo_simular),
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbar) },
    ) {
        Text(
            text = stringResource(R.string.simular_introducao),
            style = MaterialTheme.typography.bodyLarge,
        )
        CampoMoeda(
            centavos = estado.valor,
            onCentavosChange = onValorChange,
            rotulo = stringResource(R.string.simular_rotulo_valor),
            apoio = stringResource(R.string.simular_apoio_valor),
            erro = estado.erros[Campo.VALOR]?.let { textoDoErro(it) },
            focusRequester = focos.getValue(Campo.VALOR),
        )
        SeletorPrazo(prazoSelecionado = estado.prazo, onPrazoChange = onPrazoChange)
        CampoMoeda(
            centavos = estado.renda,
            onCentavosChange = onRendaChange,
            rotulo = stringResource(R.string.simular_rotulo_renda),
            apoio = stringResource(R.string.simular_apoio_renda),
            erro = estado.erros[Campo.RENDA]?.let { textoDoErro(it) },
            focusRequester = focos.getValue(Campo.RENDA),
        )
        CampoNumerico(
            valor = estado.idade,
            onValorChange = onIdadeChange,
            rotulo = stringResource(R.string.simular_rotulo_idade),
            erro = estado.erros[Campo.IDADE]?.let { textoDoErro(it) },
            imeAction = ImeAction.Done,
            onConcluir = onSimular,
            focusRequester = focos.getValue(Campo.IDADE),
        )
        BotaoGrande(
            texto = stringResource(R.string.acao_simular),
            onClick = onSimular,
            carregando = estado.carregando,
            textoCarregando = stringResource(R.string.simular_calculando),
        )
        BotaoGrandeSecundario(
            texto = stringResource(R.string.acao_ver_historico),
            onClick = onIrParaHistorico,
        )
    }
}

@Composable
private fun textoDoErro(erro: ErroValidacao): String = when (erro) {
    ErroValidacao.OBRIGATORIO -> stringResource(R.string.erro_obrigatorio)
    ErroValidacao.VALOR_ZERO -> stringResource(R.string.erro_valor_zero)
    ErroValidacao.VALOR_ACIMA_DO_MAXIMO -> stringResource(R.string.erro_valor_acima_do_maximo)
    ErroValidacao.IDADE_FORA_DA_FAIXA -> stringResource(R.string.erro_idade, IDADE_MINIMA, IDADE_MAXIMA)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SimularPreenchidaPreview() {
    SimulaCredTheme {
        SimularConteudo(
            estado = SimularUiState(valor = "500000", renda = "320000", idade = "58"),
            onValorChange = {}, onPrazoChange = {}, onRendaChange = {}, onIdadeChange = {},
            onSimular = {}, onIrParaHistorico = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SimularComErrosPreview() {
    SimulaCredTheme {
        SimularConteudo(
            estado = SimularUiState(
                idade = "17",
                erros = mapOf(
                    Campo.VALOR to ErroValidacao.OBRIGATORIO,
                    Campo.RENDA to ErroValidacao.OBRIGATORIO,
                    Campo.IDADE to ErroValidacao.IDADE_FORA_DA_FAIXA,
                ),
            ),
            onValorChange = {}, onPrazoChange = {}, onRendaChange = {}, onIdadeChange = {},
            onSimular = {}, onIrParaHistorico = {},
        )
    }
}
