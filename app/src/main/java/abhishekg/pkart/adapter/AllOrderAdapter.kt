package abhishekg.pkart.adapter

import abhishekg.pkart.databinding.LayoutAllOrderItemBinding

import abhishekg.pkart.model.AllOrderModel
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllOrderAdapter(var list: ArrayList<AllOrderModel>, val context: Context)
    : RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>(){
    inner class AllOrderViewHolder(val binding: LayoutAllOrderItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        return AllOrderViewHolder(
            LayoutAllOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent ,false)
        )
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {

    //////////////text fiend setting code////////////////////////////////////////////////
        holder.binding.orderId.text =("Order No.  ${list[position].orderId}")
        holder.binding.dateTime.text =("Ordered on:\n${list[position].dateTime}")
        holder.binding.addressTV.text =("Address: ${list[position].address}")
        holder.binding.netPriceTV.text =("Total Amount:  ₹ ${list[position].totalMoney} ${list[position].paymentMode}")
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////expanding table program/////////////////////////////////////////////////////////////
        if (list[position].expand){
            holder.binding.llProductDetails.visibility = VISIBLE
            holder.binding.showOrderDetailsTv.text="👆 Hide Details 👆"
        } else {
            holder.binding.llProductDetails.visibility = GONE
            holder.binding.showOrderDetailsTv.text="\uD83D\uDC47 Show Order Details \uD83D\uDC47"
        }
        holder.binding.cardView.setOnClickListener{
            list[position].expand = !list[position].expand
            notifyDataSetChanged()
        }
    //////////////////////////////////////////////////////////////////////////////////

    //////////////table setting////////////////
        holder.binding.name.text=""
        holder.binding.quan.text=""
        holder.binding.price.text=""
        for (data in list[position].pNameList){
            holder.binding.name.append("$data\n")
        }
        for (data in list[position].pQuanList){
            holder.binding.quan.append("$data\n")
        }
        for (data in list[position].pPriceList){
            holder.binding.price.append("$data\n")
        }
    /////////////////////////////////////////////////


    ///////////status button setting///////////
        when(list[position].status){
            "Ordered" -> {
                holder.binding.statusButton.text="Order Recieved"
            }
            "Dispatched" -> {
                holder.binding.statusButton.text="On The Way"
            }
            "Delivered" ->{
                holder.binding.statusButton.text="Order Delivered 🥳"
            }
            "Canceled"->{
                holder.binding.statusButton.text="Canceled"
            }
        }
     /////////////////////////////////////////////////////////
    }


    override fun getItemCount(): Int {
        return list.size
    }
}