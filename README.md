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

É um programa de linha de comando que gerencia reservas de áreas de cultivo do campus. Eu cadastro as áreas, faço reservas, cancelo, consulto a agenda do dia e posso importar várias reservas de um arquivo CSV. O programa recusa as reservas que quebram as regras e explica o motivo, e não encerra com erro por causa de uma entrada inválida. Os dados ficam guardados em arquivos, então o programa lembra de tudo quando é aberto de novo, e cada operação feita é registrada em um arquivo de log.

## Como compilar e executar

Precisa do JDK 25. Na pasta raiz do projeto:

    javac -d out $(find src -name '*.java')
    java -cp out br.edu.ifpe.reservas.app.App

Rode sempre a partir da pasta raiz, porque a pasta `dados/` e o arquivo `log.txt` são criados e lidos a partir de onde o programa é executado.

## Menu

1. Cadastrar área
2. Listar áreas
3. Reservar
4. Cancelar
5. Agenda do dia (de todas as áreas ou de uma só)
6. Consultas (ocupação por área e quantidade de solicitantes diferentes)
7. Importar reservas de um arquivo CSV
8. Sair (grava os dados nos arquivos)

A data é digitada como `dd/MM/yyyy` (por exemplo `12/11/2026`) e os horários como `HH:mm` (por exemplo `14:30`). Para guardar os dados, é preciso sair pela opção 8.

## Estrutura dos pacotes

| Pacote | Classes | O que faz |
|---|---|---|
| `modelo` | `AreaDeCultivo` (abstrata), `Horta`, `Estufa`, `Viveiro`, `Reserva`, `Exportavel`, `ResumoOcupacao`, `ResultadoImportacao` | Os dados e as regras de cada objeto |
| `excecao` | `RegraDeReservaException`, `ConflitoDeHorarioException` | Os erros de negócio |
| `servico` | `GerenciadorReservas`, `ImportadorReservas` | Guardam as áreas e reservas, aplicam as regras e importam reservas de um CSV |
| `persistencia` | `RepositorioCsv`, `RegistroLog` | Gravam e leem os arquivos CSV; registram o log |
| `app` | `App` | O menu e a conversa com o usuário |

## Limites de cada tipo de área

Eu escolhi estes valores, diferentes entre os três tipos:

| Tipo | Duração máxima | Funcionamento | Antecedência mínima |
|---|---|---|---|
| `Horta` | 2 horas | 08:00 às 18:00 | 0 dia (pode ser reservada para hoje) |
| `Estufa` | 1 hora | 07:00 às 19:00 | 1 dia |
| `Viveiro` | 1 hora e 30 minutos | 06:00 às 20:00 | 2 dias |

## Arquivos de dados

Os dados ficam na pasta `dados/`, em dois arquivos de texto (UTF-8) com campos separados por `;`.

`dados/areas.csv`, com o tipo na primeira coluna:

    Horta;HOR01;Horta Central;20
    Estufa;EST01;Estufa de Mudas;10

`dados/reservas.csv`, com a data no formato ISO e os horários como `HH:mm`:

    1;HOR01;Maria José;2026-11-30;14:00;15:30;Colheita de açaí

Na primeira execução os arquivos não existem, e isso não é erro: o programa abre com três áreas de exemplo (`HOR01`, `EST01` e `VIV01`). Linhas em branco nos arquivos são ignoradas. Nenhum texto pode ter `;`, e o programa recusa esse caractere na digitação. A pasta `dados/` do repositório tem arquivos de exemplo.

## Conteúdo aplicado

| Capítulo | Onde aparece no projeto |
|---|---|
| 1 | Menu em laço com `switch` e `Scanner`, leitura e validação de entradas, `String.format` |
| 2 e 3 | Classes do domínio e vários objetos em execução |
| 4 | Método sobrecarregado: `agendaDoDia(data)` e `agendaDoDia(data, codigoArea)` |
| 5 | Construtores que validam; `record` `ResumoOcupacao` e `ResultadoImportacao` |
| 6 | Atributos `private final`, só getters, sem setters |
| 7 | `Horta`, `Estufa` e `Viveiro` herdam de `AreaDeCultivo` |
| 8 | Duração máxima, horário de funcionamento e antecedência sobrescritos em cada subclasse |
| 9 | Classe abstrata `AreaDeCultivo` e interface `Exportavel` (`paraCsv()`) |
| 10 | `ArrayList` com as reservas |
| 11 | `HashMap` das áreas por código, `HashMap` do total por área, `HashSet` dos solicitantes |
| 12 | Exceções verificadas personalizadas, tratadas no menu (e linha a linha na importação do B2) |
| 13 | Código dividido em cinco pacotes |
| 14 (extra D1 e B5) | `LocalDate`, `LocalTime`, `LocalDateTime`, `Duration`, `ChronoUnit` e `DateTimeFormatter` com `ResolverStyle.STRICT`; `hoje` recebido como parâmetro |
| 15 (extra D2, B5 e B2) | `Path`, `Files`, CSV em UTF-8 e `try-with-resources`; dados gravados ao sair e carregados ao abrir; log com `StandardOpenOption.APPEND`; importação de um CSV |

## Decisões de projeto

- **Classe abstrata `AreaDeCultivo`:** não existe "área genérica", então ela é abstrata. As três subclasses sobrescrevem os métodos de tipo, duração máxima, abertura, fechamento e antecedência mínima.
- **Polimorfismo nos limites:** o `GerenciadorReservas` chama `area.duracaoMaxima()` e os outros métodos sem saber qual é o tipo da área. Não há `if` nem `switch` sobre o tipo.
- **Objetos sempre válidos:** os construtores validam os dados. Se algo estiver errado, o objeto não é criado.
- **`conflitaCom` na `Reserva`:** duas reservas conflitam se forem da mesma área, no mesmo dia, e o início de uma for antes do fim da outra (e vice-versa). Usei `isBefore` (e não "antes ou igual"), então terminar às 11:00 e começar às 11:00 não é conflito.
- **Exceções tratadas só no menu:** os métodos de regra lançam as exceções e o `App` trata todas em um único `catch`. A única exceção a essa regra é o `ImportadorReservas` (B2), de propósito.
- **Data e horário com `java.time`:** a data é um `LocalDate` e os horários são `LocalTime`. A duração máxima de cada tipo é uma `Duration`. A leitura usa `DateTimeFormatter` com `ResolverStyle.STRICT`, então datas que não existem (como `31/02/2026`) e horas inválidas (como `24:00`) são recusadas.
- **`hoje` como parâmetro:** o método `reservar` recebe a data de hoje de fora, e só o `App` chama `LocalDate.now()` e `LocalDateTime.now()`. Assim as regras de data passada e de antecedência mínima podem ser testadas com um dia fixo.
- **Reserva recusada não gasta número:** a reserva só entra na lista e recebe número depois de passar por todas as regras.
- **Só o `RepositorioCsv` mexe com os arquivos de dados:** o `GerenciadorReservas` e o `App` não sabem como os dados são gravados. O mesmo método grava áreas e reservas, porque as duas implementam `Exportavel`.
- **O primeiro campo decide a subclasse:** ao ler `areas.csv`, o texto `Horta`, `Estufa` ou `Viveiro` diz qual classe criar.
- **Carregar não passa pelo `reservar`:** reservas antigas lidas do arquivo seriam recusadas pelas regras de data passada e antecedência, então o `carregarDados` coloca os dados direto no gerenciador. Por isso o construtor da `Reserva` também não confere data passada.
- **Numeração contínua (RN7):** ao carregar, o próximo número é o maior número existente mais um.
- **Arquivo com erro não é apagado:** se a leitura falhar, o programa avisa, abre sem os dados e não grava ao sair, para não sobrescrever os arquivos.
- **Áreas de exemplo só na primeira vez:** elas só entram quando não há nenhuma área carregada.

## Melhorias feitas durante o desenvolvimento

- Tirei os setters da `AreaDeCultivo`, porque eles permitiam deixar um objeto inválido depois de criado.
- Corrigi os métodos abstratos para devolverem valores em vez de imprimirem na tela.
- Passei a usar `trim()` e `isBlank()` na validação dos textos e bloqueei o `;` no código e no nome, porque ele quebraria o CSV.
- Reuni a validação de textos em um método só (`validarTexto`), para não repetir código.
- Deixei a formatação de hora em um método único (`Reserva.formatarHora`), em vez de uma cópia em cada classe.
- Passei a listar as áreas antes de pedir o código na hora de reservar, depois de perceber nos testes que digitar o tipo ("Horta") em vez do código ("HOR01") confundia o usuário.
- Descobri nos testes do menu que o núcleo aceitava `31/02/2026`, e isso me levou a fazer o D1.
- Testei um arquivo com defeito de propósito e conferi que o programa não apaga os dados.
- Vi um caractere inválido (`�`) entrar num nome digitado e ser gravado no CSV, e passei a recusar esse caractere na validação dos textos.
- Padronizei o fim de linha dos arquivos para LF (e criei o `.gitattributes`), porque alguns tinham sido criados com o fim de linha do Windows.

## Testes

Fiz testes manuais com classes de teste e rodando o menu.

- `TesteRegras` fixa o "hoje" em 10/11/2026 para o resultado ser sempre o mesmo. Testa reserva válida, conflito, reservas que só encostam, data passada, antecedência mínima de cada tipo, duração acima do máximo, fora do horário de funcionamento, datas inexistentes (`31/02`), ano bissexto, horários inválidos e área inexistente.
- `TestePersistencia` usa uma pasta temporária. Testa a primeira execução sem arquivos, a gravação com acentos, linhas em branco, a leitura de volta e a numeração contínua.
- `TesteLog` simula duas execuções numa pasta temporária e confere que o log é acrescentado, e não sobrescrito.
- `TesteImportacao` grava um arquivo com linhas válidas, inválidas e em branco numa pasta temporária, importa com o `hoje` fixado e confere as contagens (4 importadas e 6 descartadas) e os motivos.
- No menu, testei também o cancelamento, as consultas, a gravação ao sair, a abertura seguinte, arquivos com linha em branco e com linha inválida, o conteúdo do `log.txt` depois de duas execuções seguidas e a importação (arquivo de exemplo, arquivo que não existe, caminho vazio e importar o mesmo arquivo duas vezes).

Para rodar os testes:

    java -cp out br.edu.ifpe.reservas.app.TesteRegras
    java -cp out br.edu.ifpe.reservas.app.TestePersistencia
    java -cp out br.edu.ifpe.reservas.app.TesteLog
    java -cp out br.edu.ifpe.reservas.app.TesteImportacao

## Limitações conhecidas

- Os dados só são gravados quando saio pela opção 8. Se o programa for fechado de outro jeito (por exemplo com Ctrl+C), o que foi feito naquela sessão se perde.
- O programa confia no conteúdo dos arquivos de dados: ele recusa linhas mal formadas, mas não confere se duas reservas lidas do arquivo conflitam entre si.
- Se eu editar os arquivos à mão e colocar um `;` dentro de um texto, a linha fica inválida.
- Na importação, uma linha de cabeçalho (por exemplo `codigoArea;solicitante;...`) seria descartada como inválida, então o arquivo deve ter só reservas.

## Extras

O D1 (datas e horas com `java.time`), o D2 (gravar e carregar em CSV), o B5 (log) e o B2 (importação) estão prontos. Pretendo fazer ainda os bônus B4, B1 e B3, e vou atualizar este README conforme cada um ficar pronto e funcionando.

### B5: arquivo de log

A cada execução, o programa acrescenta no arquivo `log.txt` uma linha por operação, com a data, a hora e o que foi feito. Registra o início e o fim do programa, o carregamento e a gravação dos dados, as áreas cadastradas, as reservas criadas e canceladas, as consultas, as importações e as operações recusadas (com o motivo). O nome do solicitante não vai para o log.

Exemplo:

    25/11/2026 14:02:10 | Programa iniciado
    25/11/2026 14:02:10 | Dados carregados: 3 área(s) e 2 reserva(s)
    25/11/2026 14:02:31 | Reserva criada: #3 HOR01 em 30/11/2026 14:00-15:30
    25/11/2026 14:03:02 | Programa encerrado

Decisões:

- A classe `RegistroLog` usa `LocalDateTime`, `DateTimeFormatter` e `Files.writeString` com `StandardOpenOption.CREATE` e `APPEND`, então as execuções antigas não são apagadas.
- Só o `App` chama `LocalDateTime.now()`. A `RegistroLog` recebe o momento de fora, como o `hoje` das regras de reserva.
- Se não for possível escrever no log, o programa avisa e continua funcionando.
- O `log.txt` está no `.gitignore`, porque é gerado a cada uso.

### B2: importação de reservas de um CSV

Na opção 7 do menu eu informo o caminho de um arquivo CSV e o programa tenta criar cada reserva do arquivo. As linhas inválidas são ignoradas, e no final ele mostra quantas reservas foram importadas, quantas linhas foram descartadas e o motivo de cada descarte.

Formato de cada linha (o número da reserva é gerado automaticamente):

    codigoArea;solicitante;dd/MM/yyyy;HH:mm;HH:mm;finalidade

O arquivo `exemplos/importacao.csv` tem 10 linhas: 4 válidas e 6 com problemas diferentes (conflito, área inexistente, data inexistente, duração acima do máximo, antes de abrir e número errado de campos). Importando esse arquivo, o resultado é:

    Importação concluída: 4 reserva(s) importada(s), 6 linha(s) descartada(s).

Decisões:

- Cada linha passa pelo mesmo `reservar` do menu, então valem todas as regras (conflito, limites do tipo, data passada e antecedência mínima).
- O `ImportadorReservas` captura as exceções linha por linha. É uma exceção de propósito à regra de tratar exceções só no menu, porque o objetivo é descartar a linha ruim e continuar.
- Diferente do carregamento dos arquivos do sistema (que para no primeiro erro, para não carregar dados inconsistentes), a importação é tolerante, porque o arquivo vem de fora.
- A linha em branco é ignorada, e um arquivo que não existe ou um caminho vazio dão uma mensagem de erro, sem derrubar o programa.
- Importar o mesmo arquivo duas vezes não duplica as reservas, porque na segunda vez elas conflitam com as que já existem.
- O resultado volta num `record` (`ResultadoImportacao`).
- O `hoje` vem de fora, como no `reservar`, para dar para testar.

## Uso de ferramentas de IA

Usei o Claude (Anthropic) como apoio para estudar e construir o projeto por etapas.

**O que eu fiz:** escolhi o domínio e defini os limites de cada tipo de área, incluindo a antecedência mínima; escrevi as três subclasses, a `ConflitoDeHorarioException`, a primeira versão da `AreaDeCultivo` e da `Reserva` e o começo do `GerenciadorReservas` (`cadastrarArea`, a busca da área no `reservar` e o `cancelar`); apliquei as correções que apareceram nas revisões; compilei e testei tudo, inclusive no menu e com um arquivo de dados com defeito de propósito; e criei a logo e o nome do projeto.

**O que a IA fez:** explicou os conceitos (classe abstrata, polimorfismo, `split`, `try`/`catch`, `HashSet`, `record`, `java.time`, `Path`, `Files`, `try-with-resources`, UTF-8, `StandardOpenOption.APPEND` etc.), revisou o meu código e apontou erros, e escreveu a versão completa da `AreaDeCultivo`, da `Reserva`, do `GerenciadorReservas`, do `ResumoOcupacao`, da `Exportavel`, do `App` e das classes de teste. Para o D1, escreveu a versão com `java.time` das classes `AreaDeCultivo`, `Horta`, `Estufa`, `Viveiro`, `Reserva`, `ResumoOcupacao`, `GerenciadorReservas` e `App`, e o `TesteRegras`. Para o D2, escreveu o `RepositorioCsv`, os métodos `listarReservas` e `carregarDados` do `GerenciadorReservas`, a nova versão do `App` e o `TestePersistencia`. Para o B5, escreveu a `RegistroLog`, a nova versão do `App` com os registros e o `TesteLog`. Para o B2, escreveu o `ImportadorReservas`, o `ResultadoImportacao`, o método novo do `RepositorioCsv`, a nova versão do `App` com a opção de importar, o arquivo de exemplo e o `TesteImportacao`. O texto deste README também foi escrito com ajuda da IA.

Li, compilei e testei todo esse código, e apliquei as correções que apareceram pelo caminho (por exemplo, o erro de compilação no `ResumoOcupacao` e o `import` que faltava no `RepositorioCsv`).
Pull request

Título:

B2: importação de reservas de um CSV ignorando linhas inválidas

Descrição:

markdown
## O que mudou

Implementei o bônus B2: uma nova opção do menu (7) importa reservas de um arquivo CSV, ignora as linhas inválidas e mostra quantas foram descartadas e por quê.

- `ImportadorReservas` (pacote `servico`): tenta criar cada linha pelo `reservar`, dentro de um `try`/`catch`. Se der certo, conta como importada; se lançar exceção, conta como descartada e guarda o motivo.
- `ResultadoImportacao` (pacote `modelo`): `record` com as contagens e os motivos.
- `RepositorioCsv`: novo método `lerLinhasDeImportacao`, que reaproveita o `lerLinhas` (UTF-8, `try-with-resources`, sem linhas em branco) mas exige que o arquivo exista.
- `App`: nova opção 7 (Importar CSV). O Sair passou a ser a opção 8. A importação é registrada no log.
- `exemplos/importacao.csv`: 10 linhas, 4 válidas e 6 com problemas diferentes.
- Novo teste `TesteImportacao` (com o `hoje` fixado) e README atualizado.

## Decisões

- Cada linha passa pelas mesmas regras de uma reserva digitada (conflito, limites do tipo, data passada, antecedência).
- O importador captura as exceções linha por linha, de propósito, porque o objetivo é descartar a linha ruim e continuar.
- A importação é tolerante, ao contrário do carregamento dos arquivos do sistema, que para no primeiro erro.

## Como testar

    rm -rf out
    javac -d out $(find src -name '*.java')
    java -cp out br.edu.ifpe.reservas.app.TesteImportacao

Resultado esperado: 10 linhas lidas, 4 importadas e 6 descartadas, com um motivo para cada descarte. No menu, importei o arquivo de exemplo, tentei um arquivo que não existe, um caminho vazio e importei o mesmo arquivo duas vezes (0 importadas, 10 descartadas).