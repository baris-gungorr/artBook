package com.barisgungorr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.barisgungorr.artbook.R
import com.barisgungorr.artbook.databinding.ActivityDetailsBinding
import com.google.android.material.snackbar.Snackbar

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun save(view: View) {

    }

    fun saveImage(view: View) {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,"Galerine girelim mi ?",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver (!)",View.OnClickListener {

                }).show()
            }

        } else {

            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI) // intent ile bir aksiyon da yaptırabiliyoruz // galeriye git diyoruz

        }


    }
}