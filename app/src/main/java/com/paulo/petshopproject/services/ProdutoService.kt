package com.paulo.petshopproject.services

import com.paulo.petshopproject.model.Produto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProdutoService {
    @GET("/android/rest/produto")
    fun list(): Call<List<Produto>>;


    @GET("/android/rest/produto/{id}")
    fun getById(@Path("id") id: String): Call<Produto>;
}