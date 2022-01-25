package com.example.a00_tp_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HistoriqueActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historique)

        val db = TodoDatabase.getDatabase(this)
        val cacheRequeteDAO = db.cacheRequeteDAO()
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewHistorique)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CacheRequeteAdapter(this, cacheRequeteDAO)
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


class CacheRequeteViewHolder(row : View) : RecyclerView.ViewHolder(row)
{
    val texteCacheRequete  : TextView = row.findViewById(R.id.TextViewHistorique)
    val nombreCacheRequete : TextView = row.findViewById(R.id.TextViewHistoriqueNombre)
}


class CacheRequeteAdapter(private val context         : Context,
                          private val cacheRequeteDAO : CacheRequeteDAO) : RecyclerView.Adapter<CacheRequeteViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CacheRequeteViewHolder
    {
        return CacheRequeteViewHolder(LayoutInflater.from(context).inflate(R.layout.historique_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder : CacheRequeteViewHolder, position : Int)
    {
        val db = TodoDatabase.getDatabase(context)
        val entrepriseDAO = db.entrepriseDAO()
        val cacheRequeteDAO = db.cacheRequeteDAO()
        val cacheRequeteEntrepriseDAO = db.cacheRequeteEntrepriseDAO()

        val cacheRequete = cacheRequeteDAO.getByPosition(position)
        val listeSiret = cacheRequeteEntrepriseDAO.getByIdCacheRequete(cacheRequete.id!!)
        val nombreEntreprisesAssociees = entrepriseDAO.countByPlusieursSirets(listeSiret)

        holder.texteCacheRequete.text = cacheRequete.chaineRecherchee
        holder.nombreCacheRequete.text = (nombreEntreprisesAssociees.toString() + " " + context.getString(if(nombreEntreprisesAssociees <= 1) R.string.EntrepriseAssociee else R.string.EntreprisesAssociees))


        holder.itemView.setOnClickListener {
            val intent = Intent(context, HistoriqueDetailsActivity::class.java)
            intent.putExtra("idCacheRequete", cacheRequete.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() : Int
    {
        return cacheRequeteDAO.count()
    }
}