package br.com.simulacred.app.ui.navegacao

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import br.com.simulacred.app.ui.theme.SimulaCredTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Teste de UI (precisa de emulador ou aparelho): fluxo entre as telas. */
class NavegacaoTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent { SimulaCredTheme { SimulaCredNavHost() } }
    }

    private fun digitar(rotulo: String, texto: String) {
        composeTestRule.onNode(hasSetTextAction() and hasText(rotulo)).performScrollTo().performTextInput(texto)
    }

    @Test
    fun simularComDadosValidos_levaParaResultado_eVoltarRetornaParaSimular() {
        digitar("Quanto você precisa?", "500000")
        digitar("Quanto você recebe por mês?", "320000")
        digitar("Qual a sua idade?", "58")
        composeTestRule.onNodeWithText("R$ 5.000,00").assertExists()

        composeTestRule.onNodeWithText("Simular").performScrollTo().performClick()

        composeTestRule.onNodeWithText("Resultado da simulação").assertExists()
        composeTestRule.onNodeWithContentDescription("Voltar").performClick()
        composeTestRule.onNodeWithText("Simular empréstimo").assertExists()
    }

    @Test
    fun simularComCamposVazios_ficaNaTelaEMostraErros() {
        composeTestRule.onNodeWithText("Simular").performScrollTo().performClick()

        composeTestRule.onNodeWithText("Simular empréstimo").assertExists()
        composeTestRule.onAllNodes(hasText("Preencha este campo.")).fetchSemanticsNodes().let {
            check(it.size == 3) { "esperava 3 erros, veio ${it.size}" }
        }
    }

    @Test
    fun verMinhasSimulacoes_levaParaHistorico() {
        composeTestRule.onNodeWithText("Ver minhas simulações").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Minhas simulações").assertExists()
    }
}
