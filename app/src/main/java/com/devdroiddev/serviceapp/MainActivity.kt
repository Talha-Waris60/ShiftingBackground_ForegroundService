package com.devdroiddev.serviceapp

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.devdroiddev.serviceapp.databinding.ActivityMainBinding
import com.devdroiddev.serviceapp.service.MyService
import com.devdroiddev.serviceapp.service.MyService.Companion.isServiceRunning

class MainActivity : AppCompatActivity() {
    companion object {
        val APP_TAG = "Serice_App"
    }
    private lateinit var binding: ActivityMainBinding
    private var myService: MyService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.MyBinder
            myService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start Service Button
        binding.startService.setOnClickListener {
            if (!isServiceRunning) {
                Log.d(APP_TAG, "$isServiceRunning")
                val startBtnIntent = Intent(this@MainActivity, MyService::class.java)
                startService(startBtnIntent)
                bindService(startBtnIntent, serviceConnection, Context.BIND_AUTO_CREATE)

            }
            Log.d(APP_TAG, "$isServiceRunning")
        }

        // Stop Service Service
        binding.stopService.setOnClickListener {
            if (isServiceRunning) {
                stopService(Intent(this@MainActivity, MyService::class.java))
                unbindService(serviceConnection)
            }
        }

        binding.startActivityBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, BindActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        if (isServiceRunning) {
            val intent = Intent(this@MainActivity, MyService::class.java)
            intent.putExtra("from", "onStartMethod")
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isServiceRunning) {
            val intent = Intent(this@MainActivity, MyService::class.java)
            intent.putExtra("from", "onStopMethod")
            unbindService(serviceConnection)
            startService(intent)
        }
    }
}
