package com.codingraz.bootcamp.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.codingraz.bootcamp.databinding.RowConversationBinding
import com.codingraz.bootcamp.model.ConversationModel

class ConversationAdapter(private val mList: List<ConversationModel>, private var myUid: String) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    var onItemClick: ((ConversationModel) -> Unit)? = null

    fun setItemClick(action: (ConversationModel) -> Unit){
        onItemClick = action
    }


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.row_user, parent, false)

        val binding = RowConversationBinding.inflate((LayoutInflater.from(parent.context)), parent, false)

        return ConversationViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {

        val item = mList[position]

        holder.binding.tvUserMessage.text = item.message
        holder.binding.tvDateTime.text = item.temeStampLocal

//        (!item.imageMessage.isNullOrEmpty())
        if (item.imageMessage.isNotEmpty()) {
            holder.binding.ivMessageImage.visibility = View.VISIBLE
            holder.binding.ivMessageImage.load(item.imageMessage) {
                crossfade(true)
//                placeholder(R.drawable.ic_user)
//                error(R.drawable.ic_user)
            }
        } else {
            holder.binding.ivMessageImage.visibility = View.VISIBLE
        }

        if (myUid == item.userId){ // Me

            holder.binding.ivUser.visibility = View.GONE
            holder.binding.ivUserMe.visibility = View.VISIBLE

            holder.binding.tvUserMessage.gravity = Gravity.END
            holder.binding.tvDateTime.gravity = Gravity.END

            holder.binding.ivMessageImage.scaleType = ImageView.ScaleType.FIT_END

            holder.binding.ivUserMe.load(item.userImage) {
                crossfade(true)
//                placeholder(R.drawable.ic_user)
//                error(R.drawable.ic_user)
            }

        } else { // Other user

            holder.binding.ivUser.visibility = View.VISIBLE
            holder.binding.ivUserMe.visibility = View.GONE

            holder.binding.tvUserMessage.gravity = Gravity.START
            holder.binding.tvDateTime.gravity = Gravity.START

            holder.binding.ivMessageImage.scaleType = ImageView.ScaleType.FIT_START

            holder.binding.ivUser.load(item.userImage) {
                crossfade(true)
//                placeholder(R.drawable.ic_user)
//                error(R.drawable.ic_user)
            }

        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    inner class ConversationViewHolder(var binding: RowConversationBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            onItemClick?.let {
                binding.root.setOnClickListener {
                    it(mList[adapterPosition])
            }

            }
        }
    }
}