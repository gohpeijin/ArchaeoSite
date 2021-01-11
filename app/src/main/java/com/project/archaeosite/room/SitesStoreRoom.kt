package com.project.archaeosite.room

import android.content.Context
import androidx.room.Room
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.SiteInterface

class SitesStoreRoom(val context: Context) : SiteInterface {
    var dao: SiteDao

    init {
        val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
            .fallbackToDestructiveMigration()
            .build()
        dao = database.siteDao()
    }

    override fun findAll(): List<ArchaeoModel> {
        return dao.findAll()
    }

    override fun create(site: ArchaeoModel) {
        dao.create(site)
    }

    override fun update(site: ArchaeoModel) {
        dao.update(site)
    }

    override fun delete(site: ArchaeoModel) {
        dao.deleteSite(site)
    }

    override fun findById(id: Long): ArchaeoModel? {
        return dao.findById(id)
    }
}