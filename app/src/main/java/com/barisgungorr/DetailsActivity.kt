package com.barisgungorr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.barisgungorr.artbook.MainActivity
import com.barisgungorr.artbook.R
import com.barisgungorr.artbook.databinding.ActivityDetailsBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.IOException

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent> // galeriye gitmek için kullanacağımız değişken
    private lateinit var permissionLauncher: ActivityResultLauncher<String>   //
    private var selectedBitmap: Bitmap? = null
    private lateinit var database : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
        registerLauncher()
        val intent = intent
        val info = intent.getStringExtra("info")
        if (info.equals("new")) {
            binding.artNameText.setText("")
            binding.artistNameText.setText("")
            binding.yearText.setText("")

            binding.button.visibility = View.VISIBLE  // burada yeni bir veri

         //  binding.imageView.setImageResource(R.drawable.selectt)
            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.selectimage)
            binding.imageView.setImageBitmap(selectedImageBackground)

        }else {
            binding.button.visibility = View.INVISIBLE // kaydet butonu gözükmez eski bir veri olduğu için
            val selectedId = intent.getIntExtra("id",1)

            val cursor = database.rawQuery("SELECT * FROM arts WHERE id = ?" , arrayOf(selectedId.toString()))
          //  database.execSQL("DELETE FROM arts WHERE name = 4 ")

            val artNameIx = cursor.getColumnIndex("artname")
            val artistNameIx = cursor.getColumnIndex("artistname")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx =  cursor.getColumnIndex("image")


            while (cursor.moveToNext()) {
                binding.artNameText.setText(cursor.getString(artNameIx))
                binding.artistNameText.setText(cursor.getString(artistNameIx))
                binding.yearText.setText(cursor.getString(yearIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }
            cursor.close()

        }
    }

    fun save(view: View) {
        val artName = binding.artNameText.text.toString()
        val artistName = binding.artistNameText.text.toString()
        val year = binding.yearText.text.toString()

        if (selectedBitmap != null) {
            val smallBitMap = makeSmallerBitMap(selectedBitmap!!,300)

            val outPutStream = ByteArrayOutputStream()
            smallBitMap.compress(Bitmap.CompressFormat.PNG,50,outPutStream)   //görseli byteDizisine çevirmek
            val byteArray = outPutStream.toByteArray()

            try {
          //     val database = this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artname VARCHAR, artistname VARCHAR, year VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO arts (artname, artistname, year, image) VALUES (?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, artName)
                statement.bindString(2, artistName)
                statement.bindString(3, year)
                statement.bindBlob(4, byteArray)

                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // açık olan aktivite varsa kapat diyoruz

            startActivity(intent)

        }
    }

    fun selectImage(view: View) {  // görsele tıklandığında izin var mı yok mu onu kontrol ettiğimiz kısım
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                     //   if (imageData !=null) {
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(this@DetailsActivity.contentResolver, imageData!!)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(this@DetailsActivity.contentResolver, imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied
                    Toast.makeText(this@DetailsActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun makeSmallerBitMap(image: Bitmap,maximumSize: Int): Bitmap {    //Bitmap küçültme kodları
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            //landScape
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        }else{
            //portrait
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }
}
/*
1->makeSmallerBitMap adında bir özel fonksiyon tanımlanıyor. Bu fonksiyon, bir Bitmap nesnesi alacak ve küçültülmüş bir Bitmap döndürecek.
2->image parametresi, işlem yapılacak orijinal Bitmap nesnesini temsil eder.
3->maximumSize parametresi, hedeflenen maksimum boyutu belirtir. Küçültülen Bitmap, bu boyut sınırlamalarına uymak için yeniden boyutlandırılacaktır.
4->width ve height değişkenleri, orijinal Bitmap'in genişlik ve yükseklik değerlerini tutmak için kullanılır.
5->bitmapRatio değişkeni, orijinal Bitmap'in en-boy oranını hesaplar. Bu oran, Bitmap'in yatay (landscape) veya dikey (portrait) olduğunu belirlemek için kullanılacak.

6->bitmapRatio 1'den büyükse, orijinal Bitmap yatay (landscape) olarak kabul edilir.
Bu durumda, width değeri maximumSize olarak ayarlanır ve scaledHeight değişkeni aracılığıyla buna bağlı olarak height değeri yeniden boyutlandırılır.

7->bitmapRatio 1'den küçükse, orijinal Bitmap dikey (portrait) olarak kabul edilir. Bu durumda, height değeri maximumSize olarak ayarlanır ve
 scaledWidth değişkeni aracılığıyla buna bağlı olarak width değeri yeniden boyutlandırılır.

8>Son olarak, Bitmap.createScaledBitmap işlevi kullanılarak orijinal Bitmap nesnesi belirlenen boyutlara yeniden boyutlandırılır ve küçültülen Bitmap döndürülür.
 */


