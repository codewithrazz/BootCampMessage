package com.codingraz.bootcamp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.codingraz.bootcamp.R
import com.codingraz.bootcamp.databinding.RowUserBinding
import com.codingraz.bootcamp.model.UserModel

class UserAdapter(private val mList: List<UserModel>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var onItemClick: ((UserModel) -> Unit)? = null

    fun setItemClick(action: (UserModel) -> Unit){
        onItemClick = action
    }


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.row_user, parent, false)

        val binding = RowUserBinding.inflate((LayoutInflater.from(parent.context)), parent, false)

        return UserViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val item = mList[position]

        holder.binding.tvUserName.text = item.userName
        holder.binding.tvLastMessage.text = item.lastMessage

        holder.binding.ivUser.load(item.userImage) {
            crossfade(true)
            placeholder(R.drawable.ic_user)
            error(R.drawable.ic_user)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    inner class UserViewHolder(var binding: RowUserBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            onItemClick?.let {
                binding.root.setOnClickListener {
                    it(mList[adapterPosition])
            }

            }
        }
    }
}