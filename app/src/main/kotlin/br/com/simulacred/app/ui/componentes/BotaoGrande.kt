package br.com.simulacred.app.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Alvo de toque: o mínimo recomendado é 48dp; usamos 56dp pelo público 50+.
private val AlturaMinima = 56.dp

@Composable
fun BotaoGrande(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth().heightIn(min = AlturaMinima),
    ) {
        Text(texto)
    }
}

@Composable
fun BotaoGrandeSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = AlturaMinima),
    ) {
        Text(texto)
    }
}
