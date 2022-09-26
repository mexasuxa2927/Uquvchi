package com.example.a74.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.a74.BuildConfig
import com.example.a74.R
import com.example.a74.databinding.AddGroupDialogBinding
import com.example.a74.databinding.AddGroupItemBinding
import com.example.a74.databinding.AddGroupRvDialogBinding
import com.example.a74.databinding.FragmentGroupsBinding
import com.example.a74.fragments.adapters.AddGroupRvAdapter
import com.example.a74.fragments.adapters.GroupsRvAdapter
import com.example.a74.fragments.adapters.clickOnUser
import com.example.a74.models.GroupModel
import com.example.a74.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class GroupsFragment : Fragment() {
    var imagePath: String = ""
    lateinit var reference: DatabaseReference
    var groupName = ""
    lateinit var userList: ArrayList<User>
    lateinit var inflate: AddGroupDialogBinding
    val grouplist = ArrayList<GroupModel>()
    lateinit var imageView: ImageView
    lateinit var groupReference: DatabaseReference
    var avaliableGroups = ArrayList<GroupModel>()
    lateinit var binding: FragmentGroupsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userList = ArrayList()
        binding = FragmentGroupsBinding.inflate(layoutInflater)
        inflate = AddGroupDialogBinding.inflate(layoutInflater, binding.root, false)
        val currentUser1 = FirebaseAuth.getInstance().currentUser
        var currentUser = User(
            currentUser1?.displayName,
            currentUser1?.uid,
            currentUser1?.email,
            currentUser1?.photoUrl.toString()
        )
        userList.add(currentUser)
        reference = FirebaseDatabase.getInstance().getReference("Users")
        groupReference = FirebaseDatabase.getInstance().getReference("Group")
        binding.addGroup.setOnClickListener { addGroup() }
        return binding.root
    }

    private fun addGroup() {
        val dialog = Dialog(requireContext(), R.style.RoundedCornersDialog)
        inflate.buttonNegative.setOnClickListener { dialog.dismiss() }
        inflate.imageCard.setOnClickListener {
            addImage()
        }
        inflate.buttonPositive.setOnClickListener {
            if (inflate.message.text.toString().trim().isNotEmpty()) {
                addGroupPositiveBtnClicked()
                groupName = inflate.message.text.toString()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Group name is empty!", Toast.LENGTH_SHORT).show()
            }
        }
        imageView = inflate.image
        dialog.setContentView(inflate.root)
        dialog.show()
        dialog.setOnDismissListener {
            var a = inflate.root.parent as ViewGroup
            a.removeView(inflate.root)
            inflate.image.setImageResource(R.drawable.ic_baseline_add_a_photo_24)
            inflate.message.text.clear()
            inflate.image.setImageResource(R.drawable.ic_baseline_add_a_photo_24)
            inflate.message.text.clear()
        }

    }

    private fun addGroupPositiveBtnClicked() {
        val list = ArrayList<User>()
        val dialog = Dialog(requireContext())
        val inflate = AddGroupRvDialogBinding.inflate(layoutInflater)
        val currentUser = FirebaseAuth.getInstance().currentUser
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                var children: MutableIterable<DataSnapshot> = snapshot.children
                for (child in children) {
                    val value: User? = child.getValue(User::class.java)
                    if (value != null && value.uid != currentUser?.uid) {
                        list.add(value)
                    }
                }

                var addGroupRvAdapter =
                    AddGroupRvAdapter(binding.root.context, list, object : clickOnUser {
                        override fun selectUser(
                            user: User,
                            addGroupItemBinding: AddGroupItemBinding
                        ) {
                            if (!userList.contains(user)) {
                                addGroupItemBinding.statust.visibility = View.VISIBLE
                                userList.add(user)

                            } else {
                                addGroupItemBinding.statust.visibility = View.INVISIBLE
                                userList.remove(user)
                            }
                        }


                    })
                inflate.rv.adapter = addGroupRvAdapter
                inflate.buttonPositive.setOnClickListener {
                    addNewGroup()
                    dialog.dismiss()
                }
                inflate.buttonNegative.setOnClickListener { dialog.dismiss() }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        dialog.setContentView(inflate.root)
        dialog.show()
    }

    private fun addNewGroup() {
        val key = groupReference.push().key
        val group = GroupModel(key!!, groupName, userList, imagePath)
        groupReference.child(key).setValue(group)
    }

    private
    val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@registerForActivityResult
            inflate.image.setPadding(0)
            inflate.image.setImageURI(uri)
            val inputStream = binding.root.context.contentResolver?.openInputStream(uri)
            val file =
                File(
                    requireContext().filesDir,
                    "image_${System.currentTimeMillis()}.jpg"
                )
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            val path = file.absolutePath
            imagePath = path


        }


    private fun addImage() {
        val dialog = AlertDialog.Builder(binding.root.context)
        dialog.setTitle("Choose image from:")
        dialog.setPositiveButton(
            "Gallery"
        ) { _, _ ->

            getImageContent.launch("image/*")

        }

        dialog.setNegativeButton(
            "Camera"
        ) { _, _ ->

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(requireContext().packageManager)
            val file = createImageFile()
            file.also {
                val photoUrl = FileProvider.getUriForFile(
                    requireContext(),
                    BuildConfig.APPLICATION_ID,
                    file
                )
                val inflate = AddGroupDialogBinding.inflate(layoutInflater)
                inflate.image.setImageURI(photoUrl)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUrl)
                startActivityForResult(intent, 101)
                Log.d("AAA", "saveRuleBtnClicked: $photoUrl")


            }
        }

        dialog.show()

    }

    private fun createImageFile(): File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHssmm").format(Date())
        val externalFilesDir =
            binding.root.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_$timeStamp", "jpg", externalFilesDir)
            .apply {
                imagePath = absolutePath
            }

    }

    override fun onResume() {
        super.onResume()
        groupReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                grouplist.clear()
                avaliableGroups.clear()
                val hashMap = snapshot.children.toList()
//
                for (groupModel in hashMap) {
                    val group = groupModel.getValue(GroupModel::class.java)
                    grouplist.add(group!!)
                }

                for (i in 0 until grouplist.size) {
                    for (k in 0 until grouplist[i].users!!.size) {
                        if (grouplist[i].users!![k].uid == FirebaseAuth.getInstance().currentUser!!.uid) {
                            avaliableGroups.add(grouplist[i])
                        }
                    }
                }
                val groupAdapter = GroupsRvAdapter(
                    binding.root.context,
                    sortList(avaliableGroups),
                    object : GroupsRvAdapter.OnClick {
                        override fun onClickUser(groupModel: GroupModel) {
                            var bundle = Bundle()
                            bundle.putSerializable("group", groupModel)
                            findNavController().navigate(R.id.groupChatFragment, bundle)
                        }
                    })
                Log.d("AAA", "onDataChange: ${sortList(avaliableGroups).size}")
                binding.rv.adapter = groupAdapter

            }

            private fun sortList(list: ArrayList<GroupModel>): java.util.ArrayList<GroupModel> {
                val mf = ArrayList<GroupModel>()
                for (i in list) {
                    if (!mf.contains(i)) {
                        mf.add(i)
                    }
                }
                return mf
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: ${error.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            if (imagePath != "") {

                inflate.image.setImageURI(Uri.fromFile(File(imagePath)))

                inflate.image.setPadding(0)
            }
        }
    }
}

