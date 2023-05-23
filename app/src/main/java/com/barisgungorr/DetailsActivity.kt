package com.barisgungorr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.barisgungorr.artbook.R
import com.barisgungorr.artbook.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun save (view: View) {

    }
    fun saveImage(view: View) {

    }

}