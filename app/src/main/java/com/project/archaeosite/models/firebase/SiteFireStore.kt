package com.project.archaeosite.models.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.SiteInterface
import org.jetbrains.anko.AnkoLogger

class SiteFireStore(val context: Context) : SiteInterface, AnkoLogger {

    val sites = ArrayList<ArchaeoModel>()
    lateinit var userId: String
    lateinit var db: DatabaseReference

    override fun findAll(): List<ArchaeoModel> {
        return sites
    }

    override fun create(site: ArchaeoModel) {
        val key = db.child("users").child(userId).child("sites").push().key
        key?.let {
            site.fbId = key
           sites.add(site)
            db.child("users").child(userId).child("sites").child(key).setValue(site)
        }
    }

    override fun update(site: ArchaeoModel) {
        var foundsite =sites.find { it.id ==site.id}
        if (foundsite!=null){
            foundsite.title = site.title
            foundsite.description = site.description
            foundsite.image = site.image
            foundsite.location = site.location
        }
        db.child("users").child(userId).child("sites").child(site.fbId).setValue(site)
    }

    override fun delete(site: ArchaeoModel) {
        db.child("users").child(userId).child("sites").child(site.fbId).removeValue()
        sites.remove(site)
    }

    override fun findById(id: Long): ArchaeoModel? {
        val foundsite =sites.find { it.id ==id}
        return foundsite
    }

    override fun clear() {
        sites.clear()
    }

    fun fetchSites(sitesReady: () -> Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot!!.children.mapNotNullTo(sites) { it.getValue<ArchaeoModel>(ArchaeoModel::class.java) }
                sitesReady()
            }
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference
        sites.clear()
        db.child("users").child(userId).child("sites").addListenerForSingleValueEvent(valueEventListener)
    }
}