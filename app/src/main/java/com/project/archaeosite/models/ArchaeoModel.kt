package com.project.archaeosite.models


import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


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
    @Embedded var location : Location = Location(),
    var additionalNote: String="",
    var visited: Boolean=false,
    @Embedded var date : Date=Date()
): Parcelable


@Parcelize
data class Location(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f
) : Parcelable

@Parcelize
data class Date(
        var day: Int = 0,
        var month: Int = 0,
        var year: Int = 0
) : Parcelable


@Parcelize
data class HillfortModel(
        var itemId : String = "",
        var Title:String="",
        var Image:String="",
        var Location:@RawValue GeoPoint?=null,

//    var visited: MutableList<String> = arrayListOf(),
//    var favourite: MutableList<String> = arrayListOf(),
        var userReaction: @RawValue MutableList<UserReaction> = arrayListOf()
): Parcelable

data class UserReaction(
        var userID:String,
        var visited: Boolean,
        var favourite:Boolean,
        var rating: Float?

){
    constructor() : this("",false,false,null)
    constructor(userID: String) : this(){
        this.userID=userID
    }
}
@Parcelize
data class ArchaeoUser(
        var generatedId : String = "",
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

