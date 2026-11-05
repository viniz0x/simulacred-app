package br.com.simulacred.app.ui.navegacao

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.simulacred.app.ui.theme.SimulaCredTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Teste de UI (precisa de emulador ou aparelho): fluxo entre as 3 telas. */
class NavegacaoTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent { SimulaCredTheme { SimulaCredNavHost() } }
    }

    @Test
    fun simular_levaParaResultado_eVoltarRetornaParaSimular() {
        composeTestRule.onNodeWithText("Simular").performClick()
        composeTestRule.onNodeWithText("Resultado da simulação").assertExists()
        composeTestRule.onNodeWithContentDescription("Voltar").performClick()
        composeTestRule.onNodeWithText("Simular empréstimo").assertExists()
    }

    @Test
    fun verMinhasSimulacoes_levaParaHistorico() {
        composeTestRule.onNodeWithText("Ver minhas simulações").performClick()
        composeTestRule.onNodeWithText("Minhas simulações").assertExists()
    }
}
