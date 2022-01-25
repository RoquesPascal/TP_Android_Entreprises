package com.example.a00_tp_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.ButtonRechercheParNom).setOnClickListener {
            val intent = Intent(applicationContext, FormulaireEntrepriseActivity::class.java)
            intent.putExtra("RechercheParNomUniquement", true)
            startActivity(intent)
        }

        findViewById<Button>(R.id.ButtonRechercheParVilleOuCP).setOnClickListener {
            val intent = Intent(applicationContext, FormulaireEntrepriseActivity::class.java)
            intent.putExtra("RechercheParNomUniquement", false)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean
    {
        if(item.title.toString() == getString(R.string.Historique))
        {
            val intent = Intent(applicationContext, HistoriqueActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}