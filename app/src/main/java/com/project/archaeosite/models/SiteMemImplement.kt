package com.project.archaeosite.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L //to generate id for the uniqueness of placemark

internal fun getId(): Long {
    return lastId++
}

class SiteMemImplement : SiteInterface, AnkoLogger {

    val sites= ArrayList<ArchaeoModel>()

    override fun findAll(): List<ArchaeoModel> {
        return sites
    }

    override fun create(site: ArchaeoModel) {
        site.id= getId()
        sites.add(site)
        displayAll()
    }

    override fun update(site: ArchaeoModel) {
        var foundsite =sites.find { it.id ==site.id}
        if (foundsite!=null){
            foundsite.title = site.title
            foundsite.description = site.description
            foundsite.image = site.image
            foundsite.location = site.location
            displayAll()
        }
    }

    override fun delete(site: ArchaeoModel) {
       sites.remove(site)
    }

    override fun findById(id: Long): ArchaeoModel? {
        var foundsite =sites.find { it.id ==id}
        return foundsite
    }

    override fun clear() {
        sites.clear()
    }

    fun displayAll(){
        sites.forEach{info(it)}
    }
}