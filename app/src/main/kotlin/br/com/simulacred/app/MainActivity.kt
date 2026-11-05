package br.com.simulacred.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.simulacred.app.ui.navegacao.SimulaCredNavHost
import br.com.simulacred.app.ui.theme.SimulaCredTheme

/** Única Activity do app (single-activity): só hospeda o NavHost do Compose. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimulaCredTheme {
                SimulaCredNavHost()
            }
        }
    }
}
