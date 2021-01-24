package com.project.archaeosite.view.displayList

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_display_lists.*
import kotlinx.android.synthetic.main.activity_display_lists.mytoolbar
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.*
import java.lang.reflect.Field

class DisplayListView : BaseView(), AnkoLogger, SitesListener {

    lateinit var presenter: DisplayListPresenter
    lateinit var adapter: SitesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lists)

        //region initialize an empty list so before the hillforts list callback it wont crash when user search
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
        presenter.loadSitesList()
    }

    override fun showSites(sites: List<ArchaeoModel>) {
        adapter= SitesAdapter(sites,this)
        recyclerview_sites.adapter = adapter
        recyclerview_sites.adapter?.notifyDataSetChanged()
        if(!searchhistory.isNullOrBlank())
            adapter.filter.filter(searchhistory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadSitesList()
//        info("testing" + searchhistory)
//        adapter.filter.filter(searchhistory)
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
                searchhistory=newText
                adapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    //endregion


    var searchhistory:String?=null
    override fun onSiteClick(site: ArchaeoModel) {
        presenter.doEditSite(site)
    }


}



