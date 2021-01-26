package com.project.archaeosite.view.displayList

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.DISPLAY_ALL_LIST
import com.project.archaeosite.view.base.DISPLAY_FAV_LIST
import kotlinx.android.synthetic.main.activity_display_lists.*
import kotlinx.android.synthetic.main.activity_display_lists.drawer
import kotlinx.android.synthetic.main.activity_display_lists.floatingActionButton_fav
import kotlinx.android.synthetic.main.activity_display_lists.mytoolbar
import kotlinx.android.synthetic.main.activity_display_lists.navigation_view
import kotlinx.android.synthetic.main.dialog_individualsite.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.*
import java.lang.reflect.Field

class DisplayListView : BaseView(), AnkoLogger, SitesListener {

    lateinit var presenter: DisplayListPresenter
    lateinit var adapter: SitesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lists)

        //region initialize an empty list so when there is no site it wont crash when user search
        adapter= SitesAdapter(mutableListOf(),this)
        recyclerview_sites.adapter = adapter
        //endregion

        presenter = initPresenter (DisplayListPresenter (this)) as DisplayListPresenter

        //region drawer
        navigation_view.setCheckedItem(R.id.item_home)
        navigation_view.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.item_add -> {presenter.doAddSite() }
                R.id.item_map -> {presenter.doShowSitesMap() }
                R.id.item_logout ->{presenter.doLogout() }
                R.id.item_profile ->{presenter.doOpenProfile()}
                R.id.item_hillfort->{presenter.doShowHillfort() }
                }
            drawer.closeDrawer(GravityCompat.START)
            false
        }

        super.init(mytoolbar, false)

        val drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //endregion

        val layoutManager = LinearLayoutManager(this)
        recyclerview_sites.layoutManager = layoutManager
        presenter.loadSitesList(DISPLAY_ALL_LIST)

        //region interface for floating botton

        floatingActionButton_fav.setOnClickListener {
            if (!presenter.fav_clicked) {
                presenter.fav_clicked = true
                presenter.loadSitesList(DISPLAY_FAV_LIST)
                floatingActionButton_fav.backgroundTintList = ContextCompat.getColorStateList(this, R.color.fav_toggle_red)
            } else {
                presenter.fav_clicked = false
                presenter.loadSitesList(DISPLAY_ALL_LIST)
                floatingActionButton_fav.backgroundTintList = ContextCompat.getColorStateList(this, R.color.fav_toggle_grey)

            }
        }

        recyclerview_sites.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun showSites(sites: List<ArchaeoModel>) {
        adapter= SitesAdapter(sites,this)
        recyclerview_sites.adapter = adapter
        recyclerview_sites.adapter?.notifyDataSetChanged()
        if(!presenter.searchhistory.isNullOrBlank())
            adapter.filter.filter(presenter.searchhistory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(presenter.fav_clicked)
            presenter.loadSitesList(DISPLAY_FAV_LIST)
        else
            presenter.loadSitesList(DISPLAY_ALL_LIST)
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

        val searchTextView: SearchView.SearchAutoComplete = searchView.findViewById(R.id.search_src_text)
        try {
            val field: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            field.set(searchTextView, R.drawable.cursor)
        } catch (e: Exception) {
            // Ignore exception
        }

        searchView.queryHint="Search Site..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.searchhistory=newText
                adapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    //endregion


    @SuppressLint("SetTextI18n")
    override fun onSiteClick(site: ArchaeoModel) {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_individualsite, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        //show dialog
        val siteDialog = mBuilder.show()

        //region set default text
        siteDialog.dialog_Title.text = "Title: " + site.title

        if(site.image.isNotEmpty()){
            Glide.with(this).load(site.image[0]).into(siteDialog.dialog_imageView)
        }
        else siteDialog.dialog_imageView.setImageResource(R.drawable.no_image_available)

        if(site.image.size==1||site.image.isEmpty()){
            siteDialog.dialog_button_previos_image.visibility=View.INVISIBLE
            siteDialog.dialog_button_next_image.visibility= View.INVISIBLE
        }

        siteDialog.dialog_Location.text = site.location.toString()
        siteDialog.dialog_textView_Description.text="Description: ${site.description}"
        siteDialog.dialog_textView_Additionalnote.text="Addtional note: ${site.additionalNote}"
        siteDialog.dialog_checkBox_Visited.isChecked = site.visited
        siteDialog.dialog_checkBox_favourite.isChecked = site.favourite

        if (site.rating == null) {
            siteDialog.dialog_ratingBar.rating = 0F
            siteDialog.dialog_textView_Rating.text = "(no rating)"
        }
        else {
            siteDialog.dialog_ratingBar.rating = site.rating!!
            siteDialog.dialog_textView_Rating.text = "(${siteDialog.dialog_ratingBar.rating})"
        }

        //endregion

        siteDialog.dialog_button_Done.setOnClickListener {
            siteDialog.dismiss()
            recyclerview_sites.adapter?.notifyDataSetChanged()
        }

        siteDialog.image_share.setOnClickListener { presenter.doShareSite(site,siteDialog) }
        siteDialog.dialog_button_previos_image.setOnClickListener {
            Glide.with(this).load(site.image[presenter.doPreviousImage(site)]).into(siteDialog.dialog_imageView)

        }
        siteDialog.dialog_button_next_image.setOnClickListener {
            Glide.with(this).load(site.image[presenter.doNextImage(site)]).into(siteDialog.dialog_imageView)

        }

        siteDialog.image_edit.setOnClickListener {
            siteDialog.dismiss()

            presenter.doEditSite(site)
        }

        siteDialog.dialog_checkBox_favourite.setOnClickListener { presenter.doFavourite(siteDialog.dialog_checkBox_favourite.isChecked,site) }

        siteDialog.dialog_site_image_navigator.setOnClickListener { presenter.doNavigator(site) }
    }

    override fun showVisiblility() {
       textView_notifyNoSite.visibility=View.VISIBLE
    }

    override fun hideVisibility() {
        textView_notifyNoSite.visibility=View.INVISIBLE
    }
}



