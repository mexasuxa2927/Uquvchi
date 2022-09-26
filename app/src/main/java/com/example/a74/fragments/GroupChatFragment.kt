package com.example.a74.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.a74.databinding.FragmentGroupChatBinding
import com.example.a74.fragments.adapters.GroupChatRv
import com.example.a74.models.GroupModel
import com.example.a74.models.Message
import com.example.a74.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class GroupChatFragment : Fragment() {
    lateinit var binding: FragmentGroupChatBinding
    lateinit var groupChatRv: GroupChatRv
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var group: GroupModel
    lateinit var userList: ArrayList<User>
    lateinit var reference2: DatabaseReference
    lateinit var reference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupChatBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Group")
        userList = ArrayList()
        setUserList()
        group = arguments?.getSerializable("group") as GroupModel
        reference2 = firebaseDatabase.getReference("Group")

        binding.send.setOnClickListener {
            sendMessage()
        }
        setView()
        setAdapter()
        return binding.root
    }

    fun setUserList(): ArrayList<User> {
        val currentUser = auth.currentUser!!
        val user = User(
            currentUser.displayName,
            currentUser.uid,
            currentUser.email,
            currentUser.photoUrl.toString(),
            true
        )
        var reference = firebaseDatabase.getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                var children: MutableIterable<DataSnapshot> = snapshot.children
                val filterList = ArrayList<User>()
                for (child in children) {
                    val value: User? = child.getValue(User::class.java)
                    if (value != null) {
                        userList.add(value)
                        Log.d("UUU", "onDataChange: ${userList.size}")
                    }
                    if (value != null && value.uid == currentUser.uid) {
                        filterList.add(value)
                    }
                }

                if (filterList.isEmpty()) {
                    reference.child(currentUser.uid).setValue(user)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return userList
    }

    private fun sendMessage() {

        var timeZone = ""
        val message = binding.message.text.toString().trim()
        if (message.isNotEmpty()) {
            val now = Calendar.getInstance()
            timeZone = if (now[Calendar.AM_PM] === Calendar.AM) {
                "AM"
            } else {
                "PM"
            }
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val date = simpleDateFormat.format(Date()) + " " + timeZone
            val key = reference2.push().key!!
            val messageModel =
                Message(message, date, firebaseAuth.currentUser!!.uid, group.key, false, key)
            Log.d("KKK", "sendMessage: ${group.key}")
            group.key?.let {
                reference2.child(it).child("messages").child(key)
                    .setValue(messageModel)
            }
            binding.message.text.clear()
        } else {
            binding.message.setText("")
        }
    }

    private fun setAdapter() {
        reference.child(group.key!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    val list = ArrayList<Message>()
                    for (child in children) {
                        val value = child.getValue(Message::class.java)
                        if (value != null) {
                            list.add(value)
                        }
                    }
                    Log.d("MMM", "onDataChange: ${setUserList().size}")
                    groupChatRv = GroupChatRv(group,
                        binding.root.context,
                        list,
                        firebaseAuth.currentUser!!.uid,
                        userList
                    )
                    binding.rv.adapter = groupChatRv
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    fun setView() {

        binding.status.text = "${group.users!!.size} members"
        binding.back.setOnClickListener { findNavController().popBackStack() }
        binding.name.text = group.name
        if (group.groupPhoto != "") {
            binding.image.setImageURI(Uri.parse(group.groupPhoto))
        } else {
            binding.image.visibility = View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reference =
            FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid!!)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = true
        reference.updateChildren(hashMap)
    }
}