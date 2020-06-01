package com.paulo.petshopproject.model

data class Venda(
    var id: String? = null,
    var products: ArrayList<ProdutosVenda>
)