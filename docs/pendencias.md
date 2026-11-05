# Pendências — simulacred-app

Atualizado em 2026-11-04.

## Ambiente

- [ ] **Android Studio + emulador:** só o SDK de linha de comando está instalado (`C:\Android\Sdk`). Para emulador e Layout Inspector, instalar o Studio apontando para esse SDK.
- [ ] **`JAVA_TOOL_OPTIONS` permanente:** hoje vale só no terminal em que é exportado. Sem ele como variável de usuário do Windows, o Android Studio deve falhar no sync com `Unable to establish loopback connection`. Decidir se vira variável de usuário.

## Repositório

- [x] **Remoto no GitHub:** público (decidido em 2026-11-04), para o avaliador ver código, PRs e Releases.
- [ ] Criar o Kanban (GitHub Projects) e as issues dos blocos do plano.

## Plano

- [x] Bloco 1: estrutura, tema e navegação entre 3 telas vazias
- [ ] Bloco 2: tela Simular + `SimularViewModel` + validação + `CalculadoraLocal` + testes
- [ ] Bloco 3: tela Resultado com cronograma
- [ ] Bloco 4: Dagger 2 (componente, módulos, `ViewModelFactory`)
- [ ] Bloco 5: Room + Histórico com Flow
- [ ] Bloco 6: Retrofit + `FakeSimulaCredApi` + fallback + "Me explique"
- [ ] Bloco 7: acessibilidade, README com prints/GIF, `docs/IA.md`, `docs/TESTES.md`, APK no Releases

## Pontos de atenção

- `docs/IA.md` precisa registrar o uso de IA **desde o início** (este scaffold foi feito com o Claude Code), não só no fim.
