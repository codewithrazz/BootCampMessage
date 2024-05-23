package com.codingraz.bootcamp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingraz.bootcamp.ui.HomeFragment
import com.codingraz.bootcamp.utils.fragmentAdd

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentAdd(this, HomeFragment())

    }
}