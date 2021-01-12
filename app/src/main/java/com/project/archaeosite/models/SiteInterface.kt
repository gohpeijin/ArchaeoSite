package com.project.archaeosite.models

interface SiteInterface {
    fun findAll(): List<ArchaeoModel>  //for recycle view to display all the sites????
    fun create(site: ArchaeoModel) //use to add the site into the arraylist and display all the site on the logcat "logAll() fun"
    fun update(site: ArchaeoModel) //use to up date the site for edit their values and display all the site on the logcat "logAll() fun"
    fun delete(site: ArchaeoModel) //way of deleting sites
    fun findById(id:Long) : ArchaeoModel? // this is to find the marker id then display it on map there the name and the description
    fun clear()
}