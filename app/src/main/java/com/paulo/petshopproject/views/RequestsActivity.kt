package com.paulo.petshopproject.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.paulo.petshopproject.R
import com.paulo.petshopproject.model.ProdutosVenda
import com.paulo.petshopproject.model.Venda
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.card_requests.view.*

class RequestsActivity : AppCompatActivity() {

    var database: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)

        configurarFirebase()
    }

    private fun configurarFirebase() {
        val usuario = getCurrentUser()
        if(usuario != null) {
            database = FirebaseDatabase.getInstance().reference.child(usuario.uid)

            val callback = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("MainActivity", "onCancelled", databaseError.toException())

                    Toast.makeText(this@RequestsActivity, "Erro ao acessar o servidor", Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    getRequests(dataSnapshot)
                }
            }

            database?.addValueEventListener(callback)
        }
    }

    private fun getCurrentUser(): FirebaseUser? {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser
    }

    private fun getRequests(dataSnapshot: DataSnapshot) {
        val requestList = arrayListOf<Venda>()
        val produtosList = ArrayList<ProdutosVenda>()

        dataSnapshot.child("venda").children.forEach {
            val currentNode = it

            currentNode.child("products").children.forEach {
                val currentProductNode = it
                val mapProduct = currentProductNode.getValue() as HashMap<String, Any>

                val id = mapProduct.get("id") as Long
                val name = mapProduct.get("name") as String
                val price = mapProduct.get("price") as Double
                val quantity = mapProduct.get("quantity") as Long
                val product = ProdutosVenda(id = id.toString().toInt(), name = name, price = price, quantity = quantity.toString().toInt())

                produtosList.add(product)
            }

            val map = currentNode.getValue() as HashMap<String, Any>

            val id = map.get("id") as String
            val products = produtosList

            val venda = Venda(id = id, products = products)
            requestList.add(venda)
        }

        updateUi(requestList)
    }

    private fun updateUi(requestList: List<Venda>) {
        container.removeAllViews()

        for(request in requestList) {
            val itemRquest = layoutInflater.inflate(R.layout.card_requests, container, false)

            itemRquest.txtRequest.text = "Pedido #${request.id}"

            var price: Double = 0.0
            for(product in request.products) {
                price += (product.price * product.quantity)
            }

            var formatPrice = String.format("%.2f", price)
            itemRquest.txtPrice.text = "Valor: R$${formatPrice}"

            container.addView(itemRquest)
        }
    }
}