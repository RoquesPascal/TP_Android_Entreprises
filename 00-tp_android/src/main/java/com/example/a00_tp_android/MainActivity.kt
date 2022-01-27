package com.example.a00_tp_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.example.a00_tp_android.TodoDatabase.Companion.sdf
import java.sql.Date
import java.sql.Timestamp


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = TodoDatabase.getDatabase(this)
        val cacheRequeteDAO = db.cacheRequeteDAO()
        val jourActuel = Timestamp(System.currentTimeMillis())
        var mois = (android.text.format.DateFormat.format("MM", (jourActuel.time)) as String).toIntOrNull()
        var annee = (android.text.format.DateFormat.format("yyyy", (jourActuel.time)) as String).toIntOrNull()
        mois = mois?.minus(3)
        if (mois!! <= 0)
        {
            mois += 12
            if (annee != null) annee -= 1
        }
        val dateDelais3Mois = sdf.parse(android.text.format.DateFormat.format("dd", (jourActuel.time)) as String + "/" + mois.toString() + "/" + annee.toString())
        cacheRequeteDAO.deleteDelaisDe3Mois(dateDelais3Mois!!)

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