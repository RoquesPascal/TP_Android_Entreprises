package com.example.a00_tp_android

import androidx.room.*
import java.io.Serializable
import java.util.*


@Entity
data class Entreprise(@PrimaryKey(autoGenerate = false) var siret              : Long?,   //siret
                                                        var raisonSociale      : String?, //nom_raison_sociale
                                                        var adresse            : String?, //geo_adresse
                                                        var activitePrincipale : String?, //libelle_activite_principale_entreprise
                                                        var natureJuridique    : String?, //libelle_nature_juridique_entreprise
                                                        var email              : String?, //email
                                                        var departement        : String?  //departement
                                                        ) : Comparable<Entreprise>, Serializable
{
    override fun compareTo(other : Entreprise) : Int
    {
        return raisonSociale!!.compareTo(other.raisonSociale!!)
    }

    override fun toString() : String
    {
        return raisonSociale!! + ", " + adresse
    }
}


@Dao
interface EntrepriseDAO
{
    @Query("SELECT * FROM Entreprise ORDER BY raisonSociale")
    fun getAll() : List<Entreprise>

    @Query("SELECT * FROM Entreprise WHERE siret=:numSiret")
    fun getBySiret(numSiret : Long?) : Entreprise?

    @Query("SELECT * FROM Entreprise ORDER BY Entreprise.raisonSociale LIMIT 1 OFFSET :position")
    fun getByPosition(position : Int) : Entreprise

    @Query("SELECT * FROM Entreprise WHERE siret IN (:listeSirets)")
    fun getByPlusieursSiret(listeSirets : List<Long>) : List<Entreprise>

    @Query("SELECT COUNT(*) FROM Entreprise")
    fun count() : Int

    @Query("SELECT COUNT(*) FROM Entreprise WHERE siret IN (:listeSiret)")
    fun countByPlusieursSirets(listeSiret : List<Long>) : Int

    @Insert
    fun insert(entreprise : Entreprise) : Long

    @Update
    fun update(entreprise : Entreprise)

    @Delete
    fun delete(entreprise : Entreprise)
}