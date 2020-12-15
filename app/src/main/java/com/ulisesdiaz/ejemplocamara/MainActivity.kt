package com.ulisesdiaz.ejemplocamara


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView


class MainActivity : AppCompatActivity() {

    var imgFoto: ImageView? = null
    var fotos: Fotos? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnTomar = findViewById<Button>(R.id.btnTomar)
        val btnSelecionar = findViewById<Button>(R.id.btnSelecionar)
        imgFoto = findViewById(R.id.imgFoto)

        fotos = Fotos(this, imgFoto!!)

        btnTomar.setOnClickListener {
            //dispararIntentTomarFoto()
            fotos?.tomarFoto()
        }

        btnSelecionar.setOnClickListener {
            fotos?.selecionarFoto()
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fotos?.requesttPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fotos?.activityResult(requestCode, resultCode, data)
    }


}