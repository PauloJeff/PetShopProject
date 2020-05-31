package com.paulo.petshopproject.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.paulo.petshopproject.R
import com.paulo.petshopproject.model.Produto
import com.paulo.petshopproject.services.ProdutoService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_list_products.*
import kotlinx.android.synthetic.main.card_product_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat

class ListProductsActivity : AppCompatActivity() {

    var database: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_products)

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build())

        if(getCurrentUser() == null) {
            val email = AuthUI.IdpConfig.EmailBuilder().build()
            val providers = arrayListOf(email)
            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()

            startActivityForResult(intent, 0)
        } else {
            //configurarFirebase()
            Toast.makeText(this, "Já logado!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()

        getProducts()
    }

    private fun getProducts() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://oficinacordova.azurewebsites.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ProdutoService::class.java)
        var call = service.list()

        val callback = object: Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val listProducts: List<Produto>? = response.body()
                    updateUi(listProducts)
                } else {
                    Snackbar
                        .make(Container, "Não é possível atualizar os produtos", Snackbar.LENGTH_LONG)
                        .show()
                    Log.e("ERRO", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Snackbar
                    .make(Container, "Não é possível se conectar a internet", Snackbar.LENGTH_LONG)
                    .show()
                Log.e("ERRO", "Falha ao chamar o serviço", t)
            }
        }

        call.enqueue(callback)
    }

    private fun updateUi(listProducts: List<Produto>?) {
        Container.removeAllViews()

        val formater = NumberFormat.getCurrencyInstance()
        if(listProducts != null) {
            for(product in listProducts) {
                val cardView = layoutInflater
                    .inflate(R.layout.card_product_item, Container, false)

                cardView.setOnClickListener {
                    val itemid = product.idProduto
                    val itemname = product.nomeProduto
                    val itemdesc = product.descProduto
                    val itemprice = product.precProduto
                    val intent = Intent(this, ViewItemActivity::class.java)
                    intent.putExtra("Id", itemid)
                    intent.putExtra("Name", itemname)
                    intent.putExtra("Desc", itemdesc)
                    intent.putExtra("Price", itemprice)

                    startActivity(intent)
                }

                cardView.txtName.text = product.nomeProduto
                cardView.tvPrice.text = formater.format(product.precProduto)
                val plot = product.precProduto / 3
                val plotText = "3x de R$ " + formater.format(plot) + " sem juros"
                cardView.txtPlots.text = plotText
                Picasso.get().load(
                    "https://oficinacordova.azurewebsites.net/android/rest/produto/image/"
                            + product.idProduto)
                        .into(cardView.image)
                Container.addView(cardView)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.about) {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            return true
        } else if (id == R.id.cart) {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configurarFirebase() {
        val usuario = getCurrentUser()
        if(usuario != null) {
            database = FirebaseDatabase.getInstance().reference.child(usuario.uid)

            val callback = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("MainActivity", "onCancelled", databaseError.toException())

                    Toast.makeText(this@ListProductsActivity, "Erro ao acessar o servidor", Toast.LENGTH_LONG).show()
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
}
