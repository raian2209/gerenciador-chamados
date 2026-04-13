# Sistema de Gerenciamento de Chamados para Condomínio

Este projeto implementa um sistema de gerenciamento de chamados para condomínio, com separação por perfis de acesso, controle de estrutura física do condomínio e acompanhamento completo do ciclo de vida do chamado.

# Funcionalidades Seguidas 

## Administrador
- Cadastrar blocos com identificação, quantidade de andares e apartamentos por andar.

- Gerar automaticamente as unidades com padrão de identificação por bloco, andar e apartamento.

- Cadastrar moradores.

- Vincular moradores a uma ou mais unidades.

- Cadastrar Colaborador.

- Cadastrar tipos de chamados com título e prazo máximo de resolução (SLA).

- Vincular colaborador a um ou mais tipos de chamados.

- Cadastrar os status possíveis dos chamados.

- Definir um status como padrão.
  - Iniciar o Chamado com esse Status.
  - Sistema ja inicia com um Status padrão mas pode ser modificado.

- Deletar Usuarios (Soft Delete).

- Visualizar chamados dentro do seu escopo.

- Filtrar chamados.

- Alterar status dos chamados.

- Finalizar chamados.

- Comentar em chamados dentro do seu escopo.

- Visualisar é baixar anexos no chamado

## Colaborador
- Visualizar chamados dentro do seu escopo.

- Filtrar chamados.

- Alterar status dos chamados até a finalização.

- Finalizar chamados.

- Comentar em chamados dentro do seu escopo.

- Visualisar é baixar anexos no chamado


## Morador
- Estar vinculado a uma ou mais unidades por um administrador.

- Selecionar uma de suas unidades para abrir chamado.

- Abrir chamado informando tipo, descrição e anexos.

- Visualizar chamados das suas unidades.

- Comentar apenas nos próprios chamados e nos chamados das unidades às quais está vinculado.

## Chamado

- Data de inicio definida no momento em que o chamado for iniciado.
- Data de finalização definida no momento em que o chamado for concluído.

### Regras gerais do sistema
- O sistema possui blocos, andares e unidades.
- Toda unidade deve ser criada automaticamente a partir da configuração do bloco.
- Todo chamado deve iniciar com um status padrão.
- Apenas administradores e colaboradores podem alterar o status.
- A data de finalização deve ser registrada quando o chamado for concluído.
- Comentários compõem o histórico de interações do chamado.

# Detalhamento do Sistema
## O sistema gira em torno de quatro frentes principais:

- Administradores mantêm a estrutura do condomínio, usuários, vínculos, tipos de chamado e status.
- Moradores acessam suas unidades vinculadas, abrem chamados, anexam arquivos e registram comentários.
- Colaboradores acompanham os chamados acessíveis dentro do seu contexto e atualizam o andamento até a finalização.
- O banco sustenta tanto as entidades centrais quanto as regras de visibilidade, inclusive com consultas nativas para filtrar chamados por perfil.

## Principais funcionalidades

### Gestão da estrutura do condomínio

- Cadastro de blocos com identificação, quantidade de andares e apartamentos por andar.
- Geração e manutenção de unidades vinculadas ao bloco.
- Vínculo entre moradores e unidades, permitindo que um mesmo morador tenha acesso a mais de uma unidade.
- Vinculo entre colaborador e Tipo de Chamado, permitindo que um mesmo colaborador tenha acesso a mais de um tipo de chamado.

### Gestão de usuários e acesso

- Separação de perfis entre administrador, colaborador e morador.
- Autenticação e autorização com Spring Security e JWT.
- Controle de telas e operações por papel, refletido tanto nos controllers quanto nas validações de serviço.

### Operação de chamados

- Abertura de chamado por morador com unidade, tipo e descrição.
- Definição de status inicial padrão a partir de configuração persistida.
- Atualização de status e finalização por administrador ou colaborador.
- Listagem paginada e filtrada conforme perfil de acesso.
  - Listagem usa Paginação para melhor performance. 
  - Filtros para melhorar a pesquisa e experiencia do usuario.

### Histórico e evidências

- Registro de comentários vinculados ao chamado com autoria.
- Inclusão e download de anexos.
  - Inclusão:Morador
  - Download:Colaborador , Administrador
- Persistência de datas de abertura e finalização para rastreabilidade operacional.


# Padrões de Projeto Utilizados

O projeto utiliza alguns padrões de projeto de forma prática dentro da organização do projeto.

### Controller

- Os controllers em `infrastructure/controller/web` e `infrastructure/controller/api` concentram apenas a entrada e saída HTTP.
- Eles recebem requisições, delegam a execução para casos de uso e formatam a resposta da interface web ou da API.

### Service Layer

- Os services em `infrastructure/service` centralizam a regra de negócio da aplicação.
- Classes como `ChamadoService`, `UsuarioService` e `AdminService` coordenam validações, persistência e regras do domínio.

### Repository

- Os repositories em `infrastructure/repository` seguem o padrão Repository.
- Eles abstraem o acesso ao banco usando Spring Data JPA, evitando espalhar consultas SQL e JPQL pela aplicação.

### Mapper

- Os mappers em `infrastructure/mapper` seguem o padrão Mapper.
- Eles transformam entidades de domínio em DTOs de resposta, reduzindo acoplamento entre persistência e transporte de dados.

### Adapter

- O projeto também utiliza o padrão Adapter em pontos de integração com o Spring Security.
- A classe `UserDetailsImpl` em `infrastructure/security/adapter` adapta a entidade `Usuario` do domínio para a interface `UserDetails` exigida pelo framework.
- Isso permite que a autenticação do Spring trabalhe com o modelo do sistema sem acoplar a entidade diretamente ao contrato externo.

### DTO

- Os DTOs em `infrastructure/dto` seguem o padrão Data Transfer Object.
- Eles definem contratos explícitos de entrada e saída para web e API sem expor diretamente todas as entidades.

### Strategy por Papel

- A hierarquia `Usuario -> Administrador | Colaborador | Morador` aplica uma variação do padrão Strategy por especialização de comportamento.
- Cada subtipo define seu papel por meio de `getRole()` e isso influencia autenticação, autorização e fluxo de uso.

### Template do Framework

- O Spring Boot e o Spring Security aplicam internamente o padrão Template Method em pontos como autenticação, filtros e ciclo de requisição.
- O projeto aproveita isso ao plugar implementações próprias, como `AuthenticationService` e `JwtAuthenticationFilter`.

### Facade de Casos de Uso

- As interfaces em `application/UserCase` funcionam como fachadas de comportamento da aplicação.
- Elas expõem operações coesas para cada contexto, como administração, morador, colaborador, comentário e chamado.

## Estrutura do Projeto e Princípios de Arquitetura

Este projeto foi organizado em camadas que separam domínio, regras de negócio, adaptação web, persistência e segurança.

## Estrutura do projeto

### `src/main/java/br/com/dunnastecnologia/chamados/application`

- Concentra os contratos de caso de uso do sistema, como `ChamadoUseCase`, `UsuarioUseCase`, `ComentarioUseCase` e outros fluxos da aplicação.
- Também abriga modelos de apoio, como `AuthenticatedUser`, `PageRequest` e `PageResult`.
- A decisão de manter interfaces nessa camada deixa explícito o que o sistema faz, sem acoplar essa definição a controller, banco ou framework web.

### `src/main/java/br/com/dunnastecnologia/chamados/domain`

- Contém o núcleo do negócio persistido no sistema, representado por entidades como `Chamado`, `Bloco`, `Unidade`, `Usuario`, `Comentario` e `AnexoChamado`.
- Essa camada expressa os conceitos centrais do problema do condomínio e o relacionamento entre eles.
- A decisão de manter os modelos do domínio separados facilita a evolução da regra de negócio sem misturar detalhes de interface ou infraestrutura.

### `src/main/java/br/com/dunnastecnologia/chamados/infrastructure`

- Reúne a implementação concreta da aplicação.
- `controller/web` expõe os fluxos HTTP e as páginas JSP para administrador, colaborador, morador e autenticação.
- `controller/web/form` concentra objetos de entrada vindos dos formulários.
- `service` implementa os contratos definidos na camada `application`.
- `repository` encapsula o acesso ao banco com Spring Data JPA e consultas nativas.
- `mapper` converte entidades e DTOs, reduzindo acoplamento entre persistência e apresentação.
- `dto` organiza os dados trafegados entre camadas.
- `config` centraliza configurações de segurança, bootstrap inicial e views JSP.
- `security` implementa JWT, filtro de autenticação e adaptação para Spring Security.
- `exception` e `service/support` concentram tratamento de regras e lógicas transversais de apoio.

### `src/main/resources`

- `db/migration` indica o uso de migrações versionadas para controlar a estrutura do banco.
- `static` concentra CSS, JavaScript e imagens da interface.
- A separação entre código Java e recursos de interface deixa mais claro o que é backend, frontend e infraestrutura de banco.

### `src/main/webapp/WEB-INF/jsp`

- Organiza as telas por perfil de acesso, como `admin`, `colaborador`, `morador`, `auth` e `fragments`.
- A decisão de segmentar as views por papel acompanha a regra de negócio do sistema e reduz mistura entre fluxos de cada ator.

## Estrutura do sistema

## Clean Architecture no projeto

O projeto não segue uma implementação acadêmica pura de Clean Architecture, mas apresenta uma aproximação clara por camadas e por direção de responsabilidade.

### Domínio no centro

- As entidades em `domain/model` representam o núcleo do negócio e descrevem o problema do condomínio sem depender de controller web.
- A decisão de centralizar `Chamado`, `Unidade`, `Bloco`, `Usuario` e demais modelos no domínio preserva o vocabulário principal do sistema.

### Casos de uso como contrato da aplicação

- Interfaces como `ChamadoUseCase` e `ComentarioUseCase` definem o comportamento esperado do sistema em termos de ação de negócio.
- A decisão de expor contratos na camada `application` reduz acoplamento entre a regra de negócio e a tecnologia usada para executar essa regra.

### Infraestrutura como detalhe de implementação

- Controllers, repositories, JWT, JSP, DTOs e configurações ficam em `infrastructure`.
- Isso reforça a ideia de que web, banco e segurança são mecanismos de entrega e persistência, não o centro da regra de negócio.
- A implementação concreta de um caso de uso, como `ChamadoService`, fica fora da definição abstrata do caso de uso.

### Dependência apontando para dentro

- Os controllers dependem dos contratos da camada `application`, não diretamente de implementações concretas de serviço.
- Os services implementam interfaces de caso de uso, o que reduz dependência da camada superior sobre detalhes concretos.
- A decisão melhora testabilidade e troca de adaptadores, mesmo que o projeto ainda use repositories concretos da infraestrutura.

## Princípios SOLID aplicados ao projeto

### S - Single Responsibility Principle

- `MoradorWebController` cuida do fluxo web do morador, sem concentrar regra pesada de negócio.
- `ChamadoService` concentra a orquestração do caso de uso de chamados.
- `ChamadoRepository` fica responsável por persistência e consultas.
- `ChamadoAccessSupport` e `AuthenticatedUserValidator` encapsulam validações transversais de acesso.
- Essa separação reduz classes inchadas e torna mais previsível onde alterar cada comportamento.

### O - Open/Closed Principle

- A hierarquia `Usuario -> Administrador | Colaborador | Morador` permite estender comportamento por perfil sem reescrever a base comum.
- Novos casos de uso podem ser adicionados com novas interfaces e serviços sem quebrar contratos existentes.
- A decisão de usar mappers, DTOs e formulários também ajuda a estender entradas e saídas sem contaminar o domínio.
- O ponto mais forte aqui é a extensibilidade por especialização e por novos serviços.

### L - Liskov Substitution Principle

- `Administrador`, `Colaborador` e `Morador` especializam `Usuario` preservando a identidade comum do usuário.
- `Comentario` aponta para `Usuario` como autor, o que faz sentido porque qualquer subtipo válido pode ocupar esse papel.
- A decisão reforça substituição segura no domínio sem exigir estruturas paralelas para cada tipo de autor.

### I - Interface Segregation Principle

- Os contratos de caso de uso estão separados por contexto: `ChamadoUseCase`, `TipoChamadoUseCase`, `ComentarioUseCase`, `UsuarioUseCase` e outros.
- Isso evita uma interface única e inflada com todas as operações do sistema.
- Cada controller ou serviço consumidor depende apenas das operações que realmente precisa.
- A separação melhora a clareza e reduz impacto de mudanças em contextos não relacionados.

### D - Dependency Inversion Principle

- A camada web depende de abstrações da aplicação, como `ChamadoUseCase`, em vez de depender diretamente de `ChamadoService`.
- `ChamadoService` implementa o contrato da aplicação e pode ser trocado por outra implementação sem alterar o controller.
- Essa decisão reduz acoplamento entre entrada web e regra de negócio.
- O princípio aparece de forma parcial, porque a persistência ainda é acessada por repositories concretos da infraestrutura.

## Decisões arquiteturais e relação com o projeto

### Separar por `application`, `domain` e `infrastructure`

- Essa divisão torna o projeto mais legível para manutenção.
- Também deixa claro o que é regra de negócio, o que é definição de caso de uso e o que é adaptação técnica.
- No contexto deste sistema, isso ajuda porque há muitos fluxos por papel de usuário e várias regras de autorização.

### Organizar controllers por perfil

- `AdminWebController`, `ColaboradorWebController` e `MoradorWebController` seguem os atores do problema.
- A decisão conversa diretamente com o enunciado, que define permissões diferentes para cada tipo de usuário.
- Isso reduz condicionais espalhadas e deixa cada jornada mais simples de entender.

### Centralizar regra de acesso em suporte e repositório

- A validação do usuário autenticado é concentrada em componentes de apoio.
- Parte da visibilidade do chamado foi empurrada para consultas de banco, como as funções usadas em `ChamadoRepository`.
- A decisão busca consistência de autorização e evita repetir a mesma regra em muitos pontos do código.

### Separar histórico textual e binário

- `Comentario` e `AnexoChamado` foram modelados em estruturas diferentes.
- Isso faz sentido porque mensagem e arquivo possuem ciclo de vida, formato e custo de armazenamento distintos.
- Para o sistema de chamados, essa separação simplifica a evolução e a consulta do histórico.

### Usar herança para tipos de usuário

- A tabela `usuarios` guarda a identidade comum, enquanto tabelas filhas materializam os papéis.
- Essa decisão reflete diretamente a regra de negócio, que possui três tipos de usuário com capacidades diferentes.
- Também evita duplicação de `nome`, `email` e `senha` em várias tabelas independentes.

# Diagrama Relacional

## Documentação dos Modelos

Este trecho descreve o modelo relacional implementado hoje no projeto a partir das entidades JPA em `src/main/java/br/com/dunnastecnologia/chamados/domain/model`.

![Diagrama relacional](./diagrama-relacional.drawio.svg)

## Visão geral do domínio

O domínio está organizado em quatro blocos principais:

- Estrutura do condomínio: `blocos` e `unidades`.
- Identidade e acesso: `usuarios`, `administradores`, `colaboradores` e `moradores`.
- Operação do chamado: `chamados`, `status_chamado`, `tipos_chamado` e a tabela associativa `colaborador_tipo_chamado`.
- Histórico e evidências: `comentarios`, `anexos_chamado` e a tabela associativa `morador_unidade`.

## Decisões gerais de modelagem

- A hierarquia de usuários usa `@Inheritance(strategy = InheritanceType.JOINED)`, o que separa os dados comuns em `usuarios` e deixa os papéis específicos em tabelas filhas.
- O relacionamento entre morador e unidade foi modelado como muitos-para-muitos, coerente com o enunciado que permite um morador vinculado a uma ou mais unidades.
- O chamado guarda referências diretas para morador, unidade, tipo e status, reduzindo ambiguidade na auditoria de quem abriu, para qual unidade e em que estado está.
- O histórico foi separado entre comentários e anexos para manter responsabilidade clara: texto de interação em uma tabela e binário em outra.

## Tabelas

### `usuarios`

Campos principais: `id`, `nome`, `email`, `senha`, `ativo`.

- Centraliza atributos comuns de autenticação e identificação para todos os perfis.
- `email` único impede duplicidade de login entre administradores, colaboradores e moradores.
- `ativo` sustenta a estratégia de soft delete aplicada aos usuários sem quebrar integridade referencial.
- A decisão por uma tabela base evita repetição de colunas nas tabelas filhas.
- Como a estratégia é `JOINED`, consultas de um tipo específico preservam a especialização sem perder a identidade única do usuário.

### `administradores`

Campos principais: `id` herdado de `usuarios`.

- Existe como especialização explícita porque o sistema diferencia regras de acesso por papel.
- A tabela separada segue a estratégia de herança adotada no projeto.
- Mesmo sem campos extras hoje, a separação prepara o modelo para futuras permissões administrativas sem alterar `usuarios`.

### `colaboradores`

Campos principais: `id` herdado de `usuarios`.

- Representa o perfil operacional que atua apenas na gestão de chamados.
- A tabela própria mantém coerência com os demais perfis e com a regra de autorização por role.
- O escopo operacional do colaborador é materializado pela tabela associativa `colaborador_tipo_chamado`, que define por quais tipos ele é responsável.

### `moradores`

Campos principais: `id` herdado de `usuarios`.

- Separa o perfil que pode abrir chamados e comentar apenas no próprio contexto.
- A especialização permite aplicar regras de negócio específicas sem misturar com outros perfis.
- O vínculo com unidades foi extraído para uma tabela associativa, o que acomoda moradores com mais de uma unidade.

### `morador_unidade`

Campos principais: `morador_id`, `unidade_id`.

- Foi criada implicitamente pelo `@ManyToMany` entre `Morador` e `Unidade`.
- Resolve o requisito de um morador poder estar vinculado a uma ou mais unidades.
- Também deixa aberta a possibilidade de vários moradores compartilharem uma mesma unidade, caso isso seja necessário no negócio.

### `blocos`

Campos principais: `id`, `identificacao`, `quantidade_andares`, `apartamentos_por_andar`.

- Materializa a estrutura macro do condomínio pedida no enunciado.
- `identificacao` permite reconhecer cada prédio de forma direta.
- `quantidade_andares` e `apartamentos_por_andar` guardam a configuração que permite gerar unidades automaticamente.

### `unidades`

Campos principais: `id`, `identificacao`, `andar`, `bloco_id`.

- Cada unidade pertence a um único bloco, por isso o relacionamento é muitos-para-um para `blocos`.
- `andar` foi persistido na própria unidade; assim o projeto evita uma tabela intermediária de andares.
- `identificacao` concentra o padrão legível da unidade, alinhado ao requisito de reconhecer bloco, andar e apartamento.

### `tipos_chamado`

Campos principais: `id`, `titulo`, `prazo_horas`.

- Atende ao requisito administrativo de cadastrar categorias de chamado.
- `prazo_horas` traduz o SLA máximo de resolução pedido no enunciado.
- Separar o tipo do chamado evita repetir título e prazo em cada ocorrência.

### `colaborador_tipo_chamado`

Campos principais: `colaborador_id`, `tipo_chamado_id`.

- Materializa o escopo do colaborador por tipo de chamado.
- Resolve o relacionamento muitos-para-muitos entre `colaboradores` e `tipos_chamado`.
- Permite que um colaborador atue em vários tipos e que um mesmo tipo tenha vários colaboradores responsáveis.

### `status_chamado`

Campos principais: `id`, `nome`, `inicial_padrao`.

- Atende ao requisito de cadastrar os estados possíveis de um chamado.
- `inicial_padrao` existe para identificar qual status deve ser aplicado na abertura.
- Essa decisão evita fixar o status inicial em código e deixa a regra configurável pelo administrador.

### `chamados`

Campos principais: `id`, `descricao`, `data_abertura`, `data_finalizacao`, `morador_id`, `unidade_id`, `tipo_chamado_id`, `status_id`.

- É a tabela central do sistema porque conecta quem abriu, onde ocorreu, qual é o tipo e qual é o estado atual.
- `morador_id` registra o autor da abertura, importante para permissão e rastreabilidade.
- `unidade_id` garante que o chamado esteja vinculado a uma unidade específica do morador.
- `tipo_chamado_id` desacopla a classificação do incidente do registro operacional.
- `status_id` permite evolução do fluxo sem alterar a estrutura do chamado.
- `data_abertura` e `data_finalizacao` atendem ao requisito de controle temporal, inclusive a marcação quando o chamado for concluído.

### `comentarios`

Campos principais: `id`, `mensagem`, `data_criacao`, `autor_id`, `chamado_id`.

- Atende ao requisito de histórico de interações dentro do chamado.
- `autor_id` aponta para `usuarios`, o que permite comentário por morador, colaborador ou administrador sem duplicar estrutura.
- `chamado_id` garante que cada comentário esteja associado a um único fluxo de atendimento.
- `data_criacao` preserva a ordem cronológica das interações.

### `anexos_chamado`

Campos principais: `id`, `chamado_id`, `nome_arquivo`, `content_type`, `tamanho_bytes`, `conteudo`.

- Atende ao requisito de anexos no chamado.
- O relacionamento muitos-para-um com `chamados` permite vários arquivos por ocorrência.
- `content_type` e `tamanho_bytes` ajudam na validação e no tratamento de download.
- `conteudo` em `BYTEA` indica a decisão de armazenar o binário diretamente no banco, simplificando a consistência transacional.

## Relacionamentos principais

- `usuarios` 1:1 `administradores`
- `usuarios` 1:1 `colaboradores`
- `usuarios` 1:1 `moradores`
- `moradores` N:N `unidades` via `morador_unidade`
- `blocos` 1:N `unidades`
- `colaboradores` N:N `tipos_chamado` via `colaborador_tipo_chamado`
- `moradores` 1:N `chamados`
- `unidades` 1:N `chamados`
- `tipos_chamado` 1:N `chamados`
- `status_chamado` 1:N `chamados`
- `usuarios` 1:N `comentarios`
- `chamados` 1:N `comentarios`
- `chamados` 1:N `anexos_chamado`

## Aderência ao enunciado

O modelo cobre bem a base do problema:

- Estrutura de condomínio com bloco e unidade.
- Perfis de administrador, colaborador e morador.
- Vínculo de morador com uma ou mais unidades.
- Escopo de colaborador por tipo de chamado.
- Abertura de chamados com tipo, descrição, anexos e status.
- Comentários com autoria e histórico.

# Executar o Projeto

## Variáveis de ambiente

Para executar o projeto com `docker compose`, crie um arquivo `.env` na raiz com o seguinte conteúdo:

```env
DB_USER=postgres
DB_PASSWORD=postgres
DB_NAME=condominio
TOKEN=4qhq8LrEBfYcaRHxhdb9zURb2rf8e7UdEaKS9uQhRHM=
```

### Finalidade de cada variável

- `DB_USER`: usuário do PostgreSQL usado pelo container do banco e pela aplicação.
- `DB_PASSWORD`: senha do PostgreSQL usada no banco e na conexão da aplicação.
- `DB_NAME`: nome do banco de dados que será criado no container.
- `TOKEN`: segredo usado pela camada de segurança para geração e validação de autenticação baseada em token.

## Execução com Docker Compose

### Pré-requisitos

- Docker instalado.
- Docker Compose disponível no ambiente.

### Passos para subir o ambiente

1. Criar o arquivo `.env` na raiz do projeto.
2. Executar:

```bash
docker compose up --build
```

### O que será iniciado

- Serviço `db`: container PostgreSQL 16 na porta `5432`.
- Serviço `app`: aplicação Java na porta `8080`.

### Como o `docker-compose.yml` se relaciona com o projeto

- O serviço `db` usa as variáveis `DB_USER`, `DB_PASSWORD` e `DB_NAME` para inicializar o PostgreSQL.
- O serviço `app` usa essas mesmas variáveis para montar a URL JDBC e credenciais da aplicação.
- A aplicação só sobe depois de o banco estar saudável, por causa do `depends_on` com `healthcheck`.
- A decisão de compartilhar o `.env` entre `db` e `app` evita duplicação de configuração e reduz risco de inconsistência entre banco e aplicação.

### Comandos interativos úteis nos containers

Subir o ambiente:

```bash
docker compose up --build
```

Subir em background:

```bash
docker compose up -d --build
```

Ver containers em execução:

```bash
docker compose ps
```

Ver logs da aplicação:

```bash
docker compose logs -f app
```

Ver logs do banco:

```bash
docker compose logs -f db
```

Entrar em shell no container da aplicação:

```bash
docker compose exec app bash
```

Entrar em shell no container do PostgreSQL:

```bash
docker compose exec db sh
```

Abrir terminal `psql` no banco:

```bash
docker compose exec db psql -U ${DB_USER} -d ${DB_NAME}
```

Listar tabelas no PostgreSQL:

```sql
\dt
```

Consultar histórico do Flyway:

```sql
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

Testar a API de login via linha de comando:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@condominio.local","senha":"admin123"}'
```

Testar a API autenticada com token JWT:

```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

Parar o ambiente:

```bash
docker compose down
```

Parar e remover volumes:

```bash
docker compose down -v
```

## Inicialização e Credenciais Padrão (Bootstrap)

### `src/main/java/br/com/dunnastecnologia/chamados/infrastructure/config/AdminBootstrapConfig.java`

### Credenciais de Acesso (Administrador)
Se o banco de dados estiver vazio, o sistema criará automaticamente um usuário administrador padrão. Você pode usar essas credenciais para fazer o primeiro login no painel:

- E-mail: admin@condominio.local

- Senha: admin123

(Nota: Estes valores podem ser facilmente customizados alterando as propriedades app.bootstrap.admin.* no seu arquivo application.properties ou injetando variáveis de ambiente no Docker).

### Status de Chamados Obrigatórios
A inicialização também garante a integridade do fluxo de trabalho do condomínio. O código verifica e cria automaticamente os status essenciais do sistema:

- Solicitado: É o status de entrada. O sistema é configurado para marcá-lo automaticamente como o Status Inicial Padrão, ou seja, todo novo chamado aberto por um morador cairá neste status.

- Finalizado: Garante que o status de encerramento do ciclo de vida de um chamado sempre exista no banco de dados.

### Acessando a Aplicação

- Interface Web (Página de Login): http://localhost:8080/

## Migrations do Banco

As migrations do projeto ficam em [`src/main/resources/db/migration`](</home/raimundo/ProcessoCeletivo/Dunnas/gerenciador-chamados/src/main/resources/db/migration>).

O projeto usa Flyway para versionar a estrutura do banco e a evolução das funções SQL usadas pela aplicação.

### Como a pasta está organizada

- arquivos no formato `V{numero}__descricao.sql`
- cada arquivo representa uma etapa versionada da evolução do banco
- a execução acontece em ordem crescente de versão

### Responsabilidade das migrations atuais

- `V1`: estrutura inicial das tabelas e índices principais
- `V2`: regras de autorização do morador
- `V3`: regras administrativas e geração automática de unidades
- `V4`: regras operacionais do colaborador
- `V5`: funções de apoio usadas pelos repositories
- `V6`: consolidação das regras de visibilidade dos chamados
- `V7`: suporte ao status inicial padrão
- `V8`: estrutura de anexos dos chamados
- `V9`: filtro administrativo por morador
- `V10`: filtro administrativo por prefixo do nome do morador
- `V11`: filtros do colaborador por tipo e unidade
- `V12`: suporte a soft delete de usuários
- `V13`: escopo do colaborador por tipos de chamado

### Convenções adotadas

- as migrations foram comentadas internamente para separar blocos por responsabilidade
- os nomes versionados existentes foram preservados para não quebrar o histórico do Flyway
- a semântica foi melhorada com cabeçalhos e seções dentro dos próprios arquivos

### Cuidados ao evoluir migrations

- nunca altere a ordem das versões já existentes
- para novas mudanças, crie um novo arquivo `V{proxima_versao}__descricao.sql`
- prefira descrições curtas e objetivas no nome do arquivo
- agrupe o conteúdo por blocos comentados quando a migration tiver mais de uma responsabilidade técnica

### Atenção em ambientes já executados

Se uma migration antiga já tiver sido aplicada em algum banco, mudar o conteúdo dela pode gerar divergência de checksum no Flyway.

Nesse cenário:

- evite reescrever migrations já executadas em produção
- prefira criar uma nova migration corretiva
- se a alteração em arquivo antigo já tiver acontecido, pode ser necessário executar `flyway repair` antes de subir a aplicação
