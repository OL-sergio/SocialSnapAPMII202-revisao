package ipcasergio.am2.socialsnapapmii202_revisao

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.nfc.Tag
import android.os.Build
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ipcasergio.am2.socialsnapapmii202_revisao.login.LoginActivity.Companion.TAG
import java.time.Instant

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage : RemoteMessage) {

        Log.d(TAG, "from:${remoteMessage.from}" )

        if (remoteMessage.data.isNotEmpty()){
            Log.d(TAG, "Message data payload:${remoteMessage.data}")

            sendNotification(remoteMessage.data.toString())


        }

        remoteMessage.notification?.let {
            Log.d(TAG,"Message Notification Body ${it.body}")
            sendNotification(it.body?:"")

        }
    }

    override fun onNewToken(token: String) {
        Log.d (TAG, "Refrehed token $token")

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String){

        var sharedPreferences : SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
            sharedPreferences.edit().putString("firegbase_token", token).apply()

        var auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        val hashMap = HashMap<String, Any?>()
            hashMap["token"] = token
            hashMap["email"] = currentUser?.email

            currentUser?. let {
                db.collection("users")
                    .document(currentUser?.uid?:"")
                    .set(hashMap)
                    .addOnFailureListener {

                    }
                    var sharedPreferences : SharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(this)
                    sharedPreferences.edit().putString("firebase_token",token).apply()

            }


    }

    @SuppressLint("NewApi")
    private fun sendNotification(messageBody: String){

        val intent = Intent (this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0/*Request*/, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_camera_alt_24)
            .setContentTitle("Social Snap")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                             NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /*ID of notification*/ ,notificationBuilder.build())

    }


    companion object{

        private const val TAG ="MyFirebaseService"
    }
}
