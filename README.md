![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![POO](https://img.shields.io/badge/POO-Programação%20Orientada%20a%20Objetos-2E7D32?style=for-the-badge)
![Agroecologia](https://img.shields.io/badge/Agroecologia-Sustentabilidade-558B2F?style=for-the-badge)
# GreenSystem

Projeto da 2ª unidade de Programação Orientada a Objetos (Java 25 LTS), IFPE Campus Igarassu, turma IPI.

**Autora:** Karolayne Firmino Souza Lopes (trabalho individual, com autorização do professor)

**Domínio escolhido:** Horta e agroecologia

<img width="1900" height="790" alt="Logo GreenSystem_ Tecnologia Verde" src="https://github.com/user-attachments/assets/89590407-fdfa-428a-b330-ee9893f9b1c4" />

## Identidade visual

Eu criei a logo e escolhi o nome Green System para o projeto, pensando em algo que lembrasse natureza e tecnologia ao mesmo tempo, que é a ideia de um sistema para organizar o uso de áreas de cultivo.

## O que o programa faz

É um programa de linha de comando que gerencia reservas de áreas de cultivo do campus. Eu cadastro as áreas, faço reservas, cancelo e consulto a agenda do dia. O programa recusa as reservas que quebram as regras e explica o motivo, e não encerra com erro por causa de uma entrada inválida.

## Como compilar e executar

Precisa do JDK 25. Na pasta raiz do projeto:

    javac -d out $(find src -name '*.java')
    java -cp out br.edu.ifpe.reservas.app.App

## Menu

1. Cadastrar área
2. Listar áreas
3. Reservar
4. Cancelar
5. Agenda do dia (de todas as áreas ou de uma só)
6. Consultas (ocupação por área e quantidade de solicitantes diferentes)
7. Sair

## Estrutura dos pacotes

| Pacote | Classes | O que faz |
|---|---|---|
| `modelo` | `AreaDeCultivo` (abstrata), `Horta`, `Estufa`, `Viveiro`, `Reserva`, `Exportavel`, `ResumoOcupacao` | Os dados e as regras de cada objeto |
| `excecao` | `RegraDeReservaException`, `ConflitoDeHorarioException` | Os erros de negócio |
| `servico` | `GerenciadorReservas` | Guarda as áreas e reservas e aplica as regras |
| `app` | `App` | O menu e a conversa com o usuário |

## Limites de cada tipo de área

Eu escolhi estes valores, diferentes entre os três tipos:

| Tipo | Duração máxima | Funcionamento |
|---|---|---|
| `Horta` | 2 horas | 08:00 às 18:00 |
| `Estufa` | 1 hora | 07:00 às 19:00 |
| `Viveiro` | 1 hora e 30 minutos | 06:00 às 20:00 |

## Conteúdo aplicado (capítulos 1 a 13)

| Capítulo | Onde aparece no projeto |
|---|---|
| 1 | Menu em laço com `switch` e `Scanner`, leitura e validação de entradas, `String.format` |
| 2 e 3 | Classes do domínio e vários objetos em execução |
| 4 | Método sobrecarregado: `agendaDoDia(data)` e `agendaDoDia(data, codigoArea)` |
| 5 | Construtores que validam; `record` `ResumoOcupacao` |
| 6 | Atributos `private final`, só getters, sem setters |
| 7 | `Horta`, `Estufa` e `Viveiro` herdam de `AreaDeCultivo` |
| 8 | Duração máxima e horário de funcionamento sobrescritos em cada subclasse |
| 9 | Classe abstrata `AreaDeCultivo` e interface `Exportavel` (`paraCsv()`) |
| 10 | `ArrayList` com as reservas |
| 11 | `HashMap` das áreas por código, `HashMap` do total de minutos por área, `HashSet` dos solicitantes |
| 12 | Exceções verificadas personalizadas, tratadas no menu |
| 13 | Código dividido em quatro pacotes |

## Decisões de projeto

- **Classe abstrata `AreaDeCultivo`:** não existe "área genérica", então ela é abstrata. As três subclasses sobrescrevem os métodos de tipo, duração máxima, abertura e fechamento.
- **Polimorfismo nos limites:** o `GerenciadorReservas` chama `area.duracaoMaximaMinutos()` e os outros métodos sem saber qual é o tipo da área. Não há `if` nem `switch` sobre o tipo.
- **Objetos sempre válidos:** os construtores validam os dados. Se algo estiver errado, o objeto não é criado.
- **`conflitaCom` na `Reserva`:** duas reservas conflitam se forem da mesma área, no mesmo dia, e o início de uma for antes do fim da outra (e vice-versa). Usei `<` e não `<=`, então terminar às 11:00 e começar às 11:00 não é conflito.
- **Exceções tratadas só no menu:** os métodos de regra lançam as exceções e o `App` trata todas em um único `catch`.
- **Data e horário:** a data é guardada como texto no formato `dd/MM/yyyy` e normalizada com dois dígitos (assim `1/1/2026` vira `01/01/2026`). Os horários são digitados como `HH:mm` e guardados em minutos desde a meia-noite.
- **Reserva recusada não gasta número:** a reserva só entra na lista e recebe número depois de passar por todas as regras.
- **Áreas de exemplo:** como o programa ainda não grava em arquivo, ele começa com `HOR01`, `EST01` e `VIV01` cadastradas no código.

## Melhorias feitas durante o desenvolvimento

- Tirei os setters da `AreaDeCultivo`, porque eles permitiam deixar um objeto inválido depois de criado.
- Corrigi os métodos abstratos para devolverem valores (`tipo()`, `duracaoMaximaMinutos()`, `aberturaMinutos()`, `fechamentoMinutos()`) em vez de imprimirem na tela.
- Passei a usar `trim()` e `isBlank()` na validação dos textos e bloqueei o `;` no código e no nome, porque ele quebraria o CSV no futuro.
- Reuni a validação de textos em um método só (`validarTexto`), para não repetir código.
- Troquei o `formatarHora` repetido por um método único, `Reserva.formatarHora`.
- Passei a listar as áreas antes de pedir o código na hora de reservar, depois de perceber nos testes que digitar o tipo ("Horta") em vez do código ("HOR01") confundia o usuário.

## Testes

Fiz testes manuais com classes de teste em `app/` (`Teste`, `TesteConflitos` e `TesteConsultas`) e rodando o menu. Testei reserva válida, conflito, reservas que só encostam, duração acima do máximo, fora do horário de funcionamento, datas e horários inválidos, cancelamento, o mesmo horário em áreas diferentes e as consultas.

## Limitações conhecidas

- Os dados ficam só na memória e se perdem ao fechar o programa.
- A validação da data confere só dia de 1 a 31 e mês de 1 a 12, então `31/02/2026` é aceita, e datas passadas também.

## Extras planejados (ainda não feitos)

Pretendo fazer, depois do núcleo: D1 (datas com `java.time`, que resolve as datas inexistentes), D2 (gravar e carregar em CSV), os bônus B1 a B5 e o frontend opcional B6. Vou atualizar este README conforme cada extra ficar pronto e funcionando.

## Uso de ferramentas de IA

Usei o Claude (Anthropic) como apoio para estudar e construir o projeto por etapas.

**O que eu fiz:** escolhi o domínio e defini os limites de cada tipo de área; escrevi as três subclasses, a `ConflitoDeHorarioException`, a primeira versão da `AreaDeCultivo` e da `Reserva` e o começo do `GerenciadorReservas` (`cadastrarArea`, a busca da área no `reservar` e o `cancelar`); apliquei as correções que apareceram nas revisões; compilei e testei tudo; e criei a logo e o nome do projeto.

**O que a IA fez:** explicou os conceitos (classe abstrata, polimorfismo, `split`, `try`/`catch`, `HashSet`, `record` etc.), revisou o meu código e apontou erros, e escreveu a versão completa da `AreaDeCultivo` final, da `Reserva` final, do `GerenciadorReservas` final, do `ResumoOcupacao`, da `Exportavel`, do `App`, e das classes de teste. Li, compilei e testei todo esse código.