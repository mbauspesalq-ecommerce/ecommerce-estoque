# Microsserviço Estoque

## Objetivo
Este microsserviço foi desenvolvido como parte do Trabalho de Conclusão de Curso (TCC) do MBA em Engenharia de Software da USP/Esalq. Ele exemplifica a aplicação da arquitetura de microsserviços no contexto de um e-commerce, fornecendo funcionalidades relacionadas à gestão do estoque dos produtos do e-commerce.

## Descrição
O microsserviço Estoque responsável por:

- Gerenciar o estoque de produtos do e-commerce.
- Permitir a adição, atualização e deleção de produtos.

## Endpoints

## Tecnologias Utilizadas
- Spring Boot
- Kotlin
- JVM 17
- Maven
- PostgreSQL 15
- Flyway

## Como Executar
### Requisitos
Antes de executar o microsserviço, certifique-se de ter instalado:
- Docker
- Java 17+
- Maven

### Executando com Docker
1. Clone o repositório:
```bash
git clone https://github.com/mbauspesalq-ecommerce/ecommerce-estoque.git
cd ecommerce-inventory-cart
```
2. Inicie os contêineres:
```bash
docker-compose up -d
```
3. Execute a aplicação:
```bash
mvn spring-boot:run
```

## Migrações de Banco de Dados
As migrações são gerenciadas pelo Flyway e os scripts SQL devem ser adicionados no diretório `src/main/resources/db/migration`.
Para rodar as migrações manualmente:
```bash
mvn flyway:migrate
```