package com.mbauspesalq.ecommerce.estoque.dto

data class ProdutoRequest(
    val nomeProduto: String,
    val categoria: String,
    val quantidade: Int,
    val preco: Double
)
