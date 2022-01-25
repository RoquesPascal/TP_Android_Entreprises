package com.example.a00_tp_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.ButtonRechercheParNom).setOnClickListener {
            val intent = Intent(applicationContext, FormulaireEntreprise::class.java)
            intent.putExtra("RechercheParNomUniquement", true)
            startActivity(intent)
        }

        findViewById<Button>(R.id.ButtonRechercheParVilleOuCP).setOnClickListener {
            val intent = Intent(applicationContext, FormulaireEntreprise::class.java)
            intent.putExtra("RechercheParNomUniquement", false)
            startActivity(intent)
        }
    }
}