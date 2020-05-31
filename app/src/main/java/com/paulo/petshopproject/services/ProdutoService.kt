package com.paulo.petshopproject.services

import com.paulo.petshopproject.model.Produto
import retrofit2.Call
import retrofit2.http.GET

interface ProdutoService {
    @GET("/android/rest/produto")
    fun list(): Call<List<Produto>>;
}