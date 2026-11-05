---
tipo: decisao
projeto: simulacred-app
status: decidida
criado: 2026-11-04
---
# 0001 — Template oficial do Android CLI e versões da toolchain

## Contexto

O descritivo pede o template "Empty Activity (Compose)" do Android Studio, que ainda não está instalado. As versões mais novas publicadas (AGP 9.5, Kotlin 2.4.21, Compose BOM 2026.09) não têm combinação testada conhecida, e o caminho do workspace tem acento, o que já exige contornos no build.

## Opções

1. **Montar o projeto à mão com as versões mais novas**: atualizado / risco alto de incompatibilidade entre AGP, Kotlin e KSP, difícil de depurar.
2. **Gerar com `android create empty-activity` (Android CLI do SDK) e subir só o necessário**: combinação já testada pelo Google / versões alguns meses atrás das mais novas.

## Decisão

Opção 2. O template veio com AGP 9.0.1, Gradle 9.1, Kotlin 2.3.20 e Compose BOM 2026.03.01. Mudanças em relação a ele:

- **AGP 9.4.1 + Gradle 9.6.0:** o AGP 9.0.1 não encontra a plataforma `android-37.0` instalada pelo SDK novo (`Failed to find target with hash string 'android-37'`).
- **Navigation Compose 2.10 no lugar do Navigation 3** do template: o descritivo pede Navigation Compose, e é o que a maioria dos apps em produção usa. Rotas tipadas com `@Serializable`.
- **Sem `jvmToolchain(17)`:** compila com o JDK 21 instalado e gera bytecode 17, para o Gradle não baixar outro JDK.
- **Sem cor dinâmica e só modo claro**, com paleta própria de contraste alto (modo escuro é a extensão 2).

## Consequências

- O lint lista versões mais novas disponíveis (Kotlin 2.4, BOM 2026.09). Atualizar num PR próprio, de preferência junto com a entrada do KSP/Dagger, testando a combinação.
- A versão do KSP precisa casar com o Kotlin 2.3.20 quando o Dagger entrar.
