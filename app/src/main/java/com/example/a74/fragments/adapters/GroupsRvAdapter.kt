package com.example.a74.fragments.adapters

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.example.a74.R
import com.example.a74.databinding.GroupItemBinding
import com.example.a74.models.GroupModel
import com.example.a74.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupsRvAdapter(var context: Context, var groupLIst: List<GroupModel>, var onClick: OnClick) :
    RecyclerView.Adapter<GroupsRvAdapter.Vh>() {
    interface OnClick {
        fun onClickUser(groupModel: GroupModel)
    }

    inner class Vh(var groupItemBinding: GroupItemBinding) :
        RecyclerView.ViewHolder(groupItemBinding.root) {
        fun onBind(groupModel: GroupModel, position: Int) {
            groupItemBinding.name.text = groupModel.name
            if (groupModel.groupPhoto != "") {
                groupItemBinding.profileImage.setImageURI(Uri.parse(groupModel.groupPhoto))
            } else {
                groupItemBinding.profileImage.visibility = View.INVISIBLE
                groupItemBinding.profileCard.setCardBackgroundColor(Color.parseColor("#016BFD"))
            }
            groupItemBinding.card.setOnClickListener {
                onClick.onClickUser(groupModel)
            }
            FirebaseDatabase.getInstance().getReference("Group").child(groupModel.key!!)
                .child("messages")
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
                            groupItemBinding.time.text = list.get(list.lastIndex).time
                            if (list.get(list.lastIndex).fromUid != FirebaseAuth.getInstance().currentUser!!.uid
                            ) {
                                if (list[list.lastIndex].readStatus!!) {
                                    var userName =""
                                    for (i in 0 until groupLIst[position].users!!.size) {
                                        if (groupLIst[position].users?.get(i)!!.uid == list[list.lastIndex].fromUid) {
                                            userName = groupLIst[position].users?.get(i)!!.name!!
                                        }
                                    }
                                    groupItemBinding.fromWho.text = userName
                                    groupItemBinding.message.text = list.get(list.lastIndex).message
                                } else {
                                    groupItemBinding.fromWho.text = "New: "
                                    groupItemBinding.message.text = list.get(list.lastIndex).message

                                }
                            } else {
                                if (list[list.lastIndex].readStatus!!) {
                                    groupItemBinding.readStatus.setImageResource(R.drawable.read)
                                } else {
                                    groupItemBinding.readStatus.setImageResource(R.drawable.unread)

                                }
                                groupItemBinding.fromWho.text = "You: "
                                groupItemBinding.message.text = list.get(list.lastIndex).message

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(GroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(groupLIst[position], position)
    }

    override fun getItemCount(): Int {
        return groupLIst.size
    }
}

