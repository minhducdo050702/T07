package com.example.openssldemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.MainService
import com.example.openssldemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
//        binding.sampleText.text = stringFromJNI()
//        val service = MainService()
//        service.onCreate()
//        service.onStartCommand(null, 0, 0)
//        val register=Register()
//        register.registerApp("asda")
//        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
//        val password = "password"
//
//        //load key store from file in downloads directory
//        keyStore.load(FileInputStream("/sdcard/Download/keystore.jks"), password.toCharArray())
//
//        val masterKeyAlias = "asda"+" MasterKey";
//        val AESKeyAlias = "asda"+" AESKey";
//        val MACKeyAlias = "asda"+" MACKey";
//        val masterKey = keyStore.getKey(masterKeyAlias, null) as Key
//        val AESKey = keyStore.getKey(AESKeyAlias, null) as Key
//        val MACKey = keyStore.getKey(MACKeyAlias, null) as Key
//        Log.d("MainActivity", "Master Key: $masterKey")
//        Log.d("MainActivity", "AES Key: $AESKey")
//        Log.d("MainActivity", "MAC Key: $MACKey")
        Log.d("Main Activity", "hello ");
        val crypto = EncryptDecrypt();
        val key:String = "01234567890123456789012345678901";
        val iv:String = "0123456789012345"
        val plainText:String = "Hello World!!!!"
        Log.d("Main Activity", "Plaintext : $plainText");
        Log.d("Main Activity", "key : $key");
        Log.d("Main Activity", "iv : $iv");
        val cipherMsg:String = crypto.encrypt(key, iv, plainText);
        val decryptedMsg:String = crypto.decrypt(key, iv, cipherMsg);
        Log.d("Main Activity", "Encrypted MSG: $cipherMsg");
        Log.d("Main Activity", "Decrypted MSG: $decryptedMsg");
    }

    /**
     * A native method that is implemented by the 'openssldemo' native library,
     * which is packaged with this application.
     */
        external fun stringFromJNI(): String
//
    companion object {
        // Used to load the 'openssldemo' library on application startup.
        init {
            System.loadLibrary("openssldemo")
        }
    }
}