package com.example.facebook

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class PostFragment : Fragment() {

    private lateinit var lbUserName: TextView
    private lateinit var ivPhoto: ImageView
    private lateinit var etDescrption: EditText
    private lateinit var btUpload: Button
    private lateinit var btPost: Button
    private lateinit var spinner: Spinner
    private lateinit var profileImage: CircleImageView

    lateinit var preferences: SharedPreferences
    lateinit var listPreferences: SharedPreferences
    lateinit var alfaPreferences: SharedPreferences
    lateinit var betaPreferences: SharedPreferences

    private var uriPath : String = ""
    private var username: String = "Username"
    private var phPath : String = ""
    private lateinit var location: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        lbUserName = view.findViewById(R.id.lbUserNamePost)
        ivPhoto = view.findViewById(R.id.ivPhotoPost)
        btUpload = view.findViewById(R.id.btTakePhoto)
        btPost = view.findViewById(R.id.btPost)
        etDescrption = view.findViewById(R.id.txtPost)
        spinner = view.findViewById(R.id.spinnerLocation)
        profileImage = view.findViewById(R.id.profile_image2)
        val options = resources.getStringArray(R.array.location_options)

        val adapter = ArrayAdapter(requireActivity().applicationContext,android.R.layout.simple_spinner_item,options)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                location = options[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        requestPermission()
        ivPhoto.setOnClickListener{pickPhoto()}
        btUpload.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            launcher.launch(intent)}
        btPost.setOnClickListener{uploadPost()}

        preferences = requireActivity().applicationContext.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        listPreferences = requireActivity().applicationContext.getSharedPreferences("LIST_PREF", Context.MODE_PRIVATE)
        alfaPreferences = requireActivity().applicationContext.getSharedPreferences("INFO_ALFA", Context.MODE_PRIVATE)
        betaPreferences = requireActivity().applicationContext.getSharedPreferences("INFO_BETA", Context.MODE_PRIVATE)

        val user = preferences.getString("EMAIL","")
        val nAlfa = alfaPreferences.getString("NAME","")
        val nBeta = betaPreferences.getString("NAME","")

        when(user){
            "alfa@gmail.com" -> {if(nAlfa.isNullOrEmpty()){username = "Username"}else username = nAlfa!!}
            "beta@gmail.com" -> {if(nAlfa.isNullOrEmpty()){username = "Username"}else username = nBeta!!}
            else -> {
                username = "Username"
            }
        }

        val phAl = alfaPreferences.getString("Profile","")
        val phBe = betaPreferences.getString("Profile","")

        when(user){
            "alfa@gmail.com" ->
                if (phAl!=""){
                    profileImage.setImageURI(Uri.parse(phAl))
                    phPath= phAl!!
                }
            "beta@gmail.com" ->
                if(phBe!=""){
                    profileImage.setImageURI(Uri.parse(phBe))
                    phPath= phBe!!
                }
        }

        lbUserName.text = username

        val editor : SharedPreferences.Editor = listPreferences.edit()
        val gson = Gson()
        val json : String = gson.toJson(postsList)
        editor.putString("LIST",json)
        editor.apply()

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
        ActivityResultContracts.RequestMultiplePermissions()
    ){isGranted->
        if(!isGranted.containsValue(false)){

        }else{
            Toast.makeText(requireActivity().applicationContext,"NecesitaPermisos",Toast.LENGTH_SHORT).show()
        }

    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )==PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity().applicationContext,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(requireActivity().applicationContext,
                    android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED->{
                }
                else->{
                    val PERMISSIONS = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                    requestPermissionLauncher.launch(PERMISSIONS)
                    //requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onResult)


    fun onResult(result : ActivityResult){
        val uri = result.data?.extras?.get("data") as Bitmap
        ivPhoto.setBackgroundColor(Color.parseColor("#000000"))
        ivPhoto.setPadding(0)
        ivPhoto.setImageBitmap(uri)
            //ivPhoto.setImageURI(uri)
            //uriPath = uri.toString()
    }

    private fun requestPermissionForCamera() {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    android.Manifest.permission.CAMERA
                )==PackageManager.PERMISSION_GRANTED->{
                    takePhoto()
                }
                else->{

                }
            }
        }else{
            takePhoto()
        }
    }

    private val startForActivityGallery=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result->

        if(result.resultCode==Activity.RESULT_OK){
            val uri = result.data?.data!!
            requireActivity().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            ivPhoto.setBackgroundColor(Color.parseColor("#000000"))
            ivPhoto.setPadding(0)
            ivPhoto.setImageURI(uri)
            uriPath = uri.toString()
        }
    }

    private fun takePhoto(){

    }

    private fun uploadPost(){
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        val currentDate = sdf.format(Date())
        postsList.add(Post(username,etDescrption.text.toString(),location,currentDate,uriPath,phPath))
        val editor : SharedPreferences.Editor = listPreferences.edit()
        val gson = Gson()
        val json : String = gson.toJson(postsList)
        editor.putString("LIST",json)
        editor.apply()
        ivPhoto.setBackgroundColor(Color.parseColor("#C5C7C9"))
        ivPhoto.setImageResource(R.drawable.addph)
        ivPhoto.setPadding(120)
        etDescrption.setText("")
        spinner.setSelection(0)
        Toast.makeText(requireActivity().applicationContext, "Published!", Toast.LENGTH_LONG).show();
    }

    companion object {

        @JvmStatic
        fun newInstance() = PostFragment()

        var postsList = arrayListOf<Post>()

    }

}













