# Microsserviço mbauspesalq-ecommerce-estoque

## Objetivo

Este microsserviço foi desenvolvido como parte do Trabalho de Conclusão de Curso (TCC) do MBA em Engenharia de Software da USP/Esalq. Ele exemplifica a aplicação da arquitetura de microsserviços no contexto de um e-commerce, fornecendo funcionalidades relacionadas à gestão do estoque dos produtos do e-commerce.

## Descrição

O microsserviço Estoque responsável por:

- Gerenciar o estoque de produtos do e-commerce.
- Permitir a adição, atualização e deleção de produtos.
- Verificar se há produtos em estoque quando o microsserviço ecommerce-pagamentos envia uma requisição.

## Integrações

| Ação                      | Integração Envolvida      |
|---------------------------|---------------------------|
| Controle de estoque: CRUD | Banco de Dados PostgreSQL |

## Endpoints

### 1. Listar todos os produtos (GET /api/produtos)

Retorna a lista de todos os produtos disponíveis no estoque.

Resposta (200 OK):
```json
[
  {
    "id": 1,
    "nome_produto": "Produto A",
    "categoria": "Eletrônicos",
    "quantidade": 10,
    "preco": 99.90
  }
]
```

### 2. Buscar produto por ID (GET /api/produtos/{idProduto})

Retorna os detalhes de um produto específico pelo seu ID.

Resposta (200 OK):
```json
{
  "id": 1,
  "nome": "Produto A",
  "categoria": "Eletrônicos",
  "preco": 99.90,
  "quantidade": 10
}
```

Erro (404 Not Found): Produto não encontrado.

### 3. Buscar produtos por categoria (GET /api/produtos/categorias/{nomeCategoria})

Retorna os produtos que pertencem a uma determinada categoria.

Resposta (200 OK):
```json
[
  {
    "id": 1,
    "nome": "Produto A",
    "categoria": "Eletrônicos",
    "preco": 99.90,
    "quantidade": 10
  }
]
```

Erro (404 Not Found): Nenhum produto encontrado para a categoria.

### 4. Adicionar um novo produto (POST /api/produtos)

Adiciona um novo produto ao estoque.

Payload:
```json
{
  "nome": "Produto B",
  "categoria": "Vestuário",
  "preco": 59.90,
  "quantidade": 20
}
```

Resposta (201 Created):
```json
{
  "id": 2,
  "nome": "Produto B",
  "categoria": "Vestuário",
  "preco": 59.90,
  "quantidade": 20
}
```

### 5. Atualizar um produto existente (PUT /api/produtos/{idProduto})

Atualiza os dados de um produto existente.

Payload (PUT /api/produtos/3):
```json
{
  "nome": "Produto C",
  "categoria": "Eletrônicos",
  "preco": 199.90,
  "quantidade": 15
}
```

Resposta (200 OK):
```json
{
  "id": 3,
  "nome": "Produto C",
  "categoria": "Eletrônicos",
  "preco": 199.90,
  "quantidade": 15
}
```

Erro (404 Not Found): Produto não encontrado.

### 6. Excluir um produto (DELETE /api/produtos/{idProduto})

Remove um produto do estoque.

Resposta (204 No Content): Produto excluído.

Erro (404 Not Found): Produto não encontrado.

### 7.  Subtrair estoque (POST /api/produtos/subtrai-estoque})

Subtrai a quantidade de produtos no estoque após uma compra, no processo de pagamento.

Payload:
```json
[
  {
    "idProduto": 1,
    "quantidadeRequerida": 2
  }
]
```

Resposta (200 OK): Estoque atualizado.

Erro (422 Unprocessable Entity): Produtos com estoque insuficiente.

### 8.  Devolver  estoque (POST /api/produtos/devolve-estoque})

Reverte a subtração do estoque em caso de falha no pagamento.

Payload:
```json
[
  {
    "idProduto": 1,
    "quantidadeRequerida": 2
  }
]
```

Resposta (200 OK): Produtos devolvidos ao estoque.

Erro (422 Unprocessable Entity): Não foi possível devolver o estoque.

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

### Executando todo o ecommerce com docker-compose

Para que todo o fluxo de pagamento seja validado, precisaremos subir o docker-compose com toda a infraestrutura do fluxo
de pagamento e estoque do ecommerce.

Para isso, deveremos baixar o docker-compose.yml do link: [TBD]

### Executando com Docker local - somente esse microsserviço

1. Clone o repositório:
```bash
git clone https://github.com/mbauspesalq-ecommerce/ecommerce-estoque.git
cd ecommerce-estoque
```

2. Inicie os contêineres:
```bash
docker-compose up -d
```

3. Execute a aplicação:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Migrações de Banco de Dados

As migrações são gerenciadas pelo Flyway e os scripts SQL devem ser adicionados no diretório `src/main/resources/db/migration`.
Para rodar as migrações manualmente:

```bash
mvn flyway:migrate
```