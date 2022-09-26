package com.example.a74.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a74.databinding.FragmentSendMessageBinding
import com.example.a74.fragments.adapters.MessageAdapter
import com.example.a74.models.Message
import com.example.a74.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class SendMessageFragment : Fragment() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var binding: FragmentSendMessageBinding
    lateinit var user: User
    lateinit var messageAdapter: MessageAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendMessageBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        user = arguments?.getSerializable("user") as User
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Users")
        reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}")
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
                    messageAdapter = MessageAdapter(list, user.uid!!)
                    Log.d("AAA", "Scroll:${ binding.rv.scrollState}")
                    binding.rv.adapter = messageAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        binding.send.setOnClickListener {
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
                val key = reference.push().key
                val messageModel = Message(message, date, firebaseAuth.currentUser!!.uid, user.uid,false,key!!)
                reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}/$key")
                    .setValue(messageModel)
                reference.child("${user.uid}/messages/${firebaseAuth.currentUser!!.uid}/$key")
                    .setValue(messageModel)
                binding.message.text.clear()
            } else {
                binding.message.setText("")
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            setTime(user)
        }, 100)
        setView(user)

        return binding.root

    }

    private fun setTime(user: User) {
        when (user.status) {
            true -> binding.status.text = "Online"
            false -> {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formatted = LocalDateTime.now().format(formatter)
                val now = Calendar.getInstance()
                val timeZone = if (now[Calendar.AM_PM] === Calendar.AM) {
                    "AM"
                } else {
                    "PM"
                }
                if (user.statusTime == "$formatted $timeZone") {
                    binding.status.text = "last seen just now"
                } else {
                    binding.status.text = "last seen at " + user.statusTime
                }
            }
        }
    }

    private fun setView(user: User) {
        binding.name.text = user.name
        Glide.with(requireActivity()).load(user.photoUrl).into(binding.image)
        binding.back.setOnClickListener { findNavController().popBackStack() }

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

