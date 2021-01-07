package com.project.archaeosite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.project.archaeosite.R
import com.project.archaeosite.helpers.readImage
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.helpers.showImagePicker
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class SiteActivity : AppCompatActivity(), AnkoLogger {
    var site = ArchaeoModel()
    lateinit var app: MainApp
    val IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        app = application as MainApp
        var edit=false

        if (intent.hasExtra("site_edit")) {
            edit=true
            site = intent.extras?.getParcelable<ArchaeoModel>("site_edit")!!
            text_Site_Name.setText(site.title)
            text_Site_Description.setText(site.description)
            button_Add_Site.setText("Save Site")
           // ImageSelected.setImageBitmap(readImageFromPath(this,site.image))
            ImageSelected.setImageBitmap(readImageFromPath(this, site.image!!.get(0)))
            item_delete.setVisibility(View.VISIBLE)
        }

        button_Add_Site.setOnClickListener(){
            site.title=text_Site_Name.text.toString()
            site.description=text_Site_Description.text.toString()
            if(site.title.isEmpty()){
                toast ("Please Enter a site name")
            }
            else {
                if(edit){
                    info("Edit Mode")
                    app.sites.update(site.copy())
                }
                else{
                    info("Create Mode")
                    app.sites.create(site.copy())
                }
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }
        }

        button_Select_Image.setOnClickListener{
            info("select image button pressed")
            showImagePicker(this,IMAGE_REQUEST)
        }

        button_next_image.setOnClickListener(){

        }
        item_back.setOnClickListener(){
            info("back icon pressed")
            finish()
        }
        item_delete.setOnClickListener {
            app.sites.delete(site)
            finish()
        }
    }

//    lateinit var imagePath: String
//    fun getPathFromURI(uri: Uri) {
//        var path: String = "uri.path"// uri = any content Uri
//        val databaseUri: Uri
//        val selection: String?
//        val selectionArgs: Array<String>?
//        if (path.contains("/document/image:")) { // files selected from "Documents"
//            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            selection = "_id=?"
//            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
//        } else { // files selected from all other sources, especially on Samsung devices
//            databaseUri = uri
//            selection = null
//            selectionArgs = null
//        }
//        try {
//            val projection = arrayOf(
//                MediaStore.Images.Media.DATA,
//                MediaStore.Images.Media._ID,
//                MediaStore.Images.Media.ORIENTATION,
//                MediaStore.Images.Media.DATE_TAKEN
//            ) // some example data you can query
//            val cursor = contentResolver.query(
//                databaseUri,
//                projection, selection, selectionArgs, null
//            )
//            if (cursor!!.moveToFirst()) {
//                val columnIndex = cursor.getColumnIndex(projection[0])
//                imagePath = cursor.getString(columnIndex)
//                // Log.e("path", imagePath);
//                site.image.add(imagePath)
//            }
//            cursor.close()
//        } catch (e: Exception) {
//           // Log.e(TAG, e.message, e)
//        }
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if(data!!.clipData!=null){
                    val count=data.clipData!!.itemCount
                    for(i in 0 until count){
//                        site.image!!.add(data.getData().toString())
                      val imageUri=data.clipData!!.getItemAt(i).uri
//                        site.image!!.add(imageUri)
//                        getPathFromURI(imageUri)
                        site.image!!.add(imageUri.toString())

                    }
                        ImageSelected.setImageBitmap(readImageFromPath(this, site.image!!.get(0)))
                }
                else{
                    //val iamgeUri=data.data
                    site.image!!.add(data.getData().toString())
                    ImageSelected.setImageBitmap(readImage(this, resultCode, data))

                    //readImage(this, resultCode, data)
                }
                //content://com.android.providers.media.documents/document/image%3A229292
               // ImageSelected.setImageBitmap(site.image.get(0))
//                if (data != null) {
//                    info("setting up image")
//                   // site.image = data.getData().toString()
//                    site.image!!.add(data.getData().toString())
//                 //  site.image ="content://com.android.externalstorage.documents/document/3232-6531%3ADalat%2F20191005_130946_018_saved.jpg"
//                    ImageSelected.setImageBitmap(readImage(this, resultCode, data))
//                }
            }
        }
    }

}
