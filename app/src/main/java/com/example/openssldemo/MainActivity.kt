package com.example.openssldemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.openssldemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sampleText.text = "Data Secure Vault"
        val register= Register()
        register.registerApp("asda")
        val encryptDecrypt = EncryptDecrypt("asda","2390172847198247190")
        val ciphertext=encryptDecrypt.encrypt("Hello world !!!")
        encryptDecrypt.decrypt(ciphertext)
        val hMac = HMac("asda")
        val mac = hMac.genHmac("Hello world !!!")
        val verify = hMac.verifyHmac("Hello world !!!",mac)
        Log.d("HMAC",verify.toString())
//
//






    }

    /**
     * A native method that is implemented by the 'openssldemo' native library,
     * which is packaged with this application.
     */



}