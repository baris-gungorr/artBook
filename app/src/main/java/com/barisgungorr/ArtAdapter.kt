package com.barisgungorr

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barisgungorr.artbook.databinding.ActivityDetailsBinding
import com.barisgungorr.artbook.databinding.RecyclerRowBinding

class ArtAdapter(val artList : ArrayList<Art>): RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

class ArtHolder(val binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root) {



}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
       val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(binding)
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = artList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,DetailsActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

}