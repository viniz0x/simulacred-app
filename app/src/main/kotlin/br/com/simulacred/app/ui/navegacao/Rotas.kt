package br.com.simulacred.app.ui.navegacao

import kotlinx.serialization.Serializable

// Rotas tipadas do Navigation Compose: o compilador garante os argumentos,
// sem montar strings como "resultado/{id}" à mão.

@Serializable
data object SimularRoute

@Serializable
data class ResultadoRoute(val simulacaoId: String)

@Serializable
data object HistoricoRoute
