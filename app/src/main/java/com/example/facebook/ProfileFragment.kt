package com.example.facebook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var txtNewName: EditText
    private lateinit var txtNewBio: EditText
    private lateinit var lbName: TextView
    private lateinit var lbBio: TextView
    private lateinit var btEdit: Button
    private lateinit var btLogOut: Button
    private lateinit var btUpload: FloatingActionButton
    private lateinit var profileImage: CircleImageView

    lateinit var preferences: SharedPreferences
    lateinit var listPreferences: SharedPreferences
    lateinit var alfaPreferences: SharedPreferences
    lateinit var betaPreferences: SharedPreferences

    private lateinit var user: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        txtNewName = view.findViewById(R.id.tfNewName)
        txtNewBio = view.findViewById(R.id.tfBio)
        lbName = view.findViewById(R.id.lbUserName)
        lbBio = view.findViewById(R.id.lbBioInfo)
        btEdit = view.findViewById(R.id.btEditProfile)
        btLogOut = view.findViewById(R.id.btLogOut)
        btUpload = view.findViewById(R.id.btUploadProfilePh)
        profileImage = view.findViewById(R.id.profile_image)
        preferences = requireActivity().applicationContext.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        listPreferences = requireActivity().applicationContext.getSharedPreferences("LIST_PREF", Context.MODE_PRIVATE)
        alfaPreferences = requireActivity().applicationContext.getSharedPreferences("INFO_ALFA", Context.MODE_PRIVATE)
        betaPreferences = requireActivity().applicationContext.getSharedPreferences("INFO_BETA", Context.MODE_PRIVATE)

        val nAlfa = alfaPreferences.getString("NAME","")
        val bAlfa = alfaPreferences.getString("BIO","")
        val nBeta = betaPreferences.getString("NAME","")
        val bBeta = betaPreferences.getString("BIO","")
        user = preferences.getString("EMAIL","")!!

        val editorAlfa : SharedPreferences.Editor = alfaPreferences.edit()
        val editorBeta: SharedPreferences.Editor = betaPreferences.edit()

        when(user){
            "alfa@gmail.com" ->
                if (nAlfa==""&& bAlfa==""){
                    lbName.setText("Username")
                    lbBio.setText("Describe yourself...")
                }else{
                    lbName.setText(nAlfa)
                    lbBio.setText(bAlfa)
                }
            "beta@gmail.com" ->
                if(nBeta==""&& bBeta==""){
                    lbName.setText("Username")
                    lbBio.setText("Describe yourself...")
                }else{
                    lbName.setText(nBeta)
                    lbBio.setText(bBeta)
                }
        }

        val phAl = alfaPreferences.getString("Profile","")
        val phBe = betaPreferences.getString("Profile","")

        when(user){
            "alfa@gmail.com" ->
                if (phAl!=""){
                    profileImage.setImageURI(Uri.parse(phAl))
                }
            "beta@gmail.com" ->
                if(phBe!=""){
                    profileImage.setImageURI(Uri.parse(phBe))
                }
        }

        btEdit.setOnClickListener(View.OnClickListener {
            var name =txtNewName.text.toString()
            var bio =txtNewBio.text.toString()
            if(name.isNotEmpty()){
                lbName.setText(name)
                txtNewName.setText("")
                when(user){
                    "alfa@gmail.com" -> editorAlfa.putString("NAME",name)
                    "beta@gmail.com" -> editorBeta.putString("NAME",name)
                }
                editorAlfa.apply()
                editorBeta.apply()
            }
            if(bio.isNotEmpty()){
                lbBio.setText(bio)
                txtNewBio.setText("")
                when(user){
                    "alfa@gmail.com" ->  editorAlfa.putString("BIO",bio)
                    "beta@gmail.com" -> editorBeta.putString("BIO",bio)
                }
                editorAlfa.apply()
                editorBeta.apply()
            }
        })

        btLogOut.setOnClickListener(View.OnClickListener {
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.clear()
            editor.apply()
            val ed2 : SharedPreferences.Editor = listPreferences.edit()
            val gson = Gson()
            val json : String = gson.toJson(PostFragment.postsList)
            ed2.putString("LIST",json)
            ed2.apply()
            startActivity(Intent(activity,MainActivity::class.java))
            requireActivity().finish()
        })

        btUpload.setOnClickListener{requestPermission()}

        return view
    }

    private fun pickPhoto(){
        val intent=Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type="image/*"
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startForActivityGallery.launch(intent)
    }

    private val requestPermissionLauncher=registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if(isGranted){
            pickPhoto()
        }else{
            Toast.makeText(requireActivity().applicationContext,"Need permissions",Toast.LENGTH_SHORT).show()
        }

    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity().applicationContext,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED->{
                    pickPhoto()
                }
                else->{
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }else{
            pickPhoto()
        }
    }

    private val startForActivityGallery=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result->

        if(result.resultCode== Activity.RESULT_OK){
            val uri = result.data?.data!!
            requireActivity().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            profileImage.setImageURI(uri)
            val editorAlfa : SharedPreferences.Editor = alfaPreferences.edit()
            val editorBeta: SharedPreferences.Editor = betaPreferences.edit()
            when(user){
                "alfa@gmail.com" ->  editorAlfa.putString("Profile",uri.toString())
                "beta@gmail.com" -> editorBeta.putString("Profile",uri.toString())
            }
            editorAlfa.apply()
            editorBeta.apply()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    fun editProfile(view: View){

    }
}