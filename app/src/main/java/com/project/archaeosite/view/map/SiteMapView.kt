package com.project.archaeosite.view.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.project.archaeosite.R
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_sites_maps.*

class SiteMapView : AppCompatActivity(),GoogleMap.OnMarkerClickListener {

    lateinit var presenter: SiteMapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sites_maps)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
           presenter.doReadSiteLocationToMap(it)
        }

        item_back_sitemaps.setOnClickListener() {
            finish()
        }
    }
    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doMarkerSelected(marker)
        return true
    }

   fun displaySiteContent(site:ArchaeoModel){
       currentTitle.text = site.title
       currentDescription.text = site.description
       if(site.image.isNotEmpty())
           currentImage.setImageBitmap(readImageFromPath(this, site.image.get(0)))
       else
           currentImage.setImageResource(android.R.color.transparent)
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

