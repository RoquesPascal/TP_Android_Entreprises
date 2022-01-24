package com.example.a00_tp_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*



class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val entrepriseService = EntrepriseService()
        val listeEntreprises = findViewById<ListView>(R.id.listLocations)

        findViewById<ImageButton>(R.id.buttonQuery).setOnClickListener {
            val query = findViewById<EditText>(R.id.editQuery).text.toString()
            if(query.isEmpty()) return@setOnClickListener
            val progressBar = findViewById<ProgressBar>(R.id.queryProgressBar)
            Thread(Runnable {
                runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                    listeEntreprises.visibility = View.INVISIBLE
                }
                val result = entrepriseService.getEntreprise(query)
                runOnUiThread {
                    listeEntreprises.adapter = ArrayAdapter<Entreprise>(applicationContext,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        result)
                    progressBar.visibility = View.INVISIBLE
                    listeEntreprises.visibility = View.VISIBLE
                }
            }).start()
        }

        listeEntreprises.setOnItemClickListener { _, _, position, _ ->
            val entreprise = listeEntreprises.adapter.getItem(position) as Entreprise
            val intent = Intent(applicationContext, EntrepriseActivity::class.java)
            intent.putExtra("entreprise", entreprise)
            startActivity(intent)
        }
    }
}