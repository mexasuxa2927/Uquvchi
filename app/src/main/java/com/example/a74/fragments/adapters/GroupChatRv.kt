package com.example.a74.fragments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a74.R
import com.example.a74.databinding.GorupMessageItemBinding
import com.example.a74.databinding.MessageItemBinding
import com.example.a74.models.GroupModel
import com.example.a74.models.Message
import com.example.a74.models.User
import com.google.firebase.database.FirebaseDatabase


class GroupChatRv(
    var group: GroupModel,
    var context: Context,
    var list: List<Message>,
    var uid: String,
    var userList: ArrayList<User>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class FromVh(var messageItem2Binding: GorupMessageItemBinding) :
        RecyclerView.ViewHolder(messageItem2Binding.root) {

        fun onBind(message: Message) {
            val reference = FirebaseDatabase.getInstance().getReference("Group").child(group.key!!)
                .child("messages").child(message.key)

            val hashMap = HashMap<String, Any>()
            hashMap["readStatus"] = true

            reference.updateChildren(hashMap)
            for (i in userList) {
                if (i.uid == message.fromUid) {
                    Glide.with(context).load(i.photoUrl).into(messageItem2Binding.profileImage)
                    messageItem2Binding.name.text = i.name
                }
            }
            messageItem2Binding.time.text = message.time
            messageItem2Binding.message.text = message.message
        }
    }

    inner class ToVh(var messageItemBinding: MessageItemBinding) :
        RecyclerView.ViewHolder(messageItemBinding.root) {
        fun onBind(message: Message) {
            if (message.readStatus!!) {
                messageItemBinding.readStatus.setImageResource(R.drawable.read)
            } else {
                messageItemBinding.readStatus.setImageResource(R.drawable.unread)
            }
            messageItemBinding.time.text = message.time
            messageItemBinding.message.text = message.message
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return FromVh(
                GorupMessageItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return ToVh(
                MessageItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
        } else {
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].fromUid == uid) {
            2

        } else {
            1
        }
        return super.getItemViewType(position)

    }

    override fun getItemCount(): Int {
        return list.size
    }
}