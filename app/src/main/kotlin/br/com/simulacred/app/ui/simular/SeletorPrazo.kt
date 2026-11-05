package br.com.simulacred.app.ui.simular

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.simulacred.app.R

/**
 * Seletor de prazo em botões grandes, um por opção. Para o público 50+ é mais
 * fácil do que um slider (exige precisão) ou um menu suspenso (esconde as
 * opções). Para o TalkBack, funciona como um grupo de botões de opção.
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.selectableGroup(),
        ) {
            PRAZOS_DISPONIVEIS.forEach { prazo ->
                val selecionado = prazo == prazoSelecionado
                FilterChip(
                    selected = selecionado,
                    onClick = { onPrazoChange(prazo) },
                    label = {
                        Text(
                            text = pluralStringResource(R.plurals.meses, prazo, prazo),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .widthIn(min = 104.dp)
                        .semantics { role = Role.RadioButton },
                )
            }
        }
    }
}
