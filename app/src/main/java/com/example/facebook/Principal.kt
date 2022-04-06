package com.example.facebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Principal : AppCompatActivity() {

    private lateinit var navigator: BottomNavigationView
    private lateinit var profileFragmentFragment : ProfileFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var postFragment: PostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        navigator = findViewById(R.id.navigator)
        profileFragmentFragment = ProfileFragment.newInstance()
        homeFragment = HomeFragment.newInstance()
        postFragment = PostFragment.newInstance()

        showFragment(homeFragment)
        navigator.setOnItemSelectedListener { menuItem->
            if(menuItem.itemId == R.id.post){
                showFragment(postFragment)
            }else if(menuItem.itemId == R.id.home){
                showFragment(homeFragment)
            }else{
                showFragment(profileFragmentFragment)
            }
            true
        }
    }

    fun showFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}