package com.example.a00_tp_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.sql.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val entrepriseService = EntrepriseService()
        val listeEntreprises = findViewById<ListView>(R.id.listLocations)

        val db = TodoDatabase.getDatabase(this)
        val entrepriseDAO = db.entrepriseDAO()
        val cacheRequeteDAO = db.cacheRequeteDAO()
        val cacheRequeteEntrepriseDAO = db.cacheRequeteEntrepriseDAO()

        findViewById<ImageButton>(R.id.buttonQuery).setOnClickListener {
            val query = findViewById<EditText>(R.id.editQuery).text.toString()
            if(query.isEmpty()) return@setOnClickListener
            val progressBar = findViewById<ProgressBar>(R.id.queryProgressBar)
            Thread(Runnable {
                runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                    listeEntreprises.visibility = View.INVISIBLE
                }
                val idCache = CreerOuChercherCacheRecherche(cacheRequeteDAO, query)
                val result = entrepriseService.getEntreprise(query)
                AssocierEntrepriseACacheRecherche(entrepriseDAO, cacheRequeteEntrepriseDAO, idCache, result)
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
            intent.putExtra("siretEntreprise", entreprise.siret)
            startActivity(intent)
        }
    }

    fun CreerOuChercherCacheRecherche(cacheRequeteDAO : CacheRequeteDAO, query : String) : Long
    {
        val test1 = Timestamp(System.currentTimeMillis())
        //https://stackoverflow.com/questions/50313525/room-using-date-field

        var cache = cacheRequeteDAO.getByChaineRecherchee(query)
        if(cache == null)
        {
            cacheRequeteDAO.insert(CacheRequete(null, query))
            cache = cacheRequeteDAO.getByChaineRecherchee(query)
        }
        val testGetAllCacheRequete = cacheRequeteDAO.getAll()
        return cache!!.id!!
    }

    fun AssocierEntrepriseACacheRecherche(entrepriseDAO : EntrepriseDAO, cacheRequeteEntrepriseDAO : CacheRequeteEntrepriseDAO, idCacheRecherche : Long, listeEntreprise : List<Entreprise>)
    {
        val rechercheNExistePas : Boolean = cacheRequeteEntrepriseDAO.getById(idCacheRecherche) == null
        for(entreprise in listeEntreprise)
        {
            if(entrepriseDAO.getById(entreprise.siret) == null) entrepriseDAO.insert(entreprise)
            if(rechercheNExistePas) cacheRequeteEntrepriseDAO.insert(CacheRequeteEntreprise(null, idCacheRecherche, entreprise.siret!!))
        }

        val testGetAllEntreprise = entrepriseDAO.getAll()
        val testGetAllCacheRequeteEntreprise = cacheRequeteEntrepriseDAO.getAll()
    }
}