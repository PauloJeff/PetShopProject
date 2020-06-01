package com.paulo.petshopproject.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paulo.petshopproject.R
import com.paulo.petshopproject.model.ItensCarrinho
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.card_item.view.*
import java.text.NumberFormat

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)


    }

    override fun onResume() {
        super.onResume()

        refreshCart()
    }

    fun refreshCart() {
        container.removeAllViews()

        for (cart in ItensCarrinho.itensCarrinho) {
            val formater = NumberFormat.getCurrencyInstance()
            val cardView = layoutInflater
                .inflate(R.layout.card_item, container, false)
            var cartFinalValue = 0.0

            cardView.tvName.text = cart.name
            cardView.tvPrice.text = "Preço (Un): " + cart.price.toString()
            cardView.tvQuantity.text = "Qtde: " + cart.quantity.toString()
            val itemFinalValue = cart.price * cart.quantity
            cardView.tvItemFinalValue.text ="Valor: " +  itemFinalValue.toString()
            cartFinalValue = cartFinalValue + itemFinalValue

            val itemId = cart.id
            Picasso.get().load(
                "https://oficinacordova.azurewebsites.net/android/rest/produto/image/$itemId"
            ).into(cardView.image)

            container.addView(cardView)
            tvFinalCart.text = cartFinalValue.toString()
        }
    }
}