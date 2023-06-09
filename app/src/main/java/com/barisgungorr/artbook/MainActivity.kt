package com.barisgungorr.artbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.barisgungorr.Art
import com.barisgungorr.ArtAdapter
import com.barisgungorr.DetailsActivity
import com.barisgungorr.artbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var artlist : ArrayList<Art> // bilgileri bir arrList'e koyuyoruz sonra recyclerView'da göstereceğiz
    private lateinit var artAdapter: ArtAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        artlist = ArrayList<Art>() // init ediyoruz
        artAdapter = ArtAdapter(artlist)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = artAdapter

        try {
            val database = this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)  // veri çekeceğimiz kısım
            val cursor = database.rawQuery("SELECT * FROM arts",null)
            val artNameIx = cursor.getColumnIndex("artname")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val name = cursor.getString(artNameIx) // name'i string olarak aldık
                val id = cursor.getInt(idIx) // id ınt olarak aldık
                val art = Art(name,id)  // bir model oluşturduk
                artlist.add(art)

            }
            artAdapter.notifyDataSetChanged()

            cursor.close()

        }catch (e:Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //Bağlama işlemi yapılacak
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.art_menu,menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {  // Tıklandığında ne olacağını yapacağız

        if (item.itemId == R.id.add_art_item) {
            val intent = Intent(this,DetailsActivity::class.java)
            intent.putExtra("info","new")

            startActivity(intent)

        }

        return super.onOptionsItemSelected(item)
    }


}