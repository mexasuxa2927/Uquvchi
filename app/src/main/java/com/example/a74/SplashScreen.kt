package com.example.a74

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            val context = this@SplashScreen
            startActivity(Intent(context, MainActivity::class.java))
            Log.d("AAA", "onCreate: didn't log")
            Animatoo.animateZoom(context) //fire the zoom animation
            finish()

    }, 2000)
}
}