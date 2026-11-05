package br.com.simulacred.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    primaryContainer = PetroleoClaro,
    onPrimaryContainer = PetroleoEscuro,
    secondary = Grafite,
    onSecondary = Color.White,
    secondaryContainer = Areia,
    onSecondaryContainer = Texto,
    background = Fundo,
    onBackground = Texto,
    surface = Fundo,
    onSurface = Texto,
    surfaceVariant = Areia,
    onSurfaceVariant = TextoSecundario,
    outline = Contorno,
    outlineVariant = ContornoSuave,
    error = ErroTom,
    onError = Color.White,
    errorContainer = ErroFundo,
    onErrorContainer = ErroTexto,
)

/** Cores de status que o Material 3 não tem (pré-aprovada). */
@Immutable
data class CoresDeStatus(
    val sucesso: Color = SucessoTom,
    val sucessoFundo: Color = SucessoFundo,
)

val LocalCoresDeStatus = staticCompositionLocalOf { CoresDeStatus() }

/**
 * Tema do app. Por enquanto só o modo claro, sem cor dinâmica do Android 12+:
 * a paleta foi escolhida para contraste alto, e a cor dinâmica poderia quebrá-lo.
 * O modo escuro é a extensão 2 do projeto (docs/pendencias.md).
 */
@Composable
fun SimulaCredTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
