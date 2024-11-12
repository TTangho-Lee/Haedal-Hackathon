package com.example.parttimecalander

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch{
            preloadMainActivityData()
            goToMainActivity()
        }
    }

    private suspend fun  preloadMainActivityData(){
        withContext(Dispatchers.IO){
            //TODO: pre_load mainActivity data from database
            delay(2000)
        }
    }

    private fun goToMainActivity(){
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}