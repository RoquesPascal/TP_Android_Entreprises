package com.example.a00_tp_android

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class HistoriqueDetailsActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historique_details)

        val idCacheRequete = intent.extras?.get("idCacheRequete") as Long

        val db = TodoDatabase.getDatabase(this)
        val entrepriseDAO = db.entrepriseDAO()
        val cacheRequeteEntrepriseDAO = db.cacheRequeteEntrepriseDAO()

        val listViewEntreprises = findViewById<ListView>(R.id.listDetailsEntreprises)
        val progressBar = findViewById<ProgressBar>(R.id.queryDetailsProgressBar)
        Thread(Runnable {
            runOnUiThread {
                progressBar.visibility = View.VISIBLE
                listViewEntreprises.visibility = View.INVISIBLE

                val listeSiret = cacheRequeteEntrepriseDAO.getByIdCacheRequete(idCacheRequete)
                val listeEntreprise = entrepriseDAO.getByPlusieursSiret(listeSiret)

                listViewEntreprises.adapter = ArrayAdapter<Entreprise>(applicationContext,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listeEntreprise)
                progressBar.visibility = View.INVISIBLE
                listViewEntreprises.visibility = View.VISIBLE
            }
        }).start()

        listViewEntreprises.setOnItemClickListener { _, _, position, _ ->
            val entreprise = listViewEntreprises.adapter.getItem(position) as Entreprise
            val intent = Intent(applicationContext, EntrepriseActivity::class.java)
            intent.putExtra("siretEntreprise", entreprise.siret)
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
        else if(item.title.toString() == getString(R.string.RechercherUneEntreprise))
        {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}