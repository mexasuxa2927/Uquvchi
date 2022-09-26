package com.example.a74.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.a74.R
import com.example.a74.databinding.FragmentPeopleBinding
import com.example.a74.fragments.adapters.RvAdapter
import com.example.a74.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class PeopleFragment : Fragment() {
    lateinit var binding: FragmentPeopleBinding
    lateinit var auth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var list: ArrayList<User>
    lateinit var currentUser: FirebaseUser
    lateinit var rvAdapter: RvAdapter
    lateinit var reference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        list = ArrayList()
        binding = FragmentPeopleBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Users")
        if (auth.currentUser != null) {
            setReferences()
        }
        return binding.root
    }

    private fun setReferences() {
        currentUser = auth.currentUser!!
        val user = User(
            currentUser.displayName,
            currentUser.uid,
            currentUser.email,
            currentUser.photoUrl.toString(),
            true
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                var children: MutableIterable<DataSnapshot> = snapshot.children
                val filterList = ArrayList<User>()
                for (child in children) {
                    val value: User? = child.getValue(User::class.java)
                    if (value != null && value.uid != currentUser.uid) {
                        list.add(value)
                    }
                    if (value != null && value.uid == currentUser.uid) {
                        filterList.add(value)
                    }
                }

                if (filterList.isEmpty()) {
                    reference.child(currentUser.uid).setValue(user)
                }
                rvAdapter = RvAdapter(binding.root.context, list, object : RvAdapter.OnClick {
                    override fun onClickUser(user: User) {
                        val bundle = Bundle()
                        bundle.putSerializable("user", user)
                        findNavController().navigate(R.id.sendMessageFragment, bundle)
                    }
                })
                binding.rv.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }


        })
    }

}