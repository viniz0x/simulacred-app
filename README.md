---
tipo: projeto
status: ativo
criado: 2026-11-04
---
# SimulaCred App

App Android em Kotlin para a financeira **fictícia** SimulaCred: o cliente 50+ simula um empréstimo em poucos toques, entende a simulação com uma explicação por IA generativa e consulta o histórico sem internet.

Projeto de portfólio para a vaga Junior Mobile Software Engineer (Android + Kotlin). Marca, empresa e dados 100% fictícios.

## Escopo

- **Dentro (MVP):** telas Simular, Resultado e Histórico; botão "Me explique" (IA via backend); histórico offline no Room; cálculo local (Tabela Price) quando a API cai; MVVM + Coroutines/Flow + Dagger 2; testes unitários.
- **Fora (por ora):** login/JWT, modo escuro, CI, Detekt. Extensões, nesta ordem: fonte ajustável e TalkBack revisado, modo escuro, teste de UI do fluxo, GitHub Actions, login com JWT, Detekt/ktlint.

## Mapa

- `CLAUDE.md`: regras curtas para o Claude (importa este README)
- `docs/contrato-api.md`: **contrato da API, fonte única da verdade** entre este app e o backend (`simulacred-api`)
- `docs/decisoes/`: decisões do projeto (modelo `decisao`)
- `docs/pendencias.md`: o que falta definir ou fazer

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin 2.3 (Kotlin embutido do AGP 9) |
| UI | Jetpack Compose (BOM 2026.03.01), Material 3, Navigation Compose 2.10 com rotas tipadas |
| Build | Gradle 9.6 (wrapper), AGP 9.4.1, version catalog em `gradle/libs.versions.toml` |
| SDK | minSdk 26, compileSdk/targetSdk 37 |
| Próximos blocos | Dagger 2 (KSP), Retrofit + OkHttp + kotlinx.serialization, Room, MockK, Turbine |

## Estado

Bloco 1 do plano (estrutura e navegação): três telas-esqueleto navegando entre si, tema acessível (texto base 18sp, botões de 56dp, contraste AA). Ainda sem formulário, cálculo, API ou banco.

## Como rodar

Pré-requisitos nesta máquina: JDK 21 (Temurin) no PATH e Android SDK em `C:\Android\Sdk` (o `local.properties`, fora do git, aponta para ele).

```bash
./gradlew.bat assembleDebug        # gera app/build/outputs/apk/debug/app-debug.apk
./gradlew.bat testDebugUnitTest    # testes unitários (JVM, sem emulador)
./gradlew.bat connectedDebugAndroidTest   # testes de UI (precisa de emulador ou aparelho)
```

**Atenção (Windows com acento no nome do usuário):** o JDK falha com `Unable to establish loopback connection` porque a pasta TEMP tem "í". Antes do Gradle, defina:

```bash
export JAVA_TOOL_OPTIONS="-Djdk.net.unixdomain.tmpdir=C:/Android/tmp"
```

O caminho do projeto também tem acento, por isso o `gradle.properties` liga `android.overridePathCheck=true` (o AGP bloqueia caminhos não ASCII por padrão).
