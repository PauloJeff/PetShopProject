package com.paulo.petshopproject.model

data class Produto (
    var idProduto: Int,
    var nomeProduto: String,
    var precProduto: Double,
    var qtdMinEstoque: Int,
    var descProduto: String,
    var idCategoria: Int,
    var descontoPromocao: Double,
    var ativoProduto: Boolean
)