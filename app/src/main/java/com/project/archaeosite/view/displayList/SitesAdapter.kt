package com.project.archaeosite.view.displayList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.archaeosite.R
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.card_sites.view.*
import java.util.*
import kotlin.collections.ArrayList

interface SitesListener{
    fun onSiteClick(site:ArchaeoModel)
}
class SitesAdapter constructor(private var sites: List<ArchaeoModel>, private val listener:SitesListener
    ) : RecyclerView.Adapter<SitesAdapter.MainHolder>(), Filterable {

    var siteFilterList = ArrayList<ArchaeoModel>()

    init {
        siteFilterList = sites as ArrayList<ArchaeoModel>
    }

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
        val site = siteFilterList[holder.adapterPosition]
        holder.bind(site,listener)
    }

    override fun getItemCount(): Int =  siteFilterList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    siteFilterList = sites as ArrayList<ArchaeoModel>
                } else {
                    val resultList = ArrayList<ArchaeoModel>()
                    for (site in sites) {
                        if (site.title.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(site)
                        }
                    }
                    siteFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = siteFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                siteFilterList = results?.values as ArrayList<ArchaeoModel>
                notifyDataSetChanged()
            }

        }
    }


}