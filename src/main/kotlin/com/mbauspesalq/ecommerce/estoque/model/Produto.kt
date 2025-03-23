package com.mbauspesalq.ecommerce.estoque.model

import com.mbauspesalq.ecommerce.estoque.dto.ProdutoResponse
import jakarta.persistence.*

@Entity
@Table(name = "estoque")
class Produto (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "nome_produto", nullable = false)
    val nomeProduto: String,

    @Column(nullable = false)
    val categoria: String,

    @Column(nullable = false)
    val quantidade: Int,

    @Column(nullable = false)
    val preco: Double,
) {
    fun toResponse(): ProdutoResponse = ProdutoResponse(
        id = this.id!!,
        nomeProduto = this.nomeProduto,
        categoria = this.categoria,
        quantidade = this.quantidade,
        preco = this.preco
    )
}