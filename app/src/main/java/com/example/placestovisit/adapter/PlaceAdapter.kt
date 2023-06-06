package com.example.placestovisit.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.placestovisit.R
import com.example.placestovisit.models.PlaceNote
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class PlaceAdapter(private val context : Activity, private val list : MutableList<PlaceNote>) : ArrayAdapter<PlaceNote>(context,
    R.layout.place_list_view_item,list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = context.layoutInflater.inflate(R.layout.place_list_view_item,null,true)
        val noteItemTitle = rootView.findViewById<TextView>(R.id.itemTitleTextView)
        val noteItemCity = rootView.findViewById<TextView>(R.id.itemCityTextView)
        val noteItemNote = rootView.findViewById<TextView>(R.id.itemNoteTextView)
        val noteItemImg = rootView.findViewById<ImageView>(R.id.itemPlaceImg)

        var place = list.get(position)

        noteItemTitle.text = place.title
        noteItemCity.text = place.city
        noteItemNote.text = place.note
        Picasso.get().load(place.downloadURL).into(noteItemImg)
        return rootView
    }

}
