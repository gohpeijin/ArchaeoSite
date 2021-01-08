package com.project.archaeosite.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.project.archaeosite.helpers.*
import org.jetbrains.anko.AnkoLogger
import java.util.*

val JSON_FILE = "placemarks.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<java.util.ArrayList<ArchaeoModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class SiteJSONImplement : SiteInterface, AnkoLogger {

    val context: Context
    var sites = mutableListOf<ArchaeoModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<ArchaeoModel> {
        return sites
    }

    override fun create(site: ArchaeoModel) {
        site.id = generateRandomId()
        sites.add(site)
        serialize()
    }


    override fun update(site: ArchaeoModel) {
        var foundsite =sites.find { it.id ==site.id}
        if (foundsite!=null){
            foundsite.title=site.title
            foundsite.title = site.title
            foundsite.description = site.description
            foundsite.image = site.image
            foundsite.lat = site.lat
            foundsite.lng = site.lng
            foundsite.zoom = site.zoom

        }
        serialize()
    }

    override fun delete(site: ArchaeoModel) {
        sites.remove(site)
        serialize()
    }

    override fun findById(id: Long): ArchaeoModel? {
        TODO("Not yet implemented")
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(sites, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        sites = Gson().fromJson(jsonString, listType)
    }
}