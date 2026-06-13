# DomusControl - Sistema de Gestão de Casa Inteligente

Projeto de **Programação Orientada a Objetos (POO)**, em Java: uma aplicação de linha de comandos que simula o controlo de uma casa inteligente (smart home), com dispositivos, divisões, cenários e utilizadores.

## Conceitos do domínio

- **Casa** - composta por várias **Divisões** (quartos, sala, cozinha, etc.).
- **Dispositivos** - cada divisão pode conter dispositivos como:
  - `Lampada` - luzes (ligar/desligar, intensidade)
  - `Tomada` - tomadas inteligentes
  - `Persiana` - persianas (abrir/fechar)
  - `Coluna` - altifalantes/colunas inteligentes
- **Cenário** - conjunto de configurações de dispositivos que podem ser aplicadas de uma vez (ex: "Modo Cinema", "Sair de Casa").
- **Utilizador** - utilizadores registados, com login, que podem controlar a casa.

## Funcionalidades (via `TextUI`)

- Registo de utilizadores e login
- Avançar o tempo da simulação (para testar comportamentos automáticos/agendados)
- Consultar estatísticas globais da casa
- Gerir dispositivos e cenários

## Estrutura do código (`src/`)

| Ficheiro | Responsabilidade |
|---|---|
| `Main.java` | Ponto de entrada da aplicação |
| `DomusControl.java` | Controlador principal (lógica de negócio) |
| `TextUI.java` | Interface de texto / menus interativos |
| `Menu.java` | Construção e validação de menus |
| `Casa.java`, `Divisao.java` | Estrutura da casa e divisões |
| `Dispositivo.java` e subclasses (`Lampada`, `Tomada`, `Persiana`, `Coluna`) | Hierarquia de dispositivos |
| `Cenario.java` | Cenários/configurações pré-definidas |
| `Utilizador.java` | Dados e autenticação de utilizadores |
| `Comando.java` | Padrão de comandos para ações sobre dispositivos |
| `GuardarEstado.java` | Persistência do estado da aplicação |

## Diagrama UML

`DIAGRAMA.uml` / `DIAGRAMA PNG.png` - diagrama de classes do sistema.

## Como executar

```bash
cd src
javac *.java
java Main
```

## Relatório

`relatorio POO.pdf` - relatório com a descrição do projeto, decisões de design e diagrama de classes.
