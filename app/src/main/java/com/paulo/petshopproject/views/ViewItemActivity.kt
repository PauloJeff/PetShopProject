package com.paulo.petshopproject.views

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.paulo.petshopproject.R
import com.paulo.petshopproject.model.Carrinho
import com.paulo.petshopproject.model.ItensCarrinho
import com.paulo.petshopproject.model.Produto
import com.paulo.petshopproject.services.ProdutoService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_list_products.*
import kotlinx.android.synthetic.main.activity_view_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat

class ViewItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)

        val id = intent.getStringExtra("Id")
        val name = intent.getStringExtra("Name")
        val price = intent.getDoubleExtra("Price", 0.0)

        val returnIntent = Intent()
        btnSendCart.setOnClickListener {
            val quantity = etQuant.text.toString().toInt()
            val cart = Carrinho(id = id!!.toInt(),name = name, price = price, quantity = quantity)

            ItensCarrinho.itensCarrinho.add(cart)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        getProduct()
    }

    private fun getProduct() {
        val id = intent.getStringExtra("Id")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://oficinacordova.azurewebsites.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ProdutoService::class.java)
        var call = service.getById(id)

        val callback = object: Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val product: Produto? = response.body()
                    updateUi(product)
                } else {
                    Snackbar
                        .make(Container, "Não é possível mostrar os dados do produto", Snackbar.LENGTH_LONG)
                        .show()
                    Log.e("ERRO", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                Snackbar
                    .make(Container, "Não é possível se conectar a internet", Snackbar.LENGTH_LONG)
                    .show()
                Log.e("ERRO", "Falha ao chamar o serviço", t)
            }
        }

        call.enqueue(callback)
    }

    private fun updateUi(item: Produto?) {

        val formater = NumberFormat.getCurrencyInstance()
        txtProdName.text = item?.nomeProduto
        txtPrice.text = "Preço: R" + formater.format(item?.precProduto)
        Picasso.get().load(
        "https://oficinacordova.azurewebsites.net/android/rest/produto/image/"
        + item?.idProduto)
        .into(img)
    }

}