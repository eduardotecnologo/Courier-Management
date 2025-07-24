# Delivery - Sistema de Gestão e Rastreamento de Entregas

Este projeto é composto por uma arquitetura de microserviços desenvolvida em Java, utilizando o framework Spring Boot, com o objetivo de gerenciar entregadores e rastrear entregas de forma eficiente e escalável.

## Visão Geral
O sistema Delivery é dividido em dois microserviços principais:

- **Courier-Management**: Responsável pela gestão dos entregadores (couriers), incluindo cadastro, atualização e controle de informações dos profissionais responsáveis pelas entregas.
- **Delivery-Trackin**: Responsável pelo rastreamento das entregas, controle de status, itens transportados e informações relacionadas ao processo logístico.

## Tecnologias Utilizadas
- Java 21
- Spring Boot 3.5.3
- Spring Data JPA
- Spring Validation
- Lombok
- PostgreSQL
- Docker Compose
- PgAdmin
- JUnit 5

## Arquitetura e Padrões
O projeto adota o padrão de **Domain-Driven Design (DDD)**, separando claramente as camadas de domínio, infraestrutura e API. Cada microserviço possui seu próprio ciclo de vida, dependências e banco de dados, seguindo o conceito de microserviços independentes.

A orquestração dos serviços de banco de dados e ferramentas administrativas é realizada via Docker Compose, facilitando o setup e a escalabilidade do ambiente.

## Estrutura de Pastas
- `MServices/` - Diretório principal dos microserviços
  - `Courier-Management/` - Microserviço de gestão de entregadores
  - `Delivery-Trackin/` - Microserviço de rastreamento de entregas
- `docker-compose.yml` - Orquestração dos containers de infraestrutura
- `README.md` - Documentação do projeto
- `tecnolog.md` - Detalhamento das tecnologias e arquitetura

---

**Desenvolvedor:** Eduardo Tecnologo
