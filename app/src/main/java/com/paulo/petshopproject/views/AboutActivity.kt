package com.paulo.petshopproject.views

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.paulo.petshopproject.R
import com.paulo.petshopproject.model.Produto
import com.paulo.petshopproject.services.ProdutoService
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_list_products.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        btnSenac.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
            Uri.parse("https://www.sp.senac.br/"))

        startActivity(intent)
        }

        btnMail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)

            intent.type = "text/html"

            intent.putExtra(Intent.EXTRA_EMAIL,arrayOf<String>("caiosbras@hotmail.com, paul_jeff@outlook.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback")

            startActivity(intent)
    }

    }

}