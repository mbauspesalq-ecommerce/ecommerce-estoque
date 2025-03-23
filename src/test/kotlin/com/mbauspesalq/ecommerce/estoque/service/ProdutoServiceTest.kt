package com.mbauspesalq.ecommerce.estoque.service

import com.mbauspesalq.ecommerce.estoque.dto.ProdutoEstoqueRequest
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoRequest
import com.mbauspesalq.ecommerce.estoque.model.Produto
import com.mbauspesalq.ecommerce.estoque.repository.ProdutoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.Test

class ProdutoServiceTest {
    private val repository: ProdutoRepository = mock()
    private val service: ProdutoService = ProdutoService(repository)

    @Nested
    inner class ListaTudoTest {
        @Test
        fun `deve listar todos os produtos`() {
            val produtos = listOf(
                Produto(1L, "Produto A", "Eletrônicos", 10, 99.99),
                Produto(2L, "Produto B", "Roupas", 5, 49.99)
            )

            whenever(repository.findAll()).thenReturn(produtos)

            val resultado = service.listaTudo()

            assertEquals(2, resultado.size)
            assertEquals("Produto A", resultado[0].nomeProduto)
        }
    }

    @Nested
    inner class BuscaPeloIdProdutoTest {
        @Test
        fun `deve retornar produto quando encontrado`() {
            val produto = Produto(1L, "Produto A", "Eletrônicos", 10, 99.99)

            whenever(repository.findById(1L)).thenReturn(Optional.of(produto))

            val resultado = service.buscaPeloIdProduto(1L)

            assertNotNull(resultado)
            assertEquals("Produto A", resultado?.nomeProduto)
        }

        @Test
        fun `deve retornar null quando produto não existe`() {
            whenever(repository.findById(99L)).thenReturn(Optional.empty())

            val resultado = service.buscaPeloIdProduto(99L)

            assertNull(resultado)
        }
    }

    @Nested
    inner class BuscaPelaCategoriaTest {
        @Test
        fun `deve retornar produtos de uma categoria`() {
            val produtos = listOf(Produto(1L, "Produto A", "Eletrônicos", 10, 99.99))

            whenever(repository.findByCategoria("Eletrônicos")).thenReturn(produtos)

            val resultado = service.buscaPelaCategoria("Eletrônicos")

            assertEquals(1, resultado.size)
            assertEquals("Produto A", resultado[0].nomeProduto)
        }
    }

    @Nested
    inner class AdicionaProdutoTest {
        @Test
        fun `deve adicionar um novo produto`() {
            val request = ProdutoRequest("Produto A", "Eletrônicos", 10, 99.99)
            val produto = Produto(1L, "Produto A", "Eletrônicos", 10, 99.99)

            whenever(repository.save(any())).thenReturn(produto)

            val resultado = service.adicionaProduto(request)

            assertEquals("Produto A", resultado.nomeProduto)
        }
    }

    @Nested
    inner class AtualizaProdutoTest {
        @Test
        fun `deve atualizar um produto existente`() {
            val request = ProdutoRequest("Produto Atualizado", "Eletrônicos", 20, 199.99)

            whenever(repository.existsById(1L)).thenReturn(true)
            whenever(repository.save(any())).thenReturn(
                Produto(1L, "Produto Atualizado", "Eletrônicos", 20, 199.99)
            )

            val resultado = service.atualizaProduto(1L, request)

            assertNotNull(resultado)
            assertEquals("Produto Atualizado", resultado?.nomeProduto)
        }

        @Test
        fun `deve retornar null ao tentar atualizar produto inexistente`() {
            val request = ProdutoRequest("Produto Novo", "Eletrônicos", 10, 99.99)

            whenever(repository.existsById(99L)).thenReturn(false)

            val resultado = service.atualizaProduto(99L, request)

            assertNull(resultado)
        }
    }

    @Nested
    inner class DeletaProdutoTest {
        @Test
        fun `deve deletar produto existente`() {
            whenever(repository.existsById(1L)).thenReturn(true)
            doNothing().whenever(repository).deleteById(1L)

            val resultado = service.deletaProduto(1L)

            assertTrue(resultado)
        }

        @Test
        fun `deve retornar false ao tentar deletar produto inexistente`() {
            whenever(repository.existsById(99L)).thenReturn(false)

            val resultado = service.deletaProduto(99L)

            assertFalse(resultado)
        }
    }


    @Nested
    inner class SubtraiEstoqueTest {

        @Test
        fun `deve subtrair estoque quando há quantidade suficiente`() {
            val produto = Produto(1L, "Produto A", "Eletrônicos", 10, 99.99)
            val request = listOf(ProdutoEstoqueRequest(1L, 5))

            whenever(repository.findById(1L)).thenReturn(Optional.of(produto))

            val indisponiveis = service.subtraiEstoque(request)

            assertTrue(indisponiveis.isEmpty())
            assertEquals(5, produto.quantidade)
            verify(repository, times(1)).save(produto)
        }

        @Test
        fun `deve retornar lista com produtos indisponíveis quando não há estoque suficiente`() {
            val produto = Produto(1L, "Produto A", "Eletrônicos", 3, 99.99)
            val request = listOf(ProdutoEstoqueRequest(1L, 5))

            whenever(repository.findById(1L)).thenReturn(Optional.of(produto))

            val indisponiveis = service.subtraiEstoque(request)

            assertEquals(1, indisponiveis.size)
            assertEquals(1L, indisponiveis[0].idProduto)
            verify(repository, never()).save(any())
        }

        @Test
        fun `deve retornar produto como indisponível quando não existir no banco`() {
            val request = listOf(ProdutoEstoqueRequest(99L, 2))

            whenever(repository.findById(99L)).thenReturn(Optional.empty())

            val indisponiveis = service.subtraiEstoque(request)

            assertEquals(1, indisponiveis.size)
            assertEquals(99L, indisponiveis[0].idProduto)
            verify(repository, never()).save(any())
        }
    }

    @Nested
    inner class DevolveEstoqueTest {

        @Test
        fun `deve devolver estoque corretamente`() {
            val produto = Produto(1L, "Produto A", "Eletrônicos", 5, 99.99)
            val request = listOf(ProdutoEstoqueRequest(1L, 3))

            whenever(repository.findById(1L)).thenReturn(Optional.of(produto))

            val indisponiveis = service.devolveEstoque(request)

            assertTrue(indisponiveis.isEmpty())
            assertEquals(8, produto.quantidade)
            verify(repository, times(1)).save(produto)
        }

        @Test
        fun `deve retornar produto como indisponível quando não existir no banco`() {
            val request = listOf(ProdutoEstoqueRequest(99L, 2))

            whenever(repository.findById(99L)).thenReturn(Optional.empty())

            val indisponiveis = service.devolveEstoque(request)

            assertEquals(1, indisponiveis.size)
            assertEquals(99L, indisponiveis[0].idProduto)
            verify(repository, never()).save(any())
        }
    }
}