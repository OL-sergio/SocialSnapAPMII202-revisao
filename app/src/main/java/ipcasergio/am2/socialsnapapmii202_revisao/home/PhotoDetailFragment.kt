package ipcasergio.am2.socialsnapapmii202_revisao.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ipcasergio.am2.socialsnapapmii202_revisao.R
import ipcasergio.am2.socialsnapapmii202_revisao.models.SnapItem
import kotlinx.android.synthetic.main.activity_photo_detail.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap



class PhotoDetailFragment : Fragment() {

    private var bitmap : Bitmap? = null
    private var date : Date = Date()

    val args: PhotoDetailFragment by navArgs()
    var snapitemId : String? = null
    var snapItem : SnapItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_photo_detail, container, false)

    }


    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            snapitemId = args.snapitemId

            snapitemId?.let{
                val db = FirebaseFirestore.getInstance()
                    db.collection("snaps").document(it)
                        .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                            if(querySnapshot != null){
                                snapItem = SnapItem.formHash(querySnapshot.data as HashMap<String, Any?>)
                                snapItem?.itemId = querySnapshot.id
                                textViewDescription.setText(snapItem?.description)

                                val storageRef = Firebase.storage.reference
                                val imagesRef = storageRef.child("images/${snapItem?.filePath}")

                                val ONE_MEGABYTE : Long = 1024 * 1024
                                imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {

                                    val bais = ByteArrayInputStream(it)
                                    this.imageViewPhoto.setImageBitmap(BitmapFactory.decodeStream(bais))

                                }.addOnFailureListener {


                                }
                                fabTakePhoto.visibility = View.GONE

                                val userId = FirebaseAuth.getInstance().currentUser?.uid
                                if (userId.equals(snapItem?.userId)){
                                    buttonPublish.text = "Update"
                                }else{
                                    buttonPublish.visibility = View.GONE
                                }
                            }
                        }
            }


        fabTakePhoto.setOnClickListener {
            val intent = Intent (MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,targetRequestCode)

        }

        buttonPublish.setOnClickListener {

            snapItem?.let {
                var auth = Firebase.auth
                val currentUser = auth.currentUser

                val db = FirebaseFirestore.getInstance()
                it.description = textViewDescription.text.toString()

                db.collection( "snaps")
                    .document(it.itemId!!)
                    .set(it.toHashMap())
                    .addOnSuccessListener {
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Algo correu mal,", Toast.LENGTH_SHORT)
                            .show()
                    }



            }?:run{

                val storaRef = Firebase.storage.reference
                val imagesRef = storaRef.child("images/${UUID.randomUUID()}.jpg")

                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = imagesRef.putBytes(data)
                uploadTask.addOnFailureListener {

                }.addOnSuccessListener { taskSnapshot ->

                    var auth = Firebase.auth
                    val currentUser = auth.currentUser
                    val db = FirebaseFirestore.getInstance()
                    val snap = SnapItem ( imagesRef.name,textViewDescription.text.toString()
                        ,date, currentUser!!.uid
                    )
                    db.collection("")
                        .add(snap.toHashMap())
                        .addOnSuccessListener {
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(),"Algo correu mal",Toast.LENGTH_LONG)
                                .show()

                        }
                }

            }

        }

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === Activity.RESULT_OK){
            if (requestCode == REQUEST_CODE_PHOTO){

                data?.extras?.let {
                    bitmap = it.get("data") as Bitmap
                    imageViewPhoto.setImageBitmap(bitmap)

                }
            }
        }
    }

    companion object{

        const val  REQUEST_CODE_PHOTO  = 23524


    }

}
