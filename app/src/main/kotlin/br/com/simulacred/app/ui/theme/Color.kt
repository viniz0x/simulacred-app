package br.com.simulacred.app.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta da marca fictícia SimulaCred (verde-petróleo + neutros quentes).
// Todos os pares texto/fundo abaixo passam no contraste WCAG AA (>= 4,5:1);
// os principais (onSurface/surface, onPrimary/primary) passam no AAA (>= 7:1).

val Petroleo = Color(0xFF0F5B4E) // primary: 7,6:1 sobre branco
val PetroleoEscuro = Color(0xFF08362E)
val PetroleoClaro = Color(0xFFCDEBE3) // primaryContainer
val Areia = Color(0xFFF2EBDD) // secondaryContainer
val Grafite = Color(0xFF4A4339) // secondary: 9,4:1 sobre branco
val Fundo = Color(0xFFFCFBF8)
val Texto = Color(0xFF1B1C1A) // 16,9:1 sobre o fundo
val TextoSecundario = Color(0xFF45484A) // 9,3:1 sobre o fundo
val Contorno = Color(0xFF6F7375) // 4,8:1: bordas de campos
val ContornoSuave = Color(0xFFC6C8C9)
val ErroTom = Color(0xFFB3261E) // 6,5:1 sobre branco
val ErroFundo = Color(0xFFF9DEDC)
val ErroTexto = Color(0xFF410E0B)
val SucessoTom = Color(0xFF1E6B2F) // pré-aprovada
val SucessoFundo = Color(0xFFD7EEDB)
