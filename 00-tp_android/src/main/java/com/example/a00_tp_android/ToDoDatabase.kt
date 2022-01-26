package com.example.a00_tp_android

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import java.text.*
import java.util.*


@Database(entities = [Entreprise::class, CacheRequete::class, CacheRequeteEntreprise::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class TodoDatabase : RoomDatabase()
{
    abstract fun entrepriseDAO() : EntrepriseDAO
    abstract fun cacheRequeteDAO() : CacheRequeteDAO
    abstract fun cacheRequeteEntrepriseDAO() : CacheRequeteEntrepriseDAO

    companion object
    {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
        private var INSTANCE: TodoDatabase? = null
        fun getDatabase(context: Context): TodoDatabase
        {
            if (INSTANCE == null)
            {
                INSTANCE = Room
                    .databaseBuilder(context, TodoDatabase::class.java, "BDD_TP_Android.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }
    }
}