package com.mbauspesalq.ecommerce.estoque.service

import com.mbauspesalq.ecommerce.estoque.dto.ProdutoRequest
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoResponse
import com.mbauspesalq.ecommerce.estoque.model.Produto
import com.mbauspesalq.ecommerce.estoque.repository.ProdutoRepository
import org.springframework.stereotype.Service

@Service
class ProdutoService(
    private val repository: ProdutoRepository
) {
    fun listaTudo(): List<ProdutoResponse> = repository.findAll().map { it.toResponse() }

    fun buscaPeloIdProduto(idProduto: Long): ProdutoResponse? =
        repository.findById(idProduto).orElse(null)?.toResponse()

    fun buscaPelaCategoria(nomeCategoria: String): List<ProdutoResponse> =
        repository.findByCategoria(nomeCategoria).map { it.toResponse() }

    fun adicionaProduto(produtoRequest: ProdutoRequest): ProdutoResponse {
        val produto = Produto(
            nomeProduto = produtoRequest.nomeProduto,
            categoria = produtoRequest.categoria,
            quantidade = produtoRequest.quantidade,
            preco = produtoRequest.preco
        )
        return repository.save(produto).toResponse()
    }

    fun atualizaProduto(idProduto: Long, produtoAtualizado: ProdutoRequest): ProdutoResponse? {
        return if (repository.existsById(idProduto)) {
            val produto = Produto(
                id = idProduto,
                nomeProduto = produtoAtualizado.nomeProduto,
                categoria = produtoAtualizado.categoria,
                quantidade = produtoAtualizado.quantidade,
                preco = produtoAtualizado.preco
            )
            repository.save(produto).toResponse()
        } else {
            null
        }
    }

    fun deletaProduto(idProduto: Long): Boolean {
        return if (repository.existsById(idProduto)) {
            repository.deleteById(idProduto)
            true
        } else {
            false
        }
    }
}