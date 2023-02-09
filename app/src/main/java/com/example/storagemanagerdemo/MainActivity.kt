package com.example.storagemanagerdemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.example.storagemanagerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    // dient der Anfrage an eine andere Activity/App und wertet das Ergebnis aus
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisierung des aktivityResultLauncher und Resultverarbeitung
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK && result.data != null){
                val dir = DocumentFile.fromTreeUri(this,result.data!!.data!!)
                for(file in dir?.listFiles()?: emptyArray()){
                    binding.tvOutput.append("\n --> ${file.name}")
                }
            }
        }

            val manager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
            manager.let { storageManager ->
                for(volume in storageManager.storageVolumes){
                    with(binding.tvOutput){
                        append("\n ${volume.getDescription(this@MainActivity)}")
                        append("\n state: ${volume.state}")
                        append("\n isPrimary: ${volume.isPrimary}")
                        append("\n isRemovable: ${volume.isRemovable}")
                        append("\n isEmulated: ${volume.isEmulated}")
                    }
                    if(volume.isPrimary){
                        val intent =
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                volume.createOpenDocumentTreeIntent()
                            }else{
                                volume.createAccessIntent(Environment.DIRECTORY_DOCUMENTS)
                            }
                        activityResultLauncher.launch(intent)
                    }
                }
            }





        // ältere Variante
        //startActivityForResult(intent,123)

    }

    // gehört auch zur älteren Variante
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//    }
}