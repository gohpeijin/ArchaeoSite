package com.project.archaeosite.view.displayList

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_display_lists.*
import kotlinx.android.synthetic.main.activity_display_lists.mytoolbar
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.*

class DisplayListView : BaseView(), AnkoLogger, SitesListener {

    lateinit var presenter: DisplayListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lists)

        presenter = initPresenter (DisplayListPresenter (this)) as DisplayListPresenter

        navigation_view.setCheckedItem(R.id.item_home)
        navigation_view.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.item_add -> {presenter.doAddSite() }
                R.id.item_map -> {presenter.doShowSitesMap() }
                R.id.item_logout ->{presenter.doLogout() }
                R.id.item_profile ->{presenter.doOpenProfile()}
                R.id.item_hillfort->{presenter.doShowHillfort()}
                }
            true
        }

        super.init(mytoolbar, false)

        val drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        recyclerview_sites.layoutManager = layoutManager
        presenter.loadSitesList()
    }

    override fun showSites(sites: List<ArchaeoModel>) {
        recyclerview_sites.adapter = SitesAdapter(sites,this)
        recyclerview_sites.adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadSitesList()
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
    //endregion

    override fun onSiteClick(site: ArchaeoModel) {
        presenter.doEditSite(site)
    }


}



