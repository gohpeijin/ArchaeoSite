package com.project.archaeosite.models


import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

//we need a unique way of identifying sites - this is usually via an id.
@Parcelize
@Entity
data class ArchaeoModel(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var fbId : String = "",
    var title: String = "",
    var description: String = "",
    var image: MutableList<String> = arrayListOf(),
    @Embedded var location : Location = Location()
): Parcelable


@Parcelize
data class Location(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f
) : Parcelable


@Parcelize
data class ArchaeoUser(
    var username: String = "",
    var email: String = "",
    var password: String = ""
) : Parcelable
class ImageConverter {

    @TypeConverter
    fun fromString(stringListString: String): List<String> {
        return stringListString.split(",").map { it }
    }

    @TypeConverter
    fun toString(stringList: List<String>): String {
        return stringList.joinToString(separator = ",")
    }
}

//class ImageConverter {
//    companion object {
//
//        @TypeConverter
//        fun fromString(value: String?): MutableList<String?>? {
//            val listType: Type = object : TypeToken<MutableList<String?>?>() {}.type
//            return Gson().fromJson(value, listType)
//        }
//
//        @TypeConverter
//        fun fromArrayList(list: MutableList<String?>?): String? {
//            val gson = Gson()
//            return gson.toJson(list)
//        }
//    }
//}
//We would like to include the location into our model, so we can record the latitude/longitude the user selects
//We are still keeping Location model for use with the MapsActivity
