package com.mbauspesalq.ecommerce.estoque.controller

import com.mbauspesalq.ecommerce.estoque.dto.ProdutoEstoqueRequest
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoRequest
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoResponse
import com.mbauspesalq.ecommerce.estoque.service.ProdutoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos em estoque", description = "Endpoints de gerenciamento de produtos em estoque")
class ProdutoController(
    private val service: ProdutoService
) {

    @Operation(summary = "Listar todos os produtos.")
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso.")
    @GetMapping
    fun listaTudo(): ResponseEntity<List<ProdutoResponse>> = ResponseEntity.ok(service.listaTudo())

    @Operation(summary = "Buscar produto pelo ID.")
    @ApiResponse(responseCode = "200", description = "Produto encontrado.")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado.", content = [Content()])
    @GetMapping("/{idProduto}")
    fun buscaPeloIdProduto(@PathVariable idProduto: Long): ResponseEntity<ProdutoResponse> =
        service.buscaPeloIdProduto(idProduto)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @Operation(summary = "Buscar produtos pelo nome da categoria.")
    @ApiResponse(responseCode = "200", description = "Produtos retornados com sucesso.")
    @ApiResponse(responseCode = "404", description = "Nenhum produto encontrado para a categoria.", content = [Content()])
    @GetMapping("/categorias/{nomeCategoria}")
    fun buscaPelaCategoria(@PathVariable nomeCategoria: String): ResponseEntity<List<ProdutoResponse>> =
        service.buscaPelaCategoria(nomeCategoria)
            .takeIf { it.isNotEmpty() }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @Operation(summary = "Adicionar novo produto.")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso.")
    @PostMapping
    fun adicionaProduto(@RequestBody produtoRequest: ProdutoRequest): ResponseEntity<ProdutoResponse> {
        val produtoResponse = service.adicionaProduto(produtoRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoResponse)
    }

    @Operation(summary = "Atualizar produto existente pelo ID.")
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso.")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado.", content = [Content()])
    @PutMapping("/{idProduto}")
    fun atualizaProduto(
        @PathVariable idProduto: Long,
        @RequestBody produto: ProdutoRequest
    ): ResponseEntity<ProdutoResponse> {
        return service.atualizaProduto(idProduto, produto)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @Operation(summary = "Deletar produto pelo ID.")
    @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso.")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado.", content = [Content()])
    @DeleteMapping("/{idProduto}")
    fun deletaProduto(@PathVariable idProduto: Long): ResponseEntity<Void> {
        return if (service.deletaProduto(idProduto)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @Operation(summary = "Subtrair estoque. Usado pelo microsserviço ecommerce-pagamentos para reservar produtos no fluxo de pagamento de carrinho.")
    @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso.")
    @ApiResponse(responseCode = "422", description = "Produtos com estoque insuficiente.")
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

    @Operation(summary = "Devolver estoque. Usado pelo microsserviço ecommerce-pagamentos para devolver produtos ao estoque caso haja falha no processo.")
    @ApiResponse(responseCode = "200", description = "Produtos devolvidos ao estoque com sucesso.")
    @ApiResponse(responseCode = "422", description = "Produtos não encontrados.")
    @PostMapping("/devolve-estoque")
    fun devolveEstoque(@RequestBody produtos: List<ProdutoEstoqueRequest>): ResponseEntity<String> {
        val produtosIndisponiveis = service.devolveEstoque(produtos)
        return if (produtosIndisponiveis.isEmpty()) {
            ResponseEntity.ok("Produtos devolvidos ao estoque com sucesso.")
        } else {
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Produtos não encontrados: ${produtosIndisponiveis.joinToString { it.idProduto.toString() }}")
        }
    }
}
