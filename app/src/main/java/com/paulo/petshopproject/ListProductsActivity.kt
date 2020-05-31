package com.paulo.petshopproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

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
            configurarFirebase()
            Toast.makeText(this, "Já logado!", Toast.LENGTH_LONG).show()
        }
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
