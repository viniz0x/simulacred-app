package br.com.simulacred.app.ui.navegacao

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.simulacred.app.ui.historico.HistoricoScreen
import br.com.simulacred.app.ui.resultado.ResultadoScreen
import br.com.simulacred.app.ui.simular.SimularScreen

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
            SimularScreen(
                onIrParaResultado = { id -> navController.navigate(ResultadoRoute(id)) },
                onIrParaHistorico = { navController.navigate(HistoricoRoute) { launchSingleTop = true } },
            )
        }
        composable<ResultadoRoute> { entrada ->
            val rota = entrada.toRoute<ResultadoRoute>()
            ResultadoScreen(
                simulacaoId = rota.simulacaoId,
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
