package ipcasergio.am2.socialsnapapmii202_revisao.home

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ipcasergio.am2.socialsnapapmii202_revisao.R
import ipcasergio.am2.socialsnapapmii202_revisao.models.SnapItem
import kotlinx.android.synthetic.main.activity_home_fragment.*
import kotlinx.android.synthetic.main.activity_photo_detail.view.*
import java.io.ByteArrayInputStream

class HomeFragment :Fragment() {

    private  var mAdapter: RecyclerView.Adapter<*>? = null
    private  var mLayoutManager : LinearLayoutManager? = null

    private var snapItems : MutableList<SnapItem> = arrayListOf()

    val storageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_home_fragment, container, false)
    }






    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false )
        recyclerView.layoutManager = mLayoutManager
        mAdapter = PhotosAdapter()
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter

        fabNewPhoto.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToPhotoDetailFragment(null)
            it.findNavController().navigate(action)

        }

        val db = FirebaseFirestore.getInstance()
        db.collection("snaps")
            .orderBy("date")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            snapItems.clear()
            if(querySnapshot != null){
                for(d in querySnapshot) {
                    val snap = SnapItem.formHash(d.data as HashMap<String, Any?>)
                    snap.itemId = d.id
                    snapItems.add(snap)

                        }
                    }
                mAdapter?.notifyDataSetChanged()
            }
    }

        inner class PhotosAdapter : RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

            inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.row_photos, parent, false)
                )
            }


        override fun getItemCount(): Int {
            return snapItems.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.v.apply {
                    this.isClickable = true
                    this.tag = position

                    this.textViewDescription.text = snapItems[position].description
                    val imageRef = storageRef.child("images/${snapItems[position].filePath}")

                    val ONE_MEGABYTE : Long = 1024 * 1024
                     imageRef. getBytes(ONE_MEGABYTE).addOnSuccessListener  {


                        val bais = ByteArrayInputStream(it)
                        this.imageViewPhoto.setImageBitmap(BitmapFactory.decodeStream(bais))

                    }.addOnFailureListener {

                    }

                    this.setOnClickListener {
                        val action = HomeFragmentDirections.actionNavigationHomeToPhotoDetailFragment(snapItems[position].itemId)
                        it.findNavController().navigate(action)

                    }
                }
            }
    }

}


