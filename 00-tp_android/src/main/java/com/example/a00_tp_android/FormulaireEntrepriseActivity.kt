package com.example.a00_tp_android

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.a00_tp_android.TodoDatabase.Companion.sdf
import java.sql.Date
import java.sql.Timestamp


class FormulaireEntrepriseActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val rechercheParNomUniquement = intent.extras?.get("RechercheParNomUniquement") as Boolean
        if(rechercheParNomUniquement) setContentView(R.layout.activity_formulaire_entreprise_nom)
        else setContentView(R.layout.activity_formulaire_entreprise_ville_departement)

        val entrepriseService = EntrepriseService()
        val listViewEntreprises = findViewById<ListView>(R.id.listEntreprises)

        val db = TodoDatabase.getDatabase(this)
        val entrepriseDAO = db.entrepriseDAO()
        val cacheRequeteDAO = db.cacheRequeteDAO()
        val cacheRequeteEntrepriseDAO = db.cacheRequeteEntrepriseDAO()

        findViewById<ImageButton>(R.id.buttonQuery).setOnClickListener {
            val queryNom = findViewById<EditText>(R.id.editQuery).text.toString()
            var queryVilleDepartement : String = ""
            if(!rechercheParNomUniquement) queryVilleDepartement = findViewById<EditText>(R.id.editQueryVilleDepartement).text.toString()

            if(queryNom.isEmpty()) return@setOnClickListener
            val progressBar = findViewById<ProgressBar>(R.id.queryProgressBar)
            Thread(Runnable {
                runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                    listViewEntreprises.visibility = View.INVISIBLE
                }

                val jourActuel = Timestamp(System.currentTimeMillis())
                var listeEntreprise : List<Entreprise>? = null
                var idCache = ChercherCacheRecherche(cacheRequeteDAO, queryNom, queryVilleDepartement, jourActuel)
                if(idCache == null)
                {
                    idCache = CreerCacheRecherche(cacheRequeteDAO, queryNom, queryVilleDepartement, jourActuel)
                    listeEntreprise = entrepriseService.getEntreprise(queryNom, queryVilleDepartement)
                }
                else
                {
                    val listeSiret = cacheRequeteDAO.getByRecherche(idCache)
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

    private fun ChercherCacheRecherche(cacheRequeteDAO: CacheRequeteDAO, query: String, queryVilleDepartement: String, jourActuel : Timestamp) : Long?
    {
        val jourActuelPourLaBDD = sdf.parse(android.text.format.DateFormat.format("dd", (jourActuel.time)) as String + "/" + android.text.format.DateFormat.format("MM", (jourActuel.time)) as String + "/" + android.text.format.DateFormat.format("yyyy", (jourActuel.time)) as String)
        val cache = cacheRequeteDAO.getByChaineRecherchee(query, queryVilleDepartement, jourActuelPourLaBDD!!)
        val testGetAllCacheRequete = cacheRequeteDAO.getAll()
        return cache?.id
    }

    private fun CreerCacheRecherche(cacheRequeteDAO : CacheRequeteDAO, query : String, queryVilleDepartement : String, jourActuel : Timestamp) : Long
    {
        val jourActuelPourLaBDD = sdf.parse(android.text.format.DateFormat.format("dd", (jourActuel.time)) as String + "/" + android.text.format.DateFormat.format("MM", (jourActuel.time)) as String + "/" + android.text.format.DateFormat.format("yyyy", (jourActuel.time)) as String)
        cacheRequeteDAO.insert(CacheRequete(null, query, queryVilleDepartement, jourActuelPourLaBDD!!))
        val cache = cacheRequeteDAO.getByChaineRecherchee(query, queryVilleDepartement, jourActuelPourLaBDD)
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