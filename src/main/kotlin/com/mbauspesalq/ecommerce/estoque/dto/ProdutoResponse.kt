package com.mbauspesalq.ecommerce.estoque.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProdutoResponse(
    val id: Long,
    @JsonProperty("nome_produto")
    val nomeProduto: String,
    val categoria: String,
    val quantidade: Int,
    @JsonProperty("preço")
    val preco: Double
)
