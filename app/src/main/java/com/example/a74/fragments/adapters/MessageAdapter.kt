package com.example.a74.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a74.R
import com.example.a74.databinding.MessageItem2Binding
import com.example.a74.databinding.MessageItemBinding
import com.example.a74.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessageAdapter(var list: List<Message>, var uid: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class FromVh(var messageItem2Binding: MessageItem2Binding) :
        RecyclerView.ViewHolder(messageItem2Binding.root) {
        fun onBind(message: Message, position: Int) {
            messageItem2Binding.time.text = message.time
            messageItem2Binding.message.text = message.message
            val reference = FirebaseDatabase.getInstance().getReference("Users").child(
                "$uid/messages/${FirebaseAuth.getInstance().currentUser!!.uid}/${message.key}"
            )
            val reference2 = FirebaseDatabase.getInstance().getReference("Users").child(
                "${FirebaseAuth.getInstance().currentUser!!.uid}/messages/${uid}/${message.key}"
            )

            val hashMap = HashMap<String, Any>()
            hashMap["readStatus"] = true

            reference.updateChildren(hashMap)
            reference2.updateChildren(hashMap)
        }
    }

    inner class ToVh(var messageItemBinding: MessageItemBinding) :
        RecyclerView.ViewHolder(messageItemBinding.root) {
        fun onBind(message: Message, position: Int) {

            if (message.readStatus!!) {
                messageItemBinding.readStatus.setImageResource(R.drawable.read)
            }
            messageItemBinding.time.text = message.time
            messageItemBinding.message.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return FromVh(
                MessageItem2Binding.inflate(
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
            fromVh.onBind(list[position], position)
        } else {
            val toVh = holder as ToVh
            toVh.onBind(list[position], position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].fromUid == uid) {
            1

        } else {
            2
        }
        return super.getItemViewType(position)

    }

    override fun getItemCount(): Int {
        return list.size
    }

}