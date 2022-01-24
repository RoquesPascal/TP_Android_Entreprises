package com.example.a00_tp_android

import androidx.room.*
import java.io.Serializable
import java.util.*


@Entity
data class CacheRequeteRecherche(@PrimaryKey(autoGenerate = true) var id               : Long? = null,
                                                                  var chaineRecherchee : String,
                                                                  var dateRequete      : Date
                                                                  ) : Serializable
{

}


@Dao
interface CacheRequeteRechercheDAO
{
    @Query("SELECT * FROM Entreprise ORDER BY raisonSociale")
    fun getAll() : List<Entreprise>

    @Query("SELECT * FROM Entreprise WHERE siret=:numSiret")
    fun getById(numSiret : Long?) : Entreprise?

    @Query("SELECT * FROM Entreprise ORDER BY Entreprise.raisonSociale LIMIT 1 OFFSET :position")
    fun getByPosition(position : Int) : Entreprise

    @Query("SELECT COUNT(*) FROM Entreprise")
    fun count() : Int

    @Insert
    fun insert(cacheRequete : CacheRequeteRecherche) : Long

    @Update
    fun update(cacheRequete : CacheRequeteRecherche)

    @Delete
    fun delete(cacheRequete : CacheRequeteRecherche)
}


@Entity
data class CacheRequeteEntrepriseRecherchee(@PrimaryKey(autoGenerate = true) var id          : Long? = null,
                                                                             var idRecherche : Long,
                                                                             var siret       : String
                                                                             ) : Serializable
{

}


@Dao
interface CacheRequeteEntrepriseRechercheeDAO
{
    @Query("SELECT * FROM Entreprise ORDER BY raisonSociale")
    fun getAll() : List<Entreprise>

    @Query("SELECT * FROM Entreprise WHERE siret=:numSiret")
    fun getById(numSiret : Long?) : Entreprise?

    @Query("SELECT * FROM Entreprise ORDER BY Entreprise.raisonSociale LIMIT 1 OFFSET :position")
    fun getByPosition(position : Int) : Entreprise

    @Query("SELECT COUNT(*) FROM Entreprise")
    fun count() : Int

    @Insert
    fun insert(cacheEntreprise : CacheRequeteEntrepriseRecherchee) : Long

    @Update
    fun update(cacheEntreprise : CacheRequeteEntrepriseRecherchee)

    @Delete
    fun delete(cacheEntreprise : CacheRequeteEntrepriseRecherchee)
}