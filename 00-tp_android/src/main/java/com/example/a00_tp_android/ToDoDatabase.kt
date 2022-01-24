package com.example.a00_tp_android

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Entreprise::class], version = 1)
abstract class TodoDatabase : RoomDatabase()
{
    abstract fun entrepriseDAO() : EntrepriseDAO

    companion object
    {
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