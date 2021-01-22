package com.project.archaeosite.view.hillfort

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.HILLFORT_FAV_LIST
import com.project.archaeosite.view.base.HILLFORT_LIST
import kotlinx.android.synthetic.main.activity_hillfort_view.*
import kotlinx.android.synthetic.main.dialog_hillfortsite.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.info
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
        recyclerview_hillfort.layoutManager = layoutManager
        presenter.loadHillfortList(HILLFORT_LIST)
        var clicked = false
        floatingActionButton_fav.setOnClickListener {
            if (!clicked) {
                presenter.loadHillfortList(HILLFORT_FAV_LIST)
                floatingActionButton_fav.backgroundTintList = ContextCompat.getColorStateList(
                    this,
                    R.color.fav_toggle_red
                )
                info("FALSE")
                clicked = true
            } else {
                presenter.loadHillfortList(HILLFORT_LIST)
                floatingActionButton_fav.backgroundTintList = ContextCompat.getColorStateList(
                    this,
                    R.color.fav_toggle_grey
                )
                info("TRUE")
                clicked = false
            }
        }

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

    override fun showHillfortList(hillfortList: List<HillfortModel>) {
        adapter = HillfortListAdapter(hillfortList, this)
        recyclerview_hillfort.adapter = adapter
        recyclerview_hillfort.adapter?.notifyDataSetChanged()
        if (!searchhistory.isNullOrBlank())
            adapter.filter.filter(searchhistory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadHillfortList(HILLFORT_LIST)
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
        Glide.with(this).load(hillfort.Image).into(hillfortDialog.imageView)
        hillfortDialog.dialog_Location.text = hillfort.Location.toString()

        var specifyUserReaction = presenter.getIndiReactionModel(hillfort)

        hillfortDialog.dialog_checkBox_Visited.isChecked = specifyUserReaction.visited
        hillfortDialog.checkBox_favourite.isChecked = specifyUserReaction.favourite
        if (specifyUserReaction.rating == null) hillfortDialog.ratingBar.rating = 0F
        else hillfortDialog.ratingBar.rating = specifyUserReaction.rating!!

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
        hillfortDialog.ratingBar.setOnRatingBarChangeListener { ratingBar: RatingBar?, rating: Float, fromUser: Boolean ->
            presenter.doGetRating(rating, hillfort)

            hillfortDialog.ratingBarAvg.rating = presenter.doCalculateAvg(hillfort)
            if (hillfortDialog.ratingBarAvg.rating == 0F)
                hillfortDialog.textView_avgRating.text = "(no rating)"
            else
                hillfortDialog.textView_avgRating.text = "(${hillfortDialog.ratingBarAvg.rating})"
        }

        hillfortDialog.image_share.setOnClickListener {
            presenter.doShareSite(hillfort)

            val b: Bitmap = screenshot(hillfortDialog)!!
            shareImage(store(b, "Hillfort.png")!!)
        }
    }

    private fun screenshot(dialog: Dialog): Bitmap? {
        val window: Window = dialog.window!!
        val decorView: View = window.decorView
        val bitmap = Bitmap.createBitmap(decorView.width, decorView.height, Bitmap.Config.ARGB_8888)
        decorView.draw(Canvas(bitmap))
        return bitmap
    }


    fun store(bm: Bitmap, fileName: String?) : File?{
//        val dirPath: String =
//            Environment.getExternalStorageDirectory().absolutePath.toString() + "/Screenshots"
       val dirPath = getExternalFilesDir(null)!!.absolutePath.toString() + "/Screenshots"
        val dir = File(dirPath)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dirPath, fileName)
        info("FLAG1->" + dirPath)
        info("FLAG1->" + dir)
        info("FLAG1->" + file)

        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file
    }

    private fun shareImage(file: File) {
       // val uri: Uri = Uri.fromFile(file)

        val uri = FileProvider.getUriForFile(
            this,
            this.applicationContext.packageName.toString() + ".provider",
            file
        )
        info("FLAG3->" + uri)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_SUBJECT, "extra sub")
        intent.putExtra(Intent.EXTRA_TEXT, "extra text")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }

    //endregion

}



