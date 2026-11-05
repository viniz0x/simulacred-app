package br.com.simulacred.app.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

// Alvo de toque: o mínimo recomendado é 48dp; usamos 56dp pelo público 50+.
private val AlturaMinima = 56.dp

/**
 * Botão principal. Com [carregando], continua na tela, desabilitado e com o
 * indicador dentro: se o botão sumisse, o foco do TalkBack (que estava nele)
 * pularia para o topo da tela. A troca de texto é anunciada (live region).
 */
@Composable
fun BotaoGrande(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    carregando: Boolean = false,
    textoCarregando: String = texto,
) {
    Button(
        onClick = onClick,
        enabled = !carregando,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AlturaMinima)
            .semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        if (carregando) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = LocalContentColor.current,
                    strokeWidth = 3.dp,
                )
                Text(textoCarregando)
            }
        } else {
            Text(texto)
        }
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
