package com.example.a74.fragments.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a74.databinding.AddGroupItemBinding
import com.example.a74.fragments.GroupsFragment
import com.example.a74.models.Message
import com.example.a74.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddGroupRvAdapter(var context: Context,var list: List<User>,var clickOnUser: clickOnUser) : RecyclerView.Adapter<AddGroupRvAdapter.Vh>() {
    inner class Vh(var addGroupItemBinding: AddGroupItemBinding) :
        RecyclerView.ViewHolder(addGroupItemBinding.root) {
        fun onBind(user: User) {

            Glide.with(context).load(user.photoUrl).into(addGroupItemBinding.profileImage)
            addGroupItemBinding.name.text = user.name
            var reference = FirebaseDatabase.getInstance().getReference("Users")
            reference.child("${FirebaseAuth.getInstance().uid}/messages/${user.uid}")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.children
                        var list = ArrayList<Message>()
                        for (child in children) {
                            val value = child.getValue(Message::class.java)
                            if (value != null) {
                                list.add(value)
                            }
                        }

                        if (list.isNotEmpty()) {
                            addGroupItemBinding.time.text = list.get(list.lastIndex).time
                            if (list.get(list.lastIndex).fromUid == user.uid
                            ) {
                                addGroupItemBinding.fromWho.text = "New: "
                                addGroupItemBinding.message.text = list.get(list.lastIndex).message
                            } else {
                                addGroupItemBinding.fromWho.text = "You: "
                                addGroupItemBinding.message.text = list.get(list.lastIndex).message

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            addGroupItemBinding.root.setOnClickListener {
                clickOnUser.selectUser(user,addGroupItemBinding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(AddGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


interface clickOnUser{
    fun selectUser(user: User,addGroupItemBinding: AddGroupItemBinding)
}