package com.project.archaeosite.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L //to generate id for the uniqueness of placemark

internal fun getId(): Long {
    return lastId++
}

class SiteImplement : SiteInterface, AnkoLogger {

    val sites= ArrayList<ArchaeoModel>()

    override fun findAll(): List<ArchaeoModel> {
        return sites
    }

    override fun create(site: ArchaeoModel) {
        site.id= getId()
        sites.add(site)
//        sites.add(ArchaeoModel(0,"test1","testing1"))
//        sites.add(ArchaeoModel(1,"test2","testing2"))
//        sites.add(ArchaeoModel(2,"test3","testing3"))
        displayAll()
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

    fun displayAll(){
        sites.forEach{info(it)}
    }
}