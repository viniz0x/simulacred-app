package br.com.simulacred.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Público 50+: texto base de no mínimo 18sp e parcela em destaque com 32sp.
// Tudo em sp, então o tamanho de fonte do sistema continua sendo respeitado.
private val Base = Typography()

val Typography = Typography(
    displaySmall = Base.displaySmall.copy(fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold),
    headlineMedium = Base.headlineMedium.copy(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.SemiBold),
    headlineSmall = Base.headlineSmall.copy(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = Base.titleLarge.copy(fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 18.sp, lineHeight = 28.sp, letterSpacing = 0.2.sp),
    bodyMedium = TextStyle(fontSize = 18.sp, lineHeight = 26.sp, letterSpacing = 0.2.sp),
    bodySmall = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.2.sp),
    labelLarge = Base.labelLarge.copy(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = Base.labelMedium.copy(fontSize = 16.sp, lineHeight = 22.sp),
)
