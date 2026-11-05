package br.com.simulacred.app.ui.simular

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.simulacred.app.data.SimulacaoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * - [uiState] (StateFlow): o estado atual da tela. Sobrevive à rotação porque o
 *   ViewModel sobrevive; os campos também vão para o [SavedStateHandle], que
 *   cobre a morte do processo quando o app fica em segundo plano.
 * - [eventos] (Channel): o que deve acontecer uma vez só (navegar, avisar erro).
 *   Num StateFlow, a navegação se repetiria a cada recomposição ou rotação.
 */
class SimularViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: SimulacaoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SimularUiState(
            valor = savedStateHandle[CHAVE_VALOR] ?: "",
            prazo = savedStateHandle[CHAVE_PRAZO] ?: 12,
            renda = savedStateHandle[CHAVE_RENDA] ?: "",
            idade = savedStateHandle[CHAVE_IDADE] ?: "",
        ),
    )
    val uiState: StateFlow<SimularUiState> = _uiState.asStateFlow()

    private val _eventos = Channel<SimularEvento>(Channel.BUFFERED)
    val eventos: Flow<SimularEvento> = _eventos.receiveAsFlow()

    fun onValorChange(texto: String) = atualizarDigitos(texto, MAX_DIGITOS_DINHEIRO, Campo.VALOR, CHAVE_VALOR) {
        copy(valor = it)
    }

    fun onRendaChange(texto: String) = atualizarDigitos(texto, MAX_DIGITOS_DINHEIRO, Campo.RENDA, CHAVE_RENDA) {
        copy(renda = it)
    }

    fun onIdadeChange(texto: String) = atualizarDigitos(texto, MAX_DIGITOS_IDADE, Campo.IDADE, CHAVE_IDADE) {
        copy(idade = it)
    }

    fun onPrazoChange(prazo: Int) {
        savedStateHandle[CHAVE_PRAZO] = prazo
        _uiState.update { it.copy(prazo = prazo) }
    }

    fun onSimular() {
        val estado = _uiState.value
        if (estado.carregando) return // evita duas simulações com dois toques rápidos

        val erros = estado.validar()
        if (erros.isNotEmpty()) {
            _uiState.update { it.copy(erros = erros) }
            // Sem isto, com fonte grande o erro do valor fica fora da tela e
            // parece que o botão não funcionou. O canal tem buffer: trySend não falha.
            _eventos.trySend(SimularEvento.FocarCampo(Campo.entries.first { it in erros }))
            return
        }

        _uiState.update { it.copy(erros = emptyMap(), carregando = true) }
        viewModelScope.launch {
            try {
                val simulacao = repository.simular(estado.toProposta())
                _eventos.send(SimularEvento.IrParaResultado(simulacao.id))
            } catch (e: CancellationException) {
                throw e // cancelamento não é erro: deixa a coroutine terminar
            } catch (e: Exception) {
                _eventos.send(SimularEvento.ErroInesperado)
            } finally {
                _uiState.update { it.copy(carregando = false) }
            }
        }
    }

    /** Aceita só dígitos, sem zeros à esquerda; acima do limite, ignora a digitação. */
    private inline fun atualizarDigitos(
        texto: String,
        limite: Int,
        campo: Campo,
        chave: String,
        aplicar: SimularUiState.(String) -> SimularUiState,
    ) {
        val digitos = texto.filter(Char::isDigit).trimStart('0')
        if (digitos.length > limite) return
        savedStateHandle[chave] = digitos
        _uiState.update { it.aplicar(digitos).copy(erros = it.erros - campo) }
    }

    private companion object {
        const val CHAVE_VALOR = "valor"
        const val CHAVE_PRAZO = "prazo"
        const val CHAVE_RENDA = "renda"
        const val CHAVE_IDADE = "idade"

        // 9 dígitos em centavos = até R$ 9.999.999,99: cabe o máximo do contrato
        // (R$ 1.000.000,00) e ainda permite mostrar o erro de "acima do máximo".
        const val MAX_DIGITOS_DINHEIRO = 9
        const val MAX_DIGITOS_IDADE = 3
    }
}
