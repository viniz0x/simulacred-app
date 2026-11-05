# CLAUDE.md — projeto simulacred-app

Regras específicas deste projeto. As regras globais (idioma, segurança, forma de trabalhar) estão em `../../CLAUDE.md` e já são carregadas antes deste arquivo.

## Segurança (mínimo)

Valem mesmo quando este repositório é aberto sozinho, sem o workspace (outro PC, sessão na nuvem). A versão completa está no `CLAUDE.md` global.

- Credenciais só em `.env` fora do git: nunca ler o valor, imprimir, copiar, commitar ou colar no chat.
- Dados de clientes (nome, telefone, CPF, endereço, áudios, conversas) seguem a LGPD: nunca em notas, exemplos, testes ou logs de depuração; usar dados fictícios e mascarar em logs.
- Nunca digitar senhas em sites; logins com captcha são feitos pelo Pedro.
- Confirmar antes de qualquer ação destrutiva, irreversível, em produção ou que gaste créditos.

Contexto do projeto:
@README.md

Descritivo completo (arquitetura, plano, definição de pronto): `docs/privado/projeto.md`. Fica **fora do git** porque tem anotações pessoais de preparo; nunca copiar trechos dele para arquivos versionados, commits ou PRs.

## Regras do projeto

- **Contrato da API:** `docs/contrato-api.md` é a fonte única da verdade. Nunca alterar por conta própria; se precisar mudar campo, endpoint ou formato, parar e pedir ao Pedro. A `FakeSimulaCredApi` devolve exatamente os exemplos do contrato.
- **Backend:** o projeto `../simulacred-api` é de outro agente. Não ler para depender dele, não editar, não rodar comandos lá. A integração é só pelo contrato.
- **Dinheiro:** nunca `Double`/`Float`. `String` nos DTOs e no Room, `BigDecimal` no domínio, `HALF_EVEN` com 2 casas. Os números do exemplo A do contrato (parcela 517,12; última 517,06; total 6.205,38) são obrigatórios nos testes.
- **Arquitetura:** a UI só conversa com o ViewModel; o ViewModel não conhece `Context` nem `Composable`; estado em `StateFlow`, eventos únicos em `Channel`.
- **DI:** Dagger 2 puro (não Hilt). Se travar mais de 2 horas, `AppContainer` manual e registrar a decisão.
- **Acessibilidade (público 50+):** texto em `sp` com base mínima de 18sp, parcela em destaque 32sp, alvos de toque >= 48dp (botões do app usam 56dp), `contentDescription` em ícones, contraste WCAG AA, textos em `strings.xml` em linguagem simples.
- **Chave de IA nunca no app:** a explicação vem do endpoint `/explicacao` do backend.
- **Build nesta máquina:** exportar `JAVA_TOOL_OPTIONS="-Djdk.net.unixdomain.tmpdir=C:/Android/tmp"` antes do `./gradlew.bat` (ver README). Antes de dizer que algo está pronto, rodar `./gradlew.bat testDebugUnitTest assembleDebug` e informar o resultado real.
- **Fluxo de trabalho:** uma branch e um PR por bloco do plano (`docs/privado/projeto.md`, seção 11); a vaga avalia o histórico de PRs.

## Git

- Repositório próprio, branch `main`. Remoto `origin` **público** no GitHub (`viniz0x/simulacred-app`): tudo que é commitado fica visível. Commits com o e-mail noreply do GitHub. Push só com confirmação do Pedro.
- Escopos de commit deste projeto: `ui`, `simular`, `resultado`, `historico`, `dados`, `api`, `di`, `dominio`, `build`, `docs`.
- Rodapé de decisão: `Decisao: simulacred-app/<arquivo>`.
