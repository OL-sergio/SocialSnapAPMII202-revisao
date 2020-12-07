package ipcasergio.am2.socialsnapapmii202_revisao.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ipcasergio.am2.socialsnapapmii202_revisao.MainActivity
import ipcasergio.am2.socialsnapapmii202_revisao.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    companion object{
        val TAG = "LoginActivity"

    }

    private lateinit var  auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        buttonRegister.setOnClickListener {
            auth.createUserWithEmailAndPassword(
                editTextName.text.toString(),
                editTextPassword.text.toString())
                .addOnCompleteListener (this){task->
                    if (task.isSuccessful){
                        Log.d(TAG, "reateUserWithEmail:success")

                        val user = auth.currentUser
                        val intent = Intent (this ,MainActivity::class.java)

                        startActivity(intent)

                    }else {

                        Log.w(TAG ,"createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }

                }

        }

    }
}