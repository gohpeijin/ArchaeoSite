package com.project.archaeosite.view.hillfort

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_hillfort_view.*
import kotlinx.android.synthetic.main.dialog_hillfortsite.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.info
import java.lang.reflect.Field


class HillfortView :  BaseView(), HillfortListener {

    lateinit var presenter: HillfortPresenter
    lateinit var adapter: HillfortListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort_view)

        //region initialize an empty list so before the hillforts list callback it wont crash when user search
        adapter = HillfortListAdapter(mutableListOf(), this)
        recyclerview_hillfort.adapter = adapter
        //endregion

        presenter = initPresenter(HillfortPresenter(this)) as HillfortPresenter

        //region drawer
        navigation_view.setCheckedItem(R.id.item_hillfort)
        navigation_view.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.item_add -> {
                    presenter.doAddSite()
                }
                R.id.item_map -> {
                    presenter.doShowSitesMap()
                }
                R.id.item_logout -> {
                    presenter.doLogout()
                }
                R.id.item_profile -> {
                    presenter.doOpenProfile()
                }
                R.id.item_home -> {
                    presenter.doDisplayList()
                }
            }
            true
        }

        super.init(mytoolbar, false)

        val drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //endregion

        val layoutManager = LinearLayoutManager(this)
        recyclerview_hillfort.layoutManager= layoutManager
        presenter.loadHillfortList()

        //region interface for floating botton
        recyclerview_hillfort.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    floatingActionButton_fav.hide()
                } else {
                    floatingActionButton_fav.show()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        //endregion

    }

    override fun showHillfortList(hillfortList: List<HillfortModel>){
        adapter= HillfortListAdapter(hillfortList, this)
        recyclerview_hillfort.adapter=adapter
        recyclerview_hillfort.adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        info("this is hillfort")
        presenter.loadHillfortList()
        super.onActivityResult(requestCode, resultCode, data)
    }
    //region menu
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            useremail.text = user.email
        }
        return when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchViewItem = menu!!.findItem(R.id.search)
        val searchView = MenuItemCompat.getActionView(searchViewItem) as SearchView

        val searchTextView: SearchAutoComplete = searchView.findViewById(R.id.search_src_text)
        try {
            val field: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            field.set(searchTextView, R.drawable.cursor)
        } catch (e: Exception) {
            // Ignore exception
        }

        searchView.queryHint="Search Hillfort..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    //endregion

    //region specify Hillfort
    override fun onHillfortClick(hillfort: HillfortModel) {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_hillfortsite, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        //show dialog
        val  hillfortDialog = mBuilder.show()

        hillfortDialog.dialog_Title.text="Title: "+ hillfort.Title
        Glide.with(this).load(hillfort.Image).into(hillfortDialog.imageView)
        hillfortDialog.dialog_Location.text=hillfort.Location.toString()

        var specifyUserReaction = presenter.getIndiReactionModel(hillfort)

        hillfortDialog.dialog_checkBox_Visited.isChecked=specifyUserReaction.visited
        hillfortDialog.checkBox_favourite.isChecked=specifyUserReaction.favourite
        if(specifyUserReaction.rating==null) hillfortDialog.ratingBar.rating=0F
        else hillfortDialog.ratingBar.rating= specifyUserReaction.rating!!

        hillfortDialog.ratingBarAvg.rating=presenter.doCalculateAvg(hillfort)
        if (hillfortDialog.ratingBarAvg.rating==0F) hillfortDialog.textView_avgRating.text="(no rating)"
        else hillfortDialog.textView_avgRating.text="(${hillfortDialog.ratingBarAvg.rating})"


        hillfortDialog.dialog_button_Done.setOnClickListener {
            hillfortDialog.dismiss()
            recyclerview_hillfort.adapter?.notifyDataSetChanged()
        }

        hillfortDialog.dialog_checkBox_Visited.setOnClickListener { presenter.doVisitedCheckbox(hillfortDialog.dialog_checkBox_Visited.isChecked, hillfort) }
        hillfortDialog.checkBox_favourite.setOnClickListener { presenter.doFavourite(hillfortDialog.checkBox_favourite.isChecked, hillfort) }

        hillfortDialog.ratingBar.setOnRatingBarChangeListener { ratingBar: RatingBar?, rating: Float, fromUser: Boolean ->
            presenter.doGetRating(rating, hillfort)

            hillfortDialog.ratingBarAvg.rating=presenter.doCalculateAvg(hillfort)
            if (hillfortDialog.ratingBarAvg.rating==0F)
                hillfortDialog.textView_avgRating.text="(no rating)"
            else
                hillfortDialog.textView_avgRating.text="(${hillfortDialog.ratingBarAvg.rating})"
        }
    }
    //endregion
}



