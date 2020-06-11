package com.paulo.petshopproject.views

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.paulo.petshopproject.R
import kotlinx.android.synthetic.main.activity_cart.*
import com.paulo.petshopproject.model.ItensCarrinho
import com.paulo.petshopproject.model.ProdutosVenda
import com.paulo.petshopproject.model.Venda
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_item.view.*
import java.text.NumberFormat

class CartActivity : AppCompatActivity() {
    var database: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        btnBuy.setOnClickListener {
            configurarFirebase()
            val returnIntent = Intent()
            val listProdutosVenda = ArrayList<ProdutosVenda>()
            val newNode= database?.child("venda")?.push()

            for (cart in ItensCarrinho.itensCarrinho) {
                var price = cart.price + 0.0000001
                var produtoVenda = ProdutosVenda(id = cart.id, quantity = cart.quantity, price = price, name = cart.name)
                listProdutosVenda.add(produtoVenda)
            }
            ItensCarrinho.itensCarrinho.clear()
            val venda: Venda = Venda(products = listProdutosVenda)
            venda.id = newNode?.key
            newNode?.setValue(venda)

            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    private fun configurarFirebase() {
        val usuario = getCurrentUser()
        if(usuario != null) {
            database = FirebaseDatabase.getInstance().reference.child(usuario.uid)

            val callback = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("MainActivity", "onCancelled", databaseError.toException())

                    Toast.makeText(this@CartActivity, "Erro ao acessar o servidor", Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                }
            }

            database?.addValueEventListener(callback)
        }
    }

    private fun getCurrentUser(): FirebaseUser? {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser
    }

    override fun onResume() {
        super.onResume()

        refreshCart()
    }

    fun refreshCart() {
        container.removeAllViews()

        var cartFinalValue = 0.00
        for (cart in ItensCarrinho.itensCarrinho) {
            val formater = NumberFormat.getCurrencyInstance()
            val cardView = layoutInflater
                .inflate(R.layout.card_item, container, false)

            cardView.tvName.text = cart.name
            cardView.tvPrice.text = "Preço (Un): " + cart.price.toString()
            cardView.tvQuantity.text = "Qtde: " + cart.quantity.toString()


            val finalPrice = (cart.price * cart.quantity)
            val itemFinalValue = String.format("%.2f", finalPrice)

            cardView.tvItemFinalValue.text ="Valor: ${itemFinalValue}"

            cartFinalValue += finalPrice

            val itemId = cart.id
            Picasso.get().load(
                "https://oficinacordova.azurewebsites.net/android/rest/produto/image/$itemId"
            ).into(cardView.image)

            container.addView(cardView)
        }
        tvFinalCart.text = "Valor do Carrinho: R$" + String.format("%.2f", cartFinalValue)
    }
}