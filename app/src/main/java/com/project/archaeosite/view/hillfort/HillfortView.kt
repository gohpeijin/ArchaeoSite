package com.project.archaeosite.view.hillfort

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_hillfort_view.*
import kotlinx.android.synthetic.main.activity_hillfort_view.drawer
import kotlinx.android.synthetic.main.activity_hillfort_view.mytoolbar
import kotlinx.android.synthetic.main.activity_hillfort_view.navigation_view
import kotlinx.android.synthetic.main.nav_header_main.*


class HillfortView :  BaseView() {

    lateinit var presenter: HillfortPresenter

    val firebaseRepoHillfort: FirebaseRepo_Hillfort = FirebaseRepo_Hillfort()
    var hillfortlist =ArrayList<HillfortModel>()

    val TAG = "MyMessage"
    val hillfortListAdapter:HillfortListAdapter=HillfortListAdapter(hillfortlist)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort_view)

        presenter = initPresenter (HillfortPresenter (this)) as HillfortPresenter

        navigation_view.setCheckedItem(R.id.item_hillfort)
        navigation_view.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.item_add -> {presenter.doAddSite() }
                R.id.item_map -> {presenter.doShowSitesMap() }
                R.id.item_logout ->{presenter.doLogout() }
                R.id.item_profile ->{presenter.doOpenProfile()}
                R.id.item_home->{presenter.doDisplayList()}
            }
            true
        }

        super.init(mytoolbar, false)

        val drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadHillfortData()
        val layoutManager = LinearLayoutManager(this)
        recyclerview_hillfort.layoutManager= layoutManager
        recyclerview_hillfort.adapter = hillfortListAdapter
    }


   private fun loadHillfortData(){
        firebaseRepoHillfort.getHillfortList().addOnCompleteListener {
            if(it.isSuccessful){
                hillfortlist= it.result!!.toObjects(HillfortModel::class.java) as ArrayList<HillfortModel>
                hillfortListAdapter.hillfortItems=hillfortlist
                hillfortListAdapter.notifyDataSetChanged()
            }else{
                Log.d(TAG,"Error:${it.exception!!.message}")
            }
        }
    }

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
}



