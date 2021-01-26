package com.project.archaeosite.view.hillfort

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.DISPLAY_FAV_LIST
import com.project.archaeosite.view.base.DISPLAY_ALL_LIST
import kotlinx.android.synthetic.main.activity_hillfort_view.*
import kotlinx.android.synthetic.main.activity_hillfort_view.mytoolbar
import kotlinx.android.synthetic.main.activity_hillfort_view.progressBar
import kotlinx.android.synthetic.main.dialog_hillfortsite.*
import kotlinx.android.synthetic.main.nav_header_main.useremail
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
        navigation_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_add -> { presenter.doAddSite() }
                R.id.item_map -> { presenter.doShowSitesMap() }
                R.id.item_logout -> { presenter.doLogout() }
                R.id.item_profile -> { presenter.doOpenProfile() }
                R.id.item_home -> { presenter.doDisplayList() }
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
        recyclerview_hillfort.layoutManager = layoutManager
        presenter.loadHillfortList(DISPLAY_ALL_LIST)

        //region interface for floating botton
        var clicked = false
        floatingActionButton_fav.setOnClickListener {
            if (!clicked) {
                presenter.loadHillfortList(DISPLAY_FAV_LIST)
                floatingActionButton_fav.backgroundTintList = ContextCompat.getColorStateList(this, R.color.fav_toggle_red)
                clicked = true
            } else {
                presenter.loadHillfortList(DISPLAY_ALL_LIST)
                floatingActionButton_fav.backgroundTintList = ContextCompat.getColorStateList(this, R.color.fav_toggle_grey)
                clicked = false
            }
        }

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

    override fun showHillfortList(hillfortList: List<HillfortModel>) {
        adapter = HillfortListAdapter(hillfortList, this)
        recyclerview_hillfort.adapter = adapter
        recyclerview_hillfort.adapter?.notifyDataSetChanged()
        if (!searchhistory.isNullOrBlank())
            adapter.filter.filter(searchhistory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadHillfortList(DISPLAY_ALL_LIST)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
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

    var searchhistory: String? = null
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

        searchView.queryHint = "Search Hillfort..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchhistory = newText
                adapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    //endregion

    //region specify Hillfort
    @SuppressLint("SetTextI18n")
    override fun onHillfortClick(hillfort: HillfortModel) {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_hillfortsite, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        //show dialog
        val hillfortDialog = mBuilder.show()

        //region set default text
        hillfortDialog.dialog_Title.text = "Title: " + hillfort.Title
        Glide.with(this).load(hillfort.Image).into(hillfortDialog.dialog_imageView)
        hillfortDialog.dialog_Location.text = hillfort.Location.toString()

        var specifyUserReaction = presenter.getIndiReactionModel(hillfort)

        hillfortDialog.dialog_checkBox_Visited.isChecked = specifyUserReaction.visited
        hillfortDialog.checkBox_favourite.isChecked = specifyUserReaction.favourite
        if (specifyUserReaction.rating == null) hillfortDialog.dialog_ratingBar.rating = 0F
        else hillfortDialog.dialog_ratingBar.rating = specifyUserReaction.rating!!

        hillfortDialog.ratingBarAvg.rating = presenter.doCalculateAvg(hillfort)
        if (hillfortDialog.ratingBarAvg.rating == 0F) hillfortDialog.textView_avgRating.text =
            "(no rating)"
        else hillfortDialog.textView_avgRating.text = "(${hillfortDialog.ratingBarAvg.rating})"
        //endregion

        hillfortDialog.dialog_button_Done.setOnClickListener {
            hillfortDialog.dismiss()
            recyclerview_hillfort.adapter?.notifyDataSetChanged()
        }
        hillfortDialog.dialog_checkBox_Visited.setOnClickListener {
            presenter.doVisitedCheckbox(
                hillfortDialog.dialog_checkBox_Visited.isChecked,
                hillfort
            )
        }
        hillfortDialog.checkBox_favourite.setOnClickListener {
            presenter.doFavourite(
                hillfortDialog.checkBox_favourite.isChecked,
                hillfort
            )
        }
        hillfortDialog.dialog_ratingBar.setOnRatingBarChangeListener { ratingBar: RatingBar?, rating: Float, fromUser: Boolean ->
            presenter.doGetRating(rating, hillfort)

            hillfortDialog.ratingBarAvg.rating = presenter.doCalculateAvg(hillfort)
            if (hillfortDialog.ratingBarAvg.rating == 0F)
                hillfortDialog.textView_avgRating.text = "(no rating)"
            else
                hillfortDialog.textView_avgRating.text = "(${hillfortDialog.ratingBarAvg.rating})"
        }

        hillfortDialog.image_share.setOnClickListener {
            presenter.doShareSite(hillfort,hillfortDialog)
        }

        hillfortDialog.dialog_hillfort_image_navigator.setOnClickListener { presenter.doNavigator(hillfort) }
    }
    //endregion

}



