package com.project.archaeosite.main

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.project.archaeosite.models.SiteInterface
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.models.firebase.SiteFireStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

   lateinit var sites:SiteInterface
   lateinit var hillfortlist:FirebaseRepo_Hillfort

    override fun onCreate() {
        super.onCreate()
        //sites=SiteImplement()
        //sites= SiteJSONImplement(applicationContext)
       // sites = SitesStoreRoom(applicationContext)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        sites = SiteFireStore(applicationContext)
        hillfortlist=FirebaseRepo_Hillfort()

        info("ArchaeoSite started")
    }
}