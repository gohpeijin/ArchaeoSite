package com.project.archaeosite.main

import android.app.Application
import com.project.archaeosite.models.SiteImplement
import com.project.archaeosite.models.SiteInterface
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

   lateinit var sites:SiteInterface
    //val sites = SiteImplement()
    //val sites = ArrayList<ArchaeoModel>()
    override fun onCreate() {
        super.onCreate()
        sites=SiteImplement()
        info("ArchaeoSite started")

    }


}