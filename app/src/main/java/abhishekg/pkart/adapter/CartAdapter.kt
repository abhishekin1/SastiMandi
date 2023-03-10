package abhishekg.pkart.adapter

import abhishekg.pkart.MainActivity
import abhishekg.pkart.activity.ProductDetailsActivity
import abhishekg.pkart.databinding.LayoutCartItemBinding
import abhishekg.pkart.databinding.LayoutCategoryItemBinding
import abhishekg.pkart.fragment.CartFragment
import abhishekg.pkart.roomdb.AppDatabase
import abhishekg.pkart.roomdb.ProductModel
import android.content.Context
import android.content.Intent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.common.primitives.UnsignedBytes.toInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.g

class CartAdapter(val context: Context, val list: List<ProductModel>):
RecyclerView.Adapter<CartAdapter.CartViewHolder>(){
    inner class CartViewHolder(val binding: LayoutCartItemBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = LayoutCartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        Glide.with(context).load(list[position].productImage).into(holder.binding.imageView4)
        holder.binding.textView11.text= list[position].productName
        holder.binding.textView12.text= "₹"+list[position].productSp
        holder.binding.textViewQuan.text= list[position].productQuan

        holder.itemView.setOnClickListener{
            val intent= Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }


        val dao= AppDatabase.getInstance(context).productDao()
        holder.binding.imageView5.setOnClickListener{
            GlobalScope.launch(Dispatchers.IO){
                dao.deleteProduct(ProductModel(list[position].productId, list[position].productName, list[position].productImage, list[position].productSp, list[position].productQuan))
            }

        }
        holder.binding.buttonAdd.setOnClickListener{
            var q= holder.binding.textViewQuan.text.toString().toInt();
            q++
            holder.binding.textViewQuan.text=q.toString()
            dao.updateQuan(list[position].productId, q.toString())
        }
        holder.binding.buttonSub.setOnClickListener{
            var q= holder.binding.textViewQuan.text.toString().toInt();

            if(q>1){
                q--
                holder.binding.textViewQuan.text=q.toString()
                dao.updateQuan(list[position].productId, q.toString())
            }else{
                Toast.makeText(context, "Select at least 1 product quantity 🤔 ", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}