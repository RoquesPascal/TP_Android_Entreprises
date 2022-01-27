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
import java.util.ArrayList


class FormulaireEntrepriseActivity : AppCompatActivity()
{
    private val MESDONNES_KEY = "mesDonnees"
    private var mesDonnees : ArrayList<String>? = null

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

        var queryNom : String = ""
        var queryVilleDepartement : String = ""

        findViewById<ImageButton>(R.id.buttonQuery).setOnClickListener {
            queryNom = findViewById<EditText>(R.id.editQuery).text.toString()
            if(!rechercheParNomUniquement) queryVilleDepartement = findViewById<EditText>(R.id.editQueryVilleDepartement).text.toString()
            mesDonnees =
                if (savedInstanceState != null && savedInstanceState.containsKey(MESDONNES_KEY)) savedInstanceState.getStringArrayList(MESDONNES_KEY)
                else  ArrayList()
            mesDonnees!!.add(queryNom)
            mesDonnees!!.add(queryVilleDepartement)

            if(mesDonnees!![0].isEmpty()) return@setOnClickListener
            val progressBar = findViewById<ProgressBar>(R.id.queryProgressBar)
            Thread(Runnable {
                runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                    listViewEntreprises.visibility = View.INVISIBLE
                }

                val jourActuel = Timestamp(System.currentTimeMillis())
                var listeEntreprise : List<Entreprise>? = null
                var idCache = ChercherCacheRecherche(cacheRequeteDAO, mesDonnees!![0], mesDonnees!![1], jourActuel)
                if(idCache == null)
                {
                    idCache = CreerCacheRecherche(cacheRequeteDAO, mesDonnees!![0], mesDonnees!![1], jourActuel)
                    listeEntreprise = entrepriseService.getEntreprise(mesDonnees!![0], mesDonnees!![1])
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
        return cache?.id
    }

    private fun CreerCacheRecherche(cacheRequeteDAO : CacheRequeteDAO, query : String, queryVilleDepartement : String, jourActuel : Timestamp) : Long
    {
        val jourActuelPourLaBDD = sdf.parse(android.text.format.DateFormat.format("dd", (jourActuel.time)) as String + "/" + android.text.format.DateFormat.format("MM", (jourActuel.time)) as String + "/" + android.text.format.DateFormat.format("yyyy", (jourActuel.time)) as String)
        cacheRequeteDAO.insert(CacheRequete(null, query, queryVilleDepartement, jourActuelPourLaBDD!!))
        val cache = cacheRequeteDAO.getByChaineRecherchee(query, queryVilleDepartement, jourActuelPourLaBDD)
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
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(MESDONNES_KEY, mesDonnees)
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