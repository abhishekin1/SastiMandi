package abhishekg.pkart.adapter

import abhishekg.pkart.activity.ProductDetailsActivity
import abhishekg.pkart.databinding.ItemCatagoryProductLayoutBinding
import abhishekg.pkart.databinding.LayoutProductItemBinding
import abhishekg.pkart.model.AddProductModel
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CategoryProductAdapter (val context: Context, val list: ArrayList<AddProductModel> )
    : RecyclerView.Adapter<CategoryProductAdapter.CategoryProductViewHolder>(){

    inner class CategoryProductViewHolder(val binding: ItemCatagoryProductLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder {
        Log.i("hm",list.size.toString())
        val binding = ItemCatagoryProductLayoutBinding.inflate(LayoutInflater.from(context), parent,false)
        return CategoryProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        Glide.with(context).load(list[position].productCoverImg).into(holder.binding.imageView3)
        holder.binding.textView5.text = list[position].productName
        holder.binding.textView6.text = list[position].productSp

        holder.itemView.setOnClickListener{
            val intent= Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}