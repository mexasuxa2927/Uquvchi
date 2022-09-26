package com.example.a74.fragments.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a74.R
import com.example.a74.databinding.RvItemBinding
import com.example.a74.models.Message
import com.example.a74.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RvAdapter(var context: Context, var list: List<User>, var onClick: OnClick) :
    RecyclerView.Adapter<RvAdapter.Vh>() {
    interface OnClick {
        fun onClickUser(user: User)
    }

    inner class Vh(var rvItemBinding: RvItemBinding) :
        RecyclerView.ViewHolder(rvItemBinding.root) {
        fun onBind(user: User, position: Int) {
            rvItemBinding.name.text = user.name
            Glide.with(context).load(user.photoUrl).into(rvItemBinding.profileImage)
            if (!user.status) {
                rvItemBinding.bg.setBackgroundColor(Color.parseColor("#E6EDF6"))
            }
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
                            rvItemBinding.time.text = list.get(list.lastIndex).time
                            if (list.get(list.lastIndex).fromUid == user.uid
                            ) {
                                if (list[list.lastIndex].readStatus!!) {
                                    rvItemBinding.message.text = list.get(list.lastIndex).message
                                } else {
                                    rvItemBinding.fromWho.text = "New: "
                                    rvItemBinding.message.text = list.get(list.lastIndex).message

                                }
                            } else {
                                if (list[list.lastIndex].readStatus!!) {
                                    rvItemBinding.readStatus.setImageResource(R.drawable.read)
                                } else {
                                    rvItemBinding.readStatus.setImageResource(R.drawable.unread)

                                }
                                rvItemBinding.fromWho.text = "You: "
                                rvItemBinding.message.text = list.get(list.lastIndex).message

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            rvItemBinding.card.setOnClickListener {
                onClick.onClickUser(user)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

