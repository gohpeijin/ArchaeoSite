package com.project.archaeosite.models.firebase

import android.content.Context
import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.SiteInterface
import org.jetbrains.anko.AnkoLogger
import java.io.ByteArrayOutputStream
import java.io.File

class SiteFireStore(val context: Context) : SiteInterface, AnkoLogger {

    val sites = ArrayList<ArchaeoModel>()
    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference

    override fun findAll(): List<ArchaeoModel> {
        return sites
    }

    override fun create(site: ArchaeoModel) {
        val key = db.child("users").child(userId).child("sites").push().key
        key?.let {
            site.fbId = key
           sites.add(site)
            db.child("users").child(userId).child("sites").child(key).setValue(site)
            updateImage(site)
        }
    }

    override fun update(site: ArchaeoModel) {
        var foundsite =sites.find { it.fbId ==site.fbId}
        if (foundsite!=null){
            foundsite.title = site.title
            foundsite.description = site.description
            foundsite.image = site.image
            foundsite.location = site.location
        }
        db.child("users").child(userId).child("sites").child(site.fbId).setValue(site)
         if ((site.image[0].length) > 0 && (site.image[0][0] != 'h')) {
            updateImage(site)
       }
    }

    override fun delete(site: ArchaeoModel) {
        db.child("users").child(userId).child("sites").child(site.fbId).removeValue()
        sites.remove(site)
    }

    override fun findById(id: Long): ArchaeoModel? {
        val foundsite =sites.find {  s -> s.id ==id}
        return foundsite
    }

    override fun clear() {
        sites.clear()
    }
    fun updateImage(site: ArchaeoModel) {
        val image_num= site.image.size
        for (num in 0 until image_num){
            if (site.image[num] !="") {
                //This will be called whenever the user selects an image.
                //This first part will load into a bitmap object the image the user as selected from the gallery:
                val fileName = File(site.image[num])
                val imageName = fileName.name

                var imageRef = st.child("$userId/$imageName")
                val baos = ByteArrayOutputStream()
                val bitmap = readImageFromPath(context, site.image[num])

                bitmap?.let {
                    //Then, if the bitmap successfully loaded, we compress it to save on bandwidth and obtain a reference to the bits:
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    //Then we upload to the firebase storage service:
                    uploadTask.addOnFailureListener {
                        println(it.message)
                    }.addOnSuccessListener { taskSnapshot ->
                        //If the upload goes successfully:
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            site.image[num] = it.toString()
                            db.child("users").child(userId).child("sites").child(site.fbId).setValue(site)
                        }
                    }
                }
            }
        }
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
        st = FirebaseStorage.getInstance().reference
        sites.clear()
        db.child("users").child(userId).child("sites").addListenerForSingleValueEvent(valueEventListener)
    }
}


class FirebaseRepo_Hillfort{
    val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getHillfortList(): Task<QuerySnapshot> {
        return firebaseFirestore.collection("Hillforts").get()
    }
}