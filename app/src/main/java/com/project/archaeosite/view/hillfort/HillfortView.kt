package com.project.archaeosite.view.hillfort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.project.archaeosite.R
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.view.displayList.SitesAdapter
import kotlinx.android.synthetic.main.activity_display_lists.*
import kotlinx.android.synthetic.main.activity_hillfort_view.*
import kotlinx.android.synthetic.main.card_hillfort.view.*
import kotlinx.android.synthetic.main.card_hillfort.view.imageIcon
import kotlinx.android.synthetic.main.card_hillfort.view.siteName
import kotlinx.android.synthetic.main.card_sites.view.*


class HillfortView : AppCompatActivity() {

    val firebaseRepo:FirebaseRepo= FirebaseRepo()
    var hillfortlist =ArrayList<HillfortModel>()

    val TAG = "MyMessage"
    val hillfortListAdapter:HillfortListAdapter=HillfortListAdapter(hillfortlist)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort_view)



        loadPostData()
        val layoutManager = LinearLayoutManager(this)
        recyclerview_hillfort.layoutManager= layoutManager
        recyclerview_hillfort.adapter = hillfortListAdapter
    }


   private fun loadPostData(){
        firebaseRepo.getPostList().addOnCompleteListener {
            if(it.isSuccessful){
                hillfortlist= it.result!!.toObjects(HillfortModel::class.java) as ArrayList<HillfortModel>
                hillfortListAdapter.hillfortItems=hillfortlist
                hillfortListAdapter.notifyDataSetChanged()
            }else{
                Log.d(TAG,"Error:${it.exception!!.message}")
            }
        }
    }

}

class FirebaseRepo{

    val firebaseAuth: FirebaseAuth= FirebaseAuth.getInstance()
    val firebaseFirestore:FirebaseFirestore= FirebaseFirestore.getInstance()

    fun getPostList(): Task<QuerySnapshot> {
        return firebaseFirestore.collection("Hillforts").get()
    }
}

class HillfortListAdapter(var hillfortItems: List<HillfortModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

   class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(hillfortmodel: HillfortModel) {
           itemView.siteName.text=hillfortmodel.Title
            Glide.with(itemView.context).load(hillfortmodel.Image).into(itemView.imageIcon)
            itemView.siteLocation.text=hillfortmodel.Location.toString()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.card_hillfort, parent, false)
        return MainHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MainHolder).bind(hillfortItems[position])
//        val hillfortmodel =hillfortItems[holder.adapterPosition]
//        holder.bind(hillfortmodel)
    }

    override fun getItemCount(): Int {
        return hillfortItems.size
    }

}