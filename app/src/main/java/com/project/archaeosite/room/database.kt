package com.project.archaeosite.room


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.ImageConverter

@Database(entities = arrayOf(ArchaeoModel::class), version = 1,  exportSchema = false)
@TypeConverters(ImageConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun siteDao(): SiteDao
}
