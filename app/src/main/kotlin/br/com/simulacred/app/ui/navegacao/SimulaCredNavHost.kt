package br.com.simulacred.app.ui.navegacao

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.simulacred.app.SimulaCredApp
import br.com.simulacred.app.ui.historico.HistoricoScreen
import br.com.simulacred.app.ui.resultado.ResultadoScreen
import br.com.simulacred.app.ui.resultado.ResultadoViewModel
import br.com.simulacred.app.ui.simular.SimularScreen
import br.com.simulacred.app.ui.simular.SimularViewModel

@Composable
fun SimulaCredNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = SimularRoute,
        modifier = modifier,
    ) {
        composable<SimularRoute> {
            val app = LocalContext.current.applicationContext as SimulaCredApp
            // Fábrica provisória; no bloco do Dagger 2 vira a ViewModelFactory injetada.
            val viewModel: SimularViewModel = viewModel {
                SimularViewModel(createSavedStateHandle(), app.simulacaoRepository)
            }
            SimularScreen(
                viewModel = viewModel,
                onIrParaResultado = { id -> navController.navigate(ResultadoRoute(id)) },
                onIrParaHistorico = { navController.navigate(HistoricoRoute) { launchSingleTop = true } },
            )
        }
        composable<ResultadoRoute> { entrada ->
            val rota = entrada.toRoute<ResultadoRoute>()
            val app = LocalContext.current.applicationContext as SimulaCredApp
            val viewModel: ResultadoViewModel = viewModel {
                ResultadoViewModel(rota.simulacaoId, app.simulacaoRepository)
            }
            ResultadoScreen(
                viewModel = viewModel,
                onVoltar = { navController.popBackStack() },
            )
        }
        composable<HistoricoRoute> {
            HistoricoScreen(
                onAbrirSimulacao = { id -> navController.navigate(ResultadoRoute(id)) },
                onVoltar = { navController.popBackStack() },
            )
        }
    }
}
