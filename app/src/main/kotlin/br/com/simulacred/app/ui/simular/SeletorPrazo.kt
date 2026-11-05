package br.com.simulacred.app.ui.simular

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.simulacred.app.R

/**
 * Seletor de prazo em botões grandes, um por opção. Para o público 50+ é mais
 * fácil do que um slider (exige precisão) ou um menu suspenso (esconde as
 * opções). Cada opção é um `selectable` com papel de botão de opção, dentro de
 * um `selectableGroup`, para o TalkBack anunciar "botão de opção" e o estado
 * selecionado. (O FilterChip do Material já tem papel próprio, e sobrescrevê-lo
 * deixava dois papéis no mesmo elemento.)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeletorPrazo(
    prazoSelecionado: Int,
    onPrazoChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.simular_rotulo_prazo),
            style = MaterialTheme.typography.titleMedium,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.selectableGroup(),
        ) {
            PRAZOS_DISPONIVEIS.forEach { prazo ->
                OpcaoPrazo(
                    texto = pluralStringResource(R.plurals.meses, prazo, prazo),
                    selecionada = prazo == prazoSelecionado,
                    onClick = { onPrazoChange(prazo) },
                )
            }
        }
    }
}

@Composable
private fun OpcaoPrazo(texto: String, selecionada: Boolean, onClick: () -> Unit) {
    val cores = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selecionada) cores.primary else cores.surface,
        contentColor = if (selecionada) cores.onPrimary else cores.onSurface,
        // Selecionada também muda a borda (não só a cor), para quem não distingue cores.
        border = BorderStroke(if (selecionada) 3.dp else 1.dp, if (selecionada) cores.primary else cores.outline),
        modifier = Modifier
            .heightIn(min = 56.dp)
            .widthIn(min = 104.dp)
            .selectable(selected = selecionada, role = Role.RadioButton, onClick = onClick),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selecionada) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}
