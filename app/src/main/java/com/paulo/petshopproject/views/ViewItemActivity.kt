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
import kotlinx.android.synthetic.main.activity_view_item.txtDesc
import kotlinx.android.synthetic.main.card_item.*
import kotlinx.android.synthetic.main.card_product_item.view.*
import kotlinx.android.synthetic.main.card_product_item.view.image
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

        val returnIntent = Intent()
        val id = intent.getIntExtra("Id", 0)
        val name = intent.getStringExtra("Name")
        val desc = intent.getStringExtra("Desc")
        val price = intent.getDoubleExtra("Price", 0.0)
        val formater = NumberFormat.getCurrencyInstance()

        txtProdName.text = name
        txtDesc.text = desc
        txtPrice.text = formater.format(price)
        Picasso.get().load(
            "https://oficinacordova.azurewebsites.net/android/rest/produto/image/$id"
        ).into(img)

        btnSendCart.setOnClickListener {
            val quantity = etQuant.text.toString().toInt()
            val cart = Carrinho(name = name, price = price, quantity = quantity)

            ItensCarrinho.itensCarrinho.add(cart)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }
}