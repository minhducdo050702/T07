package com.example.openssldemo

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.openssldemo.database.data.AppDatabase
import com.example.openssldemo.databinding.ActivityMainBinding
import com.example.openssldemo.viewmodel.AppViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel : AppViewModel by viewModels {

        AppViewModel.AppViewModelFactory(AppDatabase.getDatabase(this).dataDao(),
            AppDatabase.getDatabase(this).appDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dir = ContextCompat.getExternalFilesDirs(this, null)
        val dir2 = fileList()
        for (i in dir.indices) {
            Log.d("A", dir[i].absolutePath)
        }

        for(i in dir2.indices) {
            Log.d("A", dir2[i])
        }

        viewModel.createNew("3", "password", "test value")





//        val keyFile = File(dir[0].absolutePath + "/abz.jks")
//        if(!keyFile.exists()) {
//            Log.d("A", "File not found")
//            keyFile.createNewFile()
//            val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
//            keystore.load(null, null)
////            val secretKey = SecretKeySpec("12345678".toByteArray(), "AES");
////            val k = KeyStore.SecretKeyEntry(secretKey)
////            keystore.setKeyEntry("key", k , null)
//       }
//        val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
//        keystore.load( FileInputStream(dir[0].absolutePath + "/keystore.jks")  ,"".toCharArray())


//        Log.d("A", dir)
//        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Log.d("A", "Permission granted")
//        } else {
//            Log.d("A", "Permission denied")
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//
//
//        }
//        val keystoreController = KeystoreController()
//
//        val isRegistered = keystoreController.isRegistered("com.example.clientapp")
//        Log.d("MainActivity", isRegistered.toString())

        //binding.sampleText.text = "Data Secure Vault"

       // val dir = getExternalFilesDir()
//        val register= Register()
//        register.registerApp("asda")
//        val encryptDecrypt = EncryptDecrypt("asda","2390172847198247190")
//        val ciphertext=encryptDecrypt.encrypt("Hello world !!!")
//        encryptDecrypt.decrypt(ciphertext)
//        val hMac = HMac("asda")
//        val mac = hMac.genHmac("Hello world !!!!")
//        val verify = hMac.verifyHmac("Hello world !!!",mac)
//        Log.d("HMAC",verify.toString())
//
//






    }

    /**
     * A native method that is implemented by the 'openssldemo' native library,
     * which is packaged with this application.
     */



}


