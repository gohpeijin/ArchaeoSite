package com.project.archaeosite.view.hillfort

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.models.HillfortModel
import kotlinx.android.synthetic.main.card_hillfort.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*
import kotlin.collections.ArrayList

interface HillfortListener{
    fun onHillfortClick(hillfort: HillfortModel)
}

class HillfortListAdapter(var hillfortItems: List<HillfortModel>, private val listener: HillfortListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var hillfortFilterList = ArrayList<HillfortModel>()

    init {
        hillfortFilterList = hillfortItems as ArrayList<HillfortModel>
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(hillfortmodel: HillfortModel, listener: HillfortListener) {
            val user = FirebaseAuth.getInstance().currentUser
            itemView.siteName.text=hillfortmodel.Title
            Glide.with(itemView.context).load(hillfortmodel.Image).into(itemView.imageIcon)
            itemView.siteLocation.text=hillfortmodel.Location.toString()

            var reacted=false
            for (userReactions in  hillfortmodel.userReaction) {
                if(userReactions.userID== user!!.uid ){
                    reacted=true
                    if(userReactions.visited) itemView.textView_Visited.text="Visited"
                    else itemView.textView_Visited.text="Unvisited"
                    itemView.checkBox_favourite.isChecked = userReactions.favourite
                }
                if(reacted)
                    break
            }

            if(!reacted)
                itemView.textView_Visited.text="Unvisited"

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
        (holder as MainHolder).bind(hillfortFilterList[position], listener)
    }

    override fun getItemCount(): Int {
        return hillfortFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    hillfortFilterList = hillfortItems as ArrayList<HillfortModel>
                } else {
                    val resultList = ArrayList<HillfortModel>()
                    for (hillfort in hillfortItems) {
                        if (hillfort.Title.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(hillfort)
                        }
                    }
                    hillfortFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = hillfortFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                hillfortFilterList = results?.values as ArrayList<HillfortModel>
                notifyDataSetChanged()
            }

        }
    }

}