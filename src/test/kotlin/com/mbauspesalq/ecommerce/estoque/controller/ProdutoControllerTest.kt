package com.mbauspesalq.ecommerce.estoque.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoEstoqueRequest
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoRequest
import com.mbauspesalq.ecommerce.estoque.dto.ProdutoResponse
import com.mbauspesalq.ecommerce.estoque.service.ProdutoService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.Test

class ProdutoControllerTest {
    private val produtoService: ProdutoService = mock()
    private val produtoController: ProdutoController = ProdutoController(produtoService)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(produtoController).build()
    private val objectMapper = ObjectMapper()

    private lateinit var produtoRequest: ProdutoRequest
    private lateinit var produtoResponse: ProdutoResponse
    private lateinit var produtoEstoqueRequest: MutableList<ProdutoEstoqueRequest>

    @BeforeEach
    fun setup() {
        produtoRequest = ProdutoRequest("Produto A", "Eletrônicos", 10, 99.99)
        produtoResponse = ProdutoResponse(1L, "Produto A", "Eletrônicos", 10, 99.99)
        produtoEstoqueRequest = mutableListOf(
            ProdutoEstoqueRequest(idProduto = 1, quantidadeRequerida = 5),
            ProdutoEstoqueRequest(idProduto = 2, quantidadeRequerida = 10)
        )
    }

    @Nested
    inner class AdicionaProdutoTests {
        @Test
        fun `deve adicionar um novo produto com sucesso`() {
            whenever(produtoService.adicionaProduto(any())).thenReturn(produtoResponse)

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(produtoRequest))
            )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome_produto").value("Produto A"))
        }
    }

    @Nested
    inner class ListaProdutosTests {
        @Test
        fun `deve retornar lista de produtos`() {
            whenever(produtoService.listaTudo()).thenReturn(listOf(produtoResponse))

            mockMvc.perform(MockMvcRequestBuilders.get("/api/produtos"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
        }
    }

    @Nested
    inner class BuscaProdutoPorIdTests {
        @Test
        fun `deve retornar produto pelo id`() {
            whenever(produtoService.buscaPeloIdProduto(1L)).thenReturn(produtoResponse)

            mockMvc.perform(MockMvcRequestBuilders.get("/api/produtos/1"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
        }
    }

    @Nested
    inner class DeletaProdutoTests {
        @Test
        fun `deve deletar produto com sucesso`() {
            whenever(produtoService.deletaProduto(1L)).thenReturn(true)

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/produtos/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
        }
    }

    @Nested
    inner class SubtraiEstoqueTests {
        @Test
        fun `deve subtrair estoque com sucesso e retornar status 200`() {
            whenever(produtoService.subtraiEstoque(any())).thenReturn(emptyList())

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/subtrai-estoque")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(produtoEstoqueRequest))
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("Estoque atualizado com sucesso."))
        }

        @Test
        fun `deve retornar status 422 quando produtos nao tiverem estoque suficiente`() {
            whenever(produtoService.subtraiEstoque(any())).thenReturn(produtoEstoqueRequest)

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/subtrai-estoque")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(produtoEstoqueRequest))
            )
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
                .andExpect(MockMvcResultMatchers.content().string("Produtos com estoque insuficiente: 1, 2"))
        }
    }

    @Nested
    inner class DevolveEstoqueTests {
        @Test
        fun `deve devolver estoque com sucesso e retornar status 200`() {
            whenever(produtoService.devolveEstoque(any())).thenReturn(mutableListOf())

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/devolve-estoque")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(produtoEstoqueRequest))
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("Produtos devolvidos ao estoque com sucesso."))
        }

        @Test
        fun `deve retornar status 422 quando produtos nao puderem ser devolvidos`() {
            whenever(produtoService.devolveEstoque(any())).thenReturn(produtoEstoqueRequest)

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/devolve-estoque")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(produtoEstoqueRequest))
            )
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
                .andExpect(MockMvcResultMatchers.content().string("Produtos com estoque insuficiente: 1, 2"))
        }
    }
}
