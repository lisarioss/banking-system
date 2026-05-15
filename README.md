# Banking System

Projeto de microsserviços bancários em Java Spring Boot.

## Visão Geral

Este repositório contém três serviços independentes:

- `user-service` — API de autenticação e gerenciamento de usuários
- `account-service` — gerenciamento de contas bancárias
- `transaction-service` — processamento de transações e operações financeiras

> Observação: o `pom.xml` raiz referencia outros módulos, mas o workspace atual contém apenas os três serviços acima.

## Requisitos

- Java 17
- Maven 3.9+
- PostgreSQL
- Kafka (para `transaction-service`, caso você use o produtor Kafka configurado)
- Docker (opcional, para criar imagens)

## Configuração local

Cada serviço possui um `application.yml` em `src/main/resources` com configurações padrão.

Valores padrão de banco de dados:

- `user-service`: `jdbc:postgresql://localhost:5432/user_service_db`
- `account-service`: `jdbc:postgresql://localhost:5432/account_service_db`
- `transaction-service`: `jdbc:postgresql://localhost:5432/transaction_service_db`

Kafka padrão em `transaction-service`:

- `localhost:9092`

## Build

Para gerar os artefatos em cada serviço:

```bash
cd user-service && mvn clean package
cd ../account-service && mvn clean package
cd ../transaction-service && mvn clean package
```

## Execução

Executar cada serviço individualmente usando o JAR resultante:

```bash
cd user-service && java -jar target/user-service-1.0.0.jar
cd ../account-service && java -jar target/account-service-1.0.0.jar
cd ../transaction-service && java -jar target/transaction-service-1.0.0.jar
```

### Endpoints padrões

- `user-service`:
  - Base URL: `http://localhost:8081/user-service`
- `account-service`:
  - Base URL: `http://localhost:8082/account-service`
- `transaction-service`:
  - Base URL: `http://localhost:8082/transaction-service`

> Aviso: `account-service` e `transaction-service` estão configurados com o mesmo `server.port` (`8082`). Ajuste a porta de um dos serviços antes de rodar os dois simultaneamente.

## Docker

Cada serviço tem um `Dockerfile` que copia o JAR gerado para a imagem.

Build de imagem:

```bash
cd account-service && mvn clean package && docker build -t bank-system/account-service .
cd ../transaction-service && mvn clean package && docker build -t bank-system/transaction-service .
cd ../user-service && mvn clean package && docker build -t bank-system/user-service .
```

## Variáveis de ambiente úteis

Para sobrescrever configurações no runtime, utilize variáveis de ambiente do Spring Boot:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `SERVER_PORT`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`

## Observações

- Os serviços usam PostgreSQL e precisam de bancos separados por serviço.
- O `transaction-service` pode depender de um broker Kafka se o fluxo Kafka estiver em uso.
- Ajuste as portas e URLs conforme necessário para execução simultânea.

## Passos recomendados

1. Subir bancos PostgreSQL locais.
2. Ajustar `application.yml` ou variáveis de ambiente.
3. Buildar e iniciar cada serviço.
4. Testar os endpoints básicos e o fluxo de criação/consulta de contas.
