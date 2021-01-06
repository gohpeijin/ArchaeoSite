package com.project.archaeosite.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.archaeosite.R
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.card_sites.view.*

interface SitesListener{
    fun onSiteClick(site:ArchaeoModel)
}
class SitesAdapter constructor(private var sites: List<ArchaeoModel>, private val listener:SitesListener
    ) : RecyclerView.Adapter<SitesAdapter.MainHolder>(){

    inner class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(site: ArchaeoModel,listener: SitesListener) {
            itemView.siteName.text = site.title
            itemView.siteDescription.text = site.description
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