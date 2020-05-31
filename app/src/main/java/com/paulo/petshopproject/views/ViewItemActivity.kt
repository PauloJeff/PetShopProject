package com.paulo.petshopproject.views

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paulo.petshopproject.R
import com.paulo.petshopproject.model.Carrinho
import com.paulo.petshopproject.model.ItensCarrinho
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_item.*
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
        `@+id/txtQuant`.text = desc
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