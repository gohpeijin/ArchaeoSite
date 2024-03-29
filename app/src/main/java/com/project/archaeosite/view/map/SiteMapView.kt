package com.project.archaeosite.view.map

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.project.archaeosite.R
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_sites_maps.*
import kotlinx.android.synthetic.main.activity_sites_maps.mapView
import kotlinx.android.synthetic.main.activity_sites_maps.mytoolbar

class SiteMapView : BaseView(),GoogleMap.OnMarkerClickListener {

    lateinit var presenter: SiteMapPresenter
    lateinit var map : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sites_maps)

        presenter = initPresenter (SiteMapPresenter(this)) as SiteMapPresenter

        super.init(mytoolbar, true)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            map.setOnMarkerClickListener(this)
            presenter.loadSitesList()
            presenter.doLoadHillfortSite()
        }

        floatingActionButton_navi.setOnClickListener {
            presenter.doNavigator()
        }
    }
    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doMarkerSelected(marker)
        floatingActionButton_navi.visibility=View.VISIBLE
        map.uiSettings.isMapToolbarEnabled = true
        return false
    }
    //to display site content
    override fun setSiteContent(site: Any) {
        if(site is ArchaeoModel){
            currentTitle.text = site.title
            currentDescription.visibility= View.VISIBLE
            currentDescription.text = site.description
            if(site.image.isNotEmpty())
                Glide.with(this).load(site.image[0]).into(currentImage)
            else
                currentImage.setImageResource(R.drawable.no_image_available)
        }
        else if(site is HillfortModel){
            currentTitle.text=site.Title
            currentDescription.visibility= View.INVISIBLE
            if(site.Image.isNotEmpty())
                Glide.with(this).load(site.Image).into(currentImage)
            else
                currentImage.setImageResource(android.R.color.transparent)
        }

    }

    override fun showSites(sites: List<ArchaeoModel>) {
        presenter.doReadSiteLocationToMap(map,sites)
    }

    override fun showHillfortList(hillfortList: List<HillfortModel>) {
        presenter.doReadHillfortLocationToMap(map,hillfortList)
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}

