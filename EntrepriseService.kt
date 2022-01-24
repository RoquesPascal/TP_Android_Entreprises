package com.example.a00_tp_android

import android.util.JsonReader
import android.util.JsonToken
import java.io.IOException
import java.net.URL
import java.time.format.DateTimeFormatter
import java.util.*
import javax.net.ssl.HttpsURLConnection


class EntrepriseService
{
    private val serverUrl = "https://entreprise.data.gouv.fr"
    private val apiUrl = "$serverUrl/api/sirene"
    private val entrepriseUrl = "$apiUrl/v1/full_text/%s"


    fun getEntreprise(query: String): List<Entreprise>
    {
        val url = URL(String.format(entrepriseUrl, query))
        var conn: HttpsURLConnection? = null
        try
        {
            conn = url.openConnection() as HttpsURLConnection
            conn.connect()
            if (conn.responseCode != HttpsURLConnection.HTTP_OK) return emptyList()
            val inputStream = conn.inputStream ?: return emptyList()
            val reader = JsonReader(inputStream.bufferedReader())
            val result = mutableListOf<Entreprise>()
            reader.beginObject()
            while (reader.hasNext())
            {
                if(reader.nextName().equals("etablissement"))
                {
                    reader.beginArray()
                    while(reader.hasNext())
                    {
                        reader.beginObject()
                        val entreprise = Entreprise(null, null, null, null, null, null, null)
                        while (reader.hasNext())
                        {
                            when (reader.nextName())
                            {
                                "siret"                                  -> if(reader.peek() !== JsonToken.NULL) entreprise.siret              = reader.nextLong()   else reader.skipValue()
                                "nom_raison_sociale"                     -> if(reader.peek() !== JsonToken.NULL) entreprise.raisonSociale      = reader.nextString() else reader.skipValue()
                                "geo_adresse"                            -> if(reader.peek() !== JsonToken.NULL) entreprise.adresse            = reader.nextString() else reader.skipValue()
                                "libelle_activite_principale_entreprise" -> if(reader.peek() !== JsonToken.NULL) entreprise.activitePrincipale = reader.nextString() else reader.skipValue()
                                "libelle_nature_juridique_entreprise"    -> if(reader.peek() !== JsonToken.NULL) entreprise.natureJuridique    = reader.nextString() else reader.skipValue()
                                "email"                                  -> if(reader.peek() !== JsonToken.NULL) entreprise.email              = reader.nextString() else reader.skipValue()
                                "departement"                            -> if(reader.peek() !== JsonToken.NULL) entreprise.departement        = reader.nextString() else reader.skipValue()
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        result.add(entreprise)
                    }
                    reader.endArray()
                }
                else reader.skipValue()
            }
            reader.endObject()
            reader.close()
            result.sort()
            return result
        }
        catch (e: IOException)
        {
            return emptyList()
        }
        finally
        {
            conn?.disconnect()
        }
    }
}