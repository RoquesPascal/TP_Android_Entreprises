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

        val entreprise = intent?.extras?.get("entreprise") as? Entreprise ?: return
        val progressBar = findViewById<ProgressBar>(R.id.ProgressBarEntreprise)
        Thread(Runnable {
            runOnUiThread {
                progressBar.visibility = View.VISIBLE
                if (entreprise != null)
                {
                    val db = TodoDatabase.getDatabase(this)
                    val entrepriseDAO = db.entrepriseDAO()
                    if(entrepriseDAO.getById(entreprise.siret) == null)
                    {
                        entrepriseDAO.insert(entreprise)
                    }
                    val entrepriseDeLaBase = entrepriseDAO.getById(entreprise.siret)
                    findViewById<TextView>(R.id.TextViewEntrepriseNom)     .setText(entrepriseDeLaBase?.raisonSociale)
                    findViewById<TextView>(R.id.TextViewSiret)             .setText(entrepriseDeLaBase?.siret.toString())
                    findViewById<TextView>(R.id.TextViewAdresse)           .setText(entrepriseDeLaBase?.adresse)
                    findViewById<TextView>(R.id.TextViewActivitePrincipale).setText(entrepriseDeLaBase?.activitePrincipale)
                    findViewById<TextView>(R.id.TextViewNatureJuridique)   .setText(entrepriseDeLaBase?.natureJuridique)
                    findViewById<TextView>(R.id.TextViewEmail)             .setText(entrepriseDeLaBase?.email)
                    findViewById<TextView>(R.id.TextViewDepartement)       .setText(entrepriseDeLaBase?.departement)

                    val test1 = entrepriseDAO.getAll()

                    progressBar.visibility = View.INVISIBLE
                }
            }
        }).start()
    }
}