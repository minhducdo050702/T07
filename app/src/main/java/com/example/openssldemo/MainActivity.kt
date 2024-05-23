package com.example.openssldemo

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.openssldemo.database.data.AppDatabase

import com.example.openssldemo.viewmodel.AppViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val dir = ContextCompat.getExternalFilesDirs(this, null)
        val dir2 = fileList()
        for (i in dir.indices) {
            Log.d("A1", dir[i].absolutePath)
        }

        for(i in dir2.indices) {
            Log.d("A2", dir2[i])
        }

        val keystoreController = KeystoreController(this)
        val register = Register(this)
        val masterKey = keystoreController.masterKey
        if(masterKey == null) {
            Log.d("MainActivity", "Master key not found")
            register.genMasterKeyOnce()
        }
        val id = "com.example.test"
        register.registerApp(id)

        val isRegistered = keystoreController.isRegistered(id)

        Log.d("MainActivity", isRegistered.toString())

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
//        GlobalScope.launch {
//            val newID = viewModel.insert(id + 1,id,id)
//            Log.d("MainActivity", newID.toString())
//
//        }




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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * A native method that is implemented by the 'openssldemo' native library,
     * which is packaged with this application.
     */



}


