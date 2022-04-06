package com.example.facebook.adapter

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.facebook.Post
import com.example.facebook.R

class PostViewHolder(view:View) :RecyclerView.ViewHolder(view){

    val username=view.findViewById<TextView>(R.id.lbItemUserName)
    val location=view.findViewById<TextView>(R.id.lbItemLocation)
    val description=view.findViewById<TextView>(R.id.lbItemDescription)
    val date=view.findViewById<TextView>(R.id.lbItemDate)
    val photo=view.findViewById<ImageView>(R.id.ivItemPhoto)
    val phPro=view.findViewById<ImageView>(R.id.profile_image4)

    fun render(postModel:Post){
        username.text=postModel.username
        location.text=postModel.location
        description.text=postModel.description
        date.text=postModel.date
        photo.setImageURI(Uri.parse(postModel.photo))
        phPro.setImageURI(Uri.parse(postModel.phPro))
    }

}