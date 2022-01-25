package com.example.a00_tp_android

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class EntrepriseActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entreprise)

        val siretEntreprise = intent?.extras?.get("siretEntreprise") as? Long ?: return
        val progressBar = findViewById<ProgressBar>(R.id.ProgressBarEntreprise)
        Thread(Runnable {
            runOnUiThread {
                progressBar.visibility = View.VISIBLE
                val db = TodoDatabase.getDatabase(this)
                val entrepriseDAO = db.entrepriseDAO()
                val entrepriseDeLaBase = entrepriseDAO.getBySiret(siretEntreprise)
                findViewById<TextView>(R.id.TextViewEntrepriseNom)     .setText(entrepriseDeLaBase?.raisonSociale)
                findViewById<TextView>(R.id.TextViewSiret)             .setText(entrepriseDeLaBase?.siret.toString())
                findViewById<TextView>(R.id.TextViewAdresse)           .setText(entrepriseDeLaBase?.adresse)
                findViewById<TextView>(R.id.TextViewActivitePrincipale).setText(entrepriseDeLaBase?.activitePrincipale)
                findViewById<TextView>(R.id.TextViewNatureJuridique)   .setText(entrepriseDeLaBase?.natureJuridique)
                findViewById<TextView>(R.id.TextViewEmail)             .setText(entrepriseDeLaBase?.email)
                findViewById<TextView>(R.id.TextViewDepartement)       .setText(entrepriseDeLaBase?.departement)
                progressBar.visibility = View.INVISIBLE
            }
        }).start()
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