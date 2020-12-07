package ipcasergio.am2.socialsnapapmii202_revisao.models

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import ipcasergio.am2.socialsnapapmii202_revisao.helpers.dateToString
import ipcasergio.am2.socialsnapapmii202_revisao.helpers.stringToDate
import java.util.*
import kotlin.collections.HashMap

class SnapItem {

    var itemId      :String? = null
    var filePath    :String? = null
    var description :String? = null
    var date        :Date?   = null
    var userId      :String? = null

    constructor(
        filePath    : String?,
        description : String?,
        date        : Date?,
        userId      : String?

    ){
        this.filePath = filePath
        this.description = description
        this.date = date
        this.userId = userId
    }


    @SuppressLint("NewApi")
    fun toHashMap() : HashMap<String,Any?>{
        val hashMap = HashMap<String,Any?>()
        hashMap["filepath"] = filePath
        hashMap["description"] = description
       hashMap["date"] = date?.let { dateToString(it)  }
        hashMap["userId"] = userId
        return  hashMap
    }

    companion object{

        @SuppressLint("NewApi")
        fun formHash(hashMap:  HashMap<String, Any?>) : SnapItem{
            val item = SnapItem(
                hashMap["filePath"].toString(),
                hashMap["description"].toString(),
                stringToDate(hashMap["date"].toString()),
                hashMap["userId"].toString()
            )
            return item
        }

    }
}