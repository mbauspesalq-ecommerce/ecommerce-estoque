package com.mbauspesalq.ecommerce.estoque.repository

import com.mbauspesalq.ecommerce.estoque.model.Produto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProdutoRepository : JpaRepository<Produto, Long>  {
    fun findByCategoria(categoria: String): List<Produto>
}