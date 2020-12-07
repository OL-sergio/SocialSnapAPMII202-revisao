package ipcasergio.am2.socialsnapapmii202_revisao.login


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import ipcasergio.am2.socialsnapapmii202_revisao.MainActivity
import ipcasergio.am2.socialsnapapmii202_revisao.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    companion object {
        val TAG = "LoginActivity"
        val RC_SIGN_IN = 1001

    }

    private lateinit var auth: FirebaseAuth
    private var mGoogleSignInClient: GoogleSignInClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth


        textViewRegister.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            buttonLogin.setOnClickListener {
                auth.signInWithEmailAndPassword( editTextName.text.toString(), editTextPassword.text.toString())
                    .addOnCompleteListener(this) { task->
                        if (task.isSuccessful){
                            Log.d(TAG, "")
                            val user = auth.currentUser

                            val intent = Intent (this, MainActivity::class.java)
                            startActivity(intent)

                            var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                            val token = sharedPreferences.getString("firebase", "")
                            if((token?:"").isNotEmpty()){

                                var auth = Firebase.auth
                                val currentUser = auth.currentUser
                                val db = FirebaseFirestore.getInstance()

                                val hashMap = HashMap<String, Any?>()

                                hashMap["token"] = token
                                hashMap["email"] = currentUser?.email

                                currentUser?.let {
                                    db.collection("users")
                                        .document(  currentUser?.uid?:"")
                                        .set(hashMap)
                                    .addOnSuccessListener {

                                    } .addOnFailureListener {


                                    }
                                }
                            }
                            }else{

                            Log.w(TAG,"",task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()


                        }

                    }


            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        buttonLoginGoolge.setOnClickListener {

            signIn()
        }


}

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)


            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }

        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

            } else {

            Log.w(TAG, "signInWithCredential:failure", task.exception)

                    Snackbar.make(buttonLoginGoolge, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()

            }


        }

    }

}