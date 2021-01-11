package com.project.archaeosite.room

import androidx.room.*
import com.project.archaeosite.models.ArchaeoModel

@Dao
interface SiteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(site: ArchaeoModel)

    @Query("SELECT * FROM ArchaeoModel")
    fun findAll(): List<ArchaeoModel>

    @Query("select * from ArchaeoModel where id = :id")
    fun findById(id: Long): ArchaeoModel

    @Update
    fun update(site: ArchaeoModel)

    @Delete
    fun deleteSite(site: ArchaeoModel)
}

