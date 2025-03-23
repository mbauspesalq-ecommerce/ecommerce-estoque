package com.mbauspesalq.ecommerce.estoque.dto

data class ProdutoEstoqueRequest(
    val idProduto: Long,
    val quantidadeRequerida: Int
)
