package com.example.facebook

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.facebook.adapter.PostAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class HomeFragment : Fragment() {

    private lateinit var listPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        loadData()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerPost)
        recyclerView.layoutManager=LinearLayoutManager(requireActivity().applicationContext)
        recyclerView.adapter=PostAdapter(PostFragment.postsList)
        return view
    }

    private fun loadData(){
        listPreferences = requireActivity().applicationContext.getSharedPreferences("LIST_PREF", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = listPreferences.getString("LIST", null)
        val arrayTutorialType = object : TypeToken<ArrayList<Post>>() {}.type
        PostFragment.postsList = gson.fromJson(json, arrayTutorialType)
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}