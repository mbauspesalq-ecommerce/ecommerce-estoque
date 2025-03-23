package com.mbauspesalq.ecommerce.estoque.controller

import com.mbauspesalq.ecommerce.estoque.dto.ProdutoEstoqueRequest
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoRequest
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoResponse
import com.mbauspesalq.ecommerce.estoque.service.ProdutoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/produtos")
class ProdutoController(
    private val service: ProdutoService
) {

    @GetMapping
    fun listaTudo(): ResponseEntity<List<ProdutoResponse>> = ResponseEntity.ok(service.listaTudo())

    @GetMapping("/{idProduto}")
    fun buscaPeloIdProduto(@PathVariable idProduto: Long): ResponseEntity<ProdutoResponse> =
        service.buscaPeloIdProduto(idProduto)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    @GetMapping("/categorias/{nomeCategoria}")
    fun buscaPelaCategoria(@PathVariable nomeCategoria: String): ResponseEntity<List<ProdutoResponse>> =
        service.buscaPelaCategoria(nomeCategoria)
            .takeIf { it.isNotEmpty() }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun adicionaProduto(@RequestBody produtoRequest: ProdutoRequest): ResponseEntity<ProdutoResponse> {
        val produtoResponse = service.adicionaProduto(produtoRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoResponse)
    }

    @PutMapping("/{idProduto}")
    fun atualizaProduto(
        @PathVariable idProduto: Long,
        @RequestBody produto: ProdutoRequest
    ): ResponseEntity<ProdutoResponse> {
        return service.atualizaProduto(idProduto, produto)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{idProduto}")
    fun deletaProduto(@PathVariable idProduto: Long): ResponseEntity<Void> {
        return if (service.deletaProduto(idProduto)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/subtrai-estoque")
    fun subtraiEstoque(@RequestBody produtos: List<ProdutoEstoqueRequest>): ResponseEntity<String> {
        val produtosIndisponiveis = service.subtraiEstoque(produtos)

        return if (produtosIndisponiveis.isEmpty()) {
            ResponseEntity.ok("Estoque atualizado com sucesso.")
        } else {
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Produtos com estoque insuficiente: ${produtosIndisponiveis.joinToString { it.idProduto.toString() }}")
        }
    }

    @PostMapping("/devolve-estoque")
    fun devolveEstoque(@RequestBody produtos: List<ProdutoEstoqueRequest>): ResponseEntity<String> {
        val produtosIndisponiveis = service.devolveEstoque(produtos)
        return if (produtosIndisponiveis.isEmpty()) {
            ResponseEntity.ok("Produtos devolvidos ao estoque com sucesso.")
        } else {
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Produtos com estoque insuficiente: ${produtosIndisponiveis.joinToString { it.idProduto.toString() }}")
        }
    }
}