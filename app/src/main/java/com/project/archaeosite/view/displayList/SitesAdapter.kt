package com.project.archaeosite.view.displayList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.archaeosite.R
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_site.*
import kotlinx.android.synthetic.main.card_sites.view.*

interface SitesListener{
    fun onSiteClick(site:ArchaeoModel)
}
class SitesAdapter constructor(private var sites: List<ArchaeoModel>, private val listener:SitesListener
    ) : RecyclerView.Adapter<SitesAdapter.MainHolder>(){

    inner class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(site: ArchaeoModel, listener: SitesListener) {
            itemView.siteName.text = site.title
            itemView.siteDescription.text = site.description
            if(site.visited) itemView.textView_Visited.text="Visited"
            else itemView.textView_Visited.text="Unvisited"
            if(site.additionalNote.isNotBlank()) itemView.textView_Additionalnote.visibility=View.VISIBLE
            else itemView.textView_Additionalnote.visibility=View.INVISIBLE
            if(site.date.day==0&&site.date.month==0&&site.date.year==0) itemView.textView_Date.text = "No date"
            else itemView.textView_Date.text = "${site.date.day}/${site.date.month}/${site.date.year}"
            if(site.image.isNotEmpty()){
                Glide.with(itemView.context).load(site.image.get(0)).into(itemView.imageIcon)
                itemView.textView_ImageCount.text="${site.image.size} image(s)"
            }
            else  itemView.textView_ImageCount.text="No image"

            itemView.setOnClickListener(){
                listener.onSiteClick(site)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.card_sites, parent, false)
        return MainHolder(view)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val site =sites[holder.adapterPosition]
        holder.bind(site,listener)
    }

    override fun getItemCount(): Int = sites.size


}