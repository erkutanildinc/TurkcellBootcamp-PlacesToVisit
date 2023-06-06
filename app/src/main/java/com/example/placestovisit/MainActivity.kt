package com.example.placestovisit

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.placestovisit.adapter.PlaceAdapter
import com.example.placestovisit.models.PlaceNote
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth
    lateinit var db : FirebaseFirestore
    lateinit var addNoteBtn : Button
    lateinit var logOutBtn : ImageView
    lateinit var placeListView : ListView
    lateinit var placeAdapter : PlaceAdapter
    var list = mutableListOf<PlaceNote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addNoteBtn = findViewById(R.id.addNoteBtn)
        logOutBtn = findViewById(R.id.logOutBtn)
        placeListView = findViewById(R.id.noteListView)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        getData()

        placeAdapter = PlaceAdapter(this,list)
        logOutBtn.setOnClickListener(logOutClick)
        placeListView.adapter = placeAdapter

        placeListView.setOnItemLongClickListener { adapterView, view, i, l ->
            var place = list.get(i)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Note")
            builder.setMessage("Are you sure you want to delete this place?")
            builder.setCancelable(true)

            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                db.collection(mAuth.currentUser!!.uid).document(place.documentId).delete().addOnSuccessListener {
                    Toast.makeText(this,"Note Deleted",Toast.LENGTH_LONG).show()
                }
            })
            builder.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
            })
            builder.show()
            false
        }

        val user = mAuth.currentUser
        if(user == null){
            var intent = Intent(applicationContext,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        addNoteBtn.setOnClickListener {
            val intent = Intent(this,PlaceAddActivity::class.java)
            startActivity(intent)
        }
    }

    var logOutClick = View.OnClickListener {
        mAuth.signOut()
        Toast.makeText(this,"Logged out",Toast.LENGTH_LONG).show()
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)

    }

    private fun getData(){
        db.collection(mAuth.currentUser!!.uid).addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(value!=null){
                    if(!value.isEmpty){
                        val documents = value.documents
                        list.clear()
                        for(document in documents){
                            val docId = document.id
                            var title = document.get("Title") as String
                            var city = document.get("City") as String
                            var note = document.get("Note") as String
                            var url = document.get("downloadURL") as String
                            var place = PlaceNote(docId,title,city,note,url)
                            list.add(place)
                        }
                        placeAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}