package com.paulo.petshopproject.views

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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

                cardView.tvName.text = product.nomeProduto
                cardView.tvPrice.text = "R" + formater.format(product.precProduto)
                val plot = product.precProduto / 3
                val plotText = "3x de R" + formater.format(plot) + " sem juros"
                cardView.txtPlots.text = plotText
                Picasso.get().load(
                    "https://oficinacordova.azurewebsites.net/android/rest/produto/image/"
                            + product.idProduto)
                        .into(cardView.image)
                Container.addView(cardView)

                cardView.setOnClickListener {
                    val itemid = product.idProduto.toString()
                    val intent = Intent(this, ViewItemActivity::class.java)
                    intent.putExtra("Id", itemid)

                    startActivityForResult(intent, 0)
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Produto adicionado ao carrinho.", Toast.LENGTH_LONG).show()
            }
        } else if(requestCode == 1) {
            Toast.makeText(this, "Sua compra foi finalizada com sucesso!.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        var search: MenuItem? = menu?.findItem(R.id.search)
        if(search != null){
            var searchView: SearchView = search.actionView as SearchView
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
            })
        }

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
            startActivityForResult(intent, 1)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrentUser(): FirebaseUser? {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser
    }
}
