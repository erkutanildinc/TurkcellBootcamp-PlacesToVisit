package com.example.placestovisit

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class PlaceAddActivity : AppCompatActivity() {

    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedPic : Uri? = null
    lateinit var visitingPlaceImage : ImageView
    lateinit var visitingPlaceTitle : EditText
    lateinit var visitingPlaceCity : EditText
    lateinit var visitingPlaceNote : EditText
    lateinit var saveBtn : Button
    lateinit var progressbar : ProgressBar

    private lateinit var mAuth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_add)

        visitingPlaceImage = findViewById(R.id.placeToVisitImage)
        visitingPlaceTitle = findViewById(R.id.placeToVisitTitle)
        visitingPlaceCity = findViewById(R.id.placeToVisitCity)
        visitingPlaceNote = findViewById(R.id.placeToVisitNote)
        saveBtn = findViewById(R.id.saveBtn)
        progressbar = findViewById(R.id.progress)
        mAuth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        storage = Firebase.storage
        registerLauncher()

        saveBtn.setOnClickListener(saveBtnClick)

    }

    var saveBtnClick = View.OnClickListener {
        progressbar.visibility = View.VISIBLE
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)
        imageReference.putFile(selectedPic!!).addOnSuccessListener {
            //download url to storage
            val uploadPictureReference = storage.reference.child("images").child(imageName)
            uploadPictureReference.downloadUrl.addOnSuccessListener {
                val downloadURL = it.toString()
                if(mAuth.currentUser!=null){
                    val postMap = hashMapOf<String,Any>()
                    postMap.put("downloadURL",downloadURL)
                    postMap.put("Title",visitingPlaceTitle.text.toString())
                    postMap.put("City",visitingPlaceCity.text.toString())
                    postMap.put("Note",visitingPlaceNote.text.toString())
                    firestore.collection(mAuth.currentUser!!.uid).add(postMap).addOnSuccessListener {
                        progressbar.visibility = View.GONE
                        Toast.makeText(this,"Note saved",Toast.LENGTH_LONG).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.addOnFailureListener{
            Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }

    fun selectImage(view : View){

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }
            else{
                //request permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{ //izni aldıysak
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult!=null){
                    selectedPic = intentFromResult.data
                    selectedPic?.let {
                        visitingPlaceImage.setImageURI(selectedPic)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                Toast.makeText(this,"Permission needed",Toast.LENGTH_LONG).show()
            }
        }
    }
}