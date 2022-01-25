package com.example.a00_tp_android

import android.icu.text.DateFormat
import androidx.room.*
import java.io.Serializable
import java.time.format.DateTimeFormatter
import java.util.*


@Entity
data class CacheRequete(@PrimaryKey(autoGenerate = true) var id                 : Long? = null,
                                                         var chaineRecherchee   : String,
                                                         var villeOuDepartement : String/*,
                                                         var dateRequete        : Date*/
                                                         ) : Serializable
{
    override fun toString() : String
    {
        return "CacheRequete(id=$id, chaineRecherchee='$chaineRecherchee', villeOuDepartement='$villeOuDepartement')"
    }
}


@Dao
interface CacheRequeteDAO
{
    @Query("SELECT * FROM CacheRequete ORDER BY chaineRecherchee")
    fun getAll() : List<CacheRequete>

    @Query("SELECT * FROM CacheRequete WHERE id=:idRequete")
    fun getById(idRequete : Long?) : CacheRequete?

    @Query("SELECT * FROM CacheRequete WHERE chaineRecherchee LIKE :chaine")
    fun getByChaineRecherchee(chaine : String) : CacheRequete?

    @Query("SELECT * FROM CacheRequete WHERE chaineRecherchee LIKE :chaine AND villeOuDepartement LIKE :chaineVilleCP")
    fun getByChaineRecherchee(chaine : String, chaineVilleCP : String) : CacheRequete?

    @Query("SELECT CRE.siret FROM CacheRequete AS CR JOIN CacheRequeteEntreprise AS CRE ON CR.id = CRE.idRecherche WHERE CR.chaineRecherchee LIKE :chaine")
    fun getByRecherche(chaine : String) : List<Long>

    @Query("SELECT CRE.siret FROM CacheRequete AS CR JOIN CacheRequeteEntreprise AS CRE ON CR.id = CRE.idRecherche WHERE CR.chaineRecherchee LIKE :chaine AND CR.villeOuDepartement LIKE :chaineVilleCP")
    fun getByRecherche(chaine : String, chaineVilleCP : String) : List<Long>

    @Query("SELECT * FROM CacheRequete ORDER BY CacheRequete.chaineRecherchee LIMIT 1 OFFSET :position")
    fun getByPosition(position : Int): CacheRequete

    @Query("SELECT COUNT(*) FROM CacheRequete")
    fun count() : Int

    @Insert
    fun insert(cacheRequete : CacheRequete) : Long

    @Update
    fun update(cacheRequete : CacheRequete)

    @Delete
    fun delete(cacheRequete : CacheRequete)
}

@Entity(foreignKeys = [ForeignKey(entity        = CacheRequete::class,
                                  parentColumns = ["id"],
                                  childColumns  = ["idRecherche"],
                                  onDelete      = ForeignKey.CASCADE),
                       ForeignKey(entity        = Entreprise::class,
                                  parentColumns = ["siret"],
                                  childColumns  = ["siret"],
                                  onDelete      = ForeignKey.CASCADE)])
data class CacheRequeteEntreprise(@PrimaryKey(autoGenerate = true) var id          : Long? = null,
                                                                   var idRecherche : Long,
                                                                   var siret       : Long
                                                                   ) : Serializable
{
    override fun toString() : String
    {
        return "CacheRequeteEntreprise(id=$id, idRecherche=$idRecherche, siret='$siret')"
    }
}


@Dao
interface CacheRequeteEntrepriseDAO
{
    @Query("SELECT * FROM CacheRequeteEntreprise ORDER BY idRecherche")
    fun getAll() : List<CacheRequeteEntreprise>

    @Query("SELECT * FROM CacheRequeteEntreprise WHERE idRecherche=:idRequete")
    fun getById(idRequete: Long?) : CacheRequeteEntreprise?

    @Query("SELECT * FROM CacheRequeteEntreprise WHERE siret =:siret")
    fun getBySiret(siret : Long) : CacheRequeteEntreprise?

    @Query("SELECT E.siret FROM CacheRequete AS CR JOIN CacheRequeteEntreprise AS CRE ON CR.id = CRE.idRecherche JOIN Entreprise AS E ON E.siret = CRE.siret WHERE CR.id = :idCacheRequete")
    fun getByIdCacheRequete(idCacheRequete : Long) : List<Long>

    @Query("SELECT COUNT(*) FROM CacheRequeteEntreprise")
    fun count() : Int

    @Insert
    fun insert(cacheEntreprise : CacheRequeteEntreprise) : Long

    @Update
    fun update(cacheEntreprise : CacheRequeteEntreprise)

    @Delete
    fun delete(cacheEntreprise : CacheRequeteEntreprise)
}