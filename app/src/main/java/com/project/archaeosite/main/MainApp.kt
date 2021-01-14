package com.project.archaeosite.main

import android.app.Application
import com.project.archaeosite.models.SiteMemImplement
import com.project.archaeosite.models.SiteInterface
import com.project.archaeosite.models.SiteJSONImplement
import com.project.archaeosite.models.firebase.SiteFireStore
import com.project.archaeosite.room.SitesStoreRoom
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

   lateinit var sites:SiteInterface

    override fun onCreate() {
        super.onCreate()
        //sites=SiteImplement()
        //sites= SiteJSONImplement(applicationContext)
       // sites = SitesStoreRoom(applicationContext)
        sites = SiteFireStore(applicationContext)
        info("ArchaeoSite started")

    }


}