package br.com.simulacred.app.ui.resultado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.simulacred.app.data.SimulacaoRepository
import br.com.simulacred.app.domain.Simulacao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ResultadoUiState {
    data object Carregando : ResultadoUiState
    data class Encontrada(val simulacao: Simulacao) : ResultadoUiState

    /**
     * O id não existe. Acontece de verdade: o Android mata o processo com o app
     * em segundo plano, a pilha de navegação é restaurada com este id, mas o
     * repositório em memória voltou vazio. O contrato manda tratar como o 404:
     * "Simulação não encontrada" e voltar.
     */
    data object NaoEncontrada : ResultadoUiState
}

/**
 * Busca a simulação pelo id da rota. Recebe o id pronto (e não o
 * SavedStateHandle) para o teste rodar na JVM, sem Bundle do Android.
 * O cronograma, o "Me explique" e o resto da tela entram no bloco 3.
 */
class ResultadoViewModel(
    private val simulacaoId: String,
    private val repository: SimulacaoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultadoUiState>(ResultadoUiState.Carregando)
    val uiState: StateFlow<ResultadoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = repository.buscar(simulacaoId)
                ?.let { ResultadoUiState.Encontrada(it) }
                ?: ResultadoUiState.NaoEncontrada
        }
    }
}
