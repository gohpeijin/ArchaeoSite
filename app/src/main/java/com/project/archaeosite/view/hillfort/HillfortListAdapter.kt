package com.project.archaeosite.view.hillfort

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.archaeosite.R
import com.project.archaeosite.models.HillfortModel
import kotlinx.android.synthetic.main.card_hillfort.view.*

interface HillfortListener{
    fun onHillfortClick(hillfort: HillfortModel)
}

class HillfortListAdapter(var hillfortItems: List<HillfortModel>,private val listener: HillfortListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(hillfortmodel: HillfortModel,listener: HillfortListener) {
            itemView.siteName.text=hillfortmodel.Title
            Glide.with(itemView.context).load(hillfortmodel.Image).into(itemView.imageIcon)
            itemView.siteLocation.text=hillfortmodel.Location.toString()
            itemView.siteid.text=hillfortmodel.itemId

            itemView.setOnClickListener(){
                listener.onHillfortClick(hillfortmodel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.card_hillfort, parent, false)
        return MainHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MainHolder).bind(hillfortItems[position],listener)
//        val hillfortmodel =hillfortItems[holder.adapterPosition]
//        holder.bind(hillfortmodel)
    }

    override fun getItemCount(): Int {
        return hillfortItems.size
    }

}