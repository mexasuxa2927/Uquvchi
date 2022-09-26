package com.example.a74

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ebanx.swipebtn.OnActiveListener
import com.example.a74.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.swipeBtn.setOnActiveListener(OnActiveListener {
//            Toast.makeText(
//                this@MainActivity,
//                "Active!",
//                Toast.LENGTH_SHORT
//            ).show()
//        })
    }

}