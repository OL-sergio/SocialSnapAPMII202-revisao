package ipcasergio.am2.socialsnapapmii202_revisao.helpers

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*


@SuppressLint("NewApi")
fun dateToString (date: Date) : String {
    val formatter = SimpleDateFormat("dd MM hh:mm", Locale.getDefault())
    return formatter.format(date)

}

@SuppressLint("NewApi")
fun stringToDate (dateStr: String) : Date {
    val formatter = SimpleDateFormat("dd MM yyyy hh:mm", Locale.getDefault())
    val date = formatter.parse(dateStr)

    return date?: Date()


    }
