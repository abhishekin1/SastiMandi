package abhishekg.pkart.adapter

import abhishekg.pkart.activity.ProductDetailsActivity
import abhishekg.pkart.databinding.LayoutProductItemBinding
import abhishekg.pkart.model.AddProductModel
import abhishekg.pkart.roomdb.AppDatabase
import abhishekg.pkart.roomdb.ProductModel
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.GlobalScope


class ProductAdapter(val context: Context, val list: ArrayList<AddProductModel> )
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

    inner class ProductViewHolder(val binding: LayoutProductItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = LayoutProductItemBinding.inflate(LayoutInflater.from(context), parent,false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data= list[position]

        Glide.with(context).load(data.productCoverImg ).into((holder.binding.imageView2))
        holder.binding.textView.text= data.productName
        holder.binding.textView3.text= data.productCategory
        holder.binding.textView4.text= data.productMrp
        holder.binding.button.text= data.productSp
        holder.itemView.setOnClickListener{
            val intent= Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }


        val productDao= AppDatabase.getInstance(context).productDao()

        if(productDao.isExit(list[position].productId.toString()) != null){
                holder.binding.button3.visibility= GONE
                holder.binding.addSubLL.visibility= VISIBLE
                var quan= productDao.getQuan(list[position].productId.toString())
                holder.binding.textViewQuan.text= quan
            }else{
                holder.binding.button3.visibility= VISIBLE
                holder.binding.addSubLL.visibility= GONE
            }

        holder.binding.button3.setOnClickListener{
            if(productDao.isExit(list[position].productId.toString()) == null){
                val data = ProductModel(list[position].productId.toString(), list[position].productName , list[position].productCoverImg, list[position].productSp, "1")
                GlobalScope.launch(Dispatchers.IO) {
                    productDao.insertProduct(data)
                }

            }
            while(productDao.isExit(list[position].productId.toString()) == null){
                notifyDataSetChanged()
            }


        }

        holder.binding.buttonAdd.setOnClickListener{
            if(productDao.isExit(list[position].productId.toString()) != null){
                var q= holder.binding.textViewQuan.text.toString().toInt();
                q++
                productDao.updateQuan(list[position].productId.toString(), q.toString())
                holder.binding.textViewQuan.text=q.toString()

            }

        }
        holder.binding.buttonSub.setOnClickListener{
            if(productDao.isExit(list[position].productId.toString()) != null){
                var q= holder.binding.textViewQuan.text.toString().toInt();
                if(q>1){
                    q--
                    productDao.updateQuan(list[position].productId.toString(), q.toString())
                    holder.binding.textViewQuan.text=q.toString()
                }else{
                    GlobalScope.launch(Dispatchers.IO){
                        productDao.deleteProduct(ProductModel(list[position].productId.toString()))

                    }
                    while(productDao.isExit(list[position].productId.toString()) != null){
                        notifyDataSetChanged()
                    }

                }
            }

        }





    }

    override fun getItemCount(): Int {
        return list.size
    }

}