package com.example.a00_tp_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.sql.Timestamp


class FormulaireEntreprise : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val rechercheParNomUniquement = intent.extras?.get("RechercheParNomUniquement") as Boolean
        if(rechercheParNomUniquement) setContentView(R.layout.activity_formulaire_entreprise_nom)
        else setContentView(R.layout.activity_formulaire_entreprise_ville_departement)

        val entrepriseService = EntrepriseService()
        val listViewEntreprises = findViewById<ListView>(R.id.listLocations)

        val db = TodoDatabase.getDatabase(this)
        val entrepriseDAO = db.entrepriseDAO()
        val cacheRequeteDAO = db.cacheRequeteDAO()
        val cacheRequeteEntrepriseDAO = db.cacheRequeteEntrepriseDAO()

        findViewById<ImageButton>(R.id.buttonQuery).setOnClickListener {
            val queryNom = findViewById<EditText>(R.id.editQuery).text.toString()
            var queryVilleDepartement : String = ""
            if(!rechercheParNomUniquement) queryVilleDepartement = findViewById<EditText>(R.id.editQueryCPVille).text.toString()

            if(queryNom.isEmpty()) return@setOnClickListener
            val progressBar = findViewById<ProgressBar>(R.id.queryProgressBar)
            Thread(Runnable {
                runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                    listViewEntreprises.visibility = View.INVISIBLE
                }

                var listeEntreprise : List<Entreprise>? = null
                var idCache = ChercherCacheRecherche(cacheRequeteDAO, queryNom, queryVilleDepartement)
                if(idCache == null)
                {
                    idCache = CreerCacheRecherche(cacheRequeteDAO, queryNom, queryVilleDepartement)
                    listeEntreprise = entrepriseService.getEntreprise(queryNom, queryVilleDepartement)
                }
                else
                {
                    val listeSiret = cacheRequeteDAO.getByRecherche(queryNom, queryVilleDepartement)
                    listeEntreprise = entrepriseDAO.getByPlusieursSiret(listeSiret)
                }
                AssocierEntrepriseACacheRecherche(entrepriseDAO, cacheRequeteEntrepriseDAO, idCache, listeEntreprise)

                runOnUiThread {
                    listViewEntreprises.adapter = ArrayAdapter<Entreprise>(applicationContext,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        listeEntreprise)
                    progressBar.visibility = View.INVISIBLE
                    listViewEntreprises.visibility = View.VISIBLE
                }
            }).start()
        }

        listViewEntreprises.setOnItemClickListener { _, _, position, _ ->
            val entreprise = listViewEntreprises.adapter.getItem(position) as Entreprise
            val intent = Intent(applicationContext, EntrepriseActivity::class.java)
            intent.putExtra("siretEntreprise", entreprise.siret)
            startActivity(intent)
        }
    }

    private fun ChercherCacheRecherche(cacheRequeteDAO : CacheRequeteDAO, query : String, queryVilleDepartement : String) : Long?
    {
        var cache = cacheRequeteDAO.getByChaineRecherchee(query, queryVilleDepartement)
        return cache?.id
    }

    private fun CreerCacheRecherche(cacheRequeteDAO : CacheRequeteDAO, query : String, queryVilleDepartement : String) : Long
    {
        val test1 = Timestamp(System.currentTimeMillis())
        //https://stackoverflow.com/questions/50313525/room-using-date-field

        cacheRequeteDAO.insert(CacheRequete(null, query, queryVilleDepartement))
        val cache = cacheRequeteDAO.getByChaineRecherchee(query, queryVilleDepartement)
        val testGetAllCacheRequete = cacheRequeteDAO.getAll()
        return cache!!.id!!
    }

    private fun AssocierEntrepriseACacheRecherche(entrepriseDAO : EntrepriseDAO, cacheRequeteEntrepriseDAO : CacheRequeteEntrepriseDAO, idCacheRecherche : Long, listeEntreprise : List<Entreprise>)
    {
        val rechercheNExistePas : Boolean = cacheRequeteEntrepriseDAO.getById(idCacheRecherche) == null
        for(entreprise in listeEntreprise)
        {
            if(entrepriseDAO.getBySiret(entreprise.siret) == null) entrepriseDAO.insert(entreprise)
            if(rechercheNExistePas) cacheRequeteEntrepriseDAO.insert(CacheRequeteEntreprise(null, idCacheRecherche, entreprise.siret!!))
        }

        val testGetAllEntreprise = entrepriseDAO.getAll()
        val testGetAllCacheRequeteEntreprise = cacheRequeteEntrepriseDAO.getAll()
    }
}