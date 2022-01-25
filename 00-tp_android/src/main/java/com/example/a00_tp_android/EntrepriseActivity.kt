package com.example.a00_tp_android

import android.os.Bundle
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
}