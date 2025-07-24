# Tecnologias, Estruturas e Padrões do Projeto Delivery

## Tecnologias Utilizadas

### Backend (Java)
- **Java 21**: Linguagem principal utilizada para o desenvolvimento dos microserviços.
- **Spring Boot 3.5.3**: Framework para construção de aplicações Java, facilitando a configuração e o desenvolvimento de aplicações web e APIs REST.
- **Spring Data JPA**: Abstração para persistência de dados, facilitando a integração com bancos relacionais.
- **Spring Validation**: Validação de dados de entrada nas APIs.
- **Lombok**: Reduz a verbosidade do código Java, gerando automaticamente getters, setters, equals, hashCode, etc.
- **JUnit 5**: Framework de testes unitários.
- **PostgreSQL**: Banco de dados relacional utilizado para persistência dos dados.
- **Docker Compose**: Orquestração de containers para banco de dados e ferramentas auxiliares.
- **PgAdmin**: Interface gráfica para administração do PostgreSQL.

## Estrutura de Pastas e Responsabilidades

```
Delivery/
├── docker-compose.yml         # Orquestração dos containers (PostgreSQL, PgAdmin)
├── MServices/                 # Diretório dos microserviços
│   ├── Courier-Management/    # Microserviço de gestão de entregadores
│   │   ├── pom.xml            # Configuração Maven e dependências
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/edudeveloper/delivery/Courier/Management/
│   │   │   │   │   └── CourierManagementApplication.java # Classe principal do microserviço
│   │   │   │   └── resources/
│   │   │   │       ├── application.properties # Configurações do Spring Boot
│   │   │   │       ├── static/                # Recursos estáticos (vazio)
│   │   │   │       └── templates/             # Templates (vazio)
│   │   │   └── test/java/com/edudeveloper/delivery/Courier/Management/
│   │   │       └── CourierManagementApplicationTests.java # Teste de contexto Spring
│   ├── Delivery-Trackin/      # Microserviço de rastreamento de entregas
│   │   ├── pom.xml            # Configuração Maven e dependências
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/edudeveloper/delivery/Delivery/Trackin/
│   │   │   │   │   ├── DeliveryTrackinApplication.java # Classe principal do microserviço
│   │   │   │   │   ├── api/                    # (Vazio, reservado para controladores REST)
│   │   │   │   │   ├── domain/model/           # Entidades de domínio
│   │   │   │   │   │   ├── Delivery.java       # Entidade de entrega
│   │   │   │   │   │   ├── Item.java           # Entidade de item da entrega
│   │   │   │   │   │   └── DeliveryStatus.java # Enum de status da entrega
│   │   │   │   │   └── infrastructure/         # (Vazio, reservado para infraestrutura)
│   │   │   │   └── resources/
│   │   │   │       ├── application.properties  # Configurações do Spring Boot
│   │   │   │       ├── static/                 # Recursos estáticos (vazio)
│   │   │   │       └── templates/              # Templates (vazio)
│   │   │   └── test/java/com/edudeveloper/delivery/Delivery/Trackin/
│   │   │       └── DeliveryTrackinApplicationTests.java # Teste de contexto Spring
├── README.md                  # Descrição do projeto
├── UML.png                    # Diagrama UML (não analisado)
```

## Responsabilidades dos Arquivos Principais

- **CourierManagementApplication.java / DeliveryTrackinApplication.java**: Pontos de entrada dos microserviços, inicializam o contexto Spring Boot.
- **application.properties**: Configurações específicas de cada microserviço (nome da aplicação, banco, etc).
- **Delivery.java**: Entidade de domínio que representa uma entrega, com atributos como id, courierId, status, datas, valores e lista de itens.
- **Item.java**: Entidade de domínio que representa um item de uma entrega, com id, nome e quantidade.
- **DeliveryStatus.java**: Enum que define os possíveis status de uma entrega (DRAFT, WAITING_FOR_COURIER, IN_TRANSIT, DELIVERY).
- **CourierManagementApplicationTests.java / DeliveryTrackinApplicationTests.java**: Testes de contexto para garantir que a aplicação Spring Boot sobe corretamente.
- **docker-compose.yml**: Orquestração dos containers de banco de dados e ferramentas administrativas.

## Padrão de Projeto Utilizado

O projeto segue o padrão **Domain-Driven Design (DDD)** em sua estrutura inicial, separando claramente os pacotes de domínio (`domain/model`), infraestrutura (`infrastructure`) e API (`api`). Utiliza também o padrão de microserviços, onde cada contexto de negócio (gestão de entregadores, rastreamento de entregas) é implementado como um serviço independente, com seu próprio ciclo de vida, dependências e banco de dados.

Além disso, o uso do Spring Boot favorece a aplicação do padrão **Injeção de Dependência** e a configuração baseada em anotações, promovendo baixo acoplamento e alta coesão entre os componentes.

---

*Arquivo gerado automaticamente para documentação das tecnologias e arquitetura do projeto Delivery.* 