package com.example.a74.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.a74.R
import com.example.a74.databinding.FragmentBottomBinding
import com.example.a74.databinding.LogOutBinding
import com.example.a74.fragments.adapters.ViewPagerAdapter
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class BottomFragment : Fragment() {
    lateinit var binding: FragmentBottomBinding
    lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomBinding.inflate(layoutInflater)
        checkPermission()
        findNavController().addOnDestinationChangedListener(NavController.OnDestinationChangedListener { controller, destination: NavDestination, arguments ->


        })
        return binding.root
    }


    fun checkPermission() {
        askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA) {
            setViewPager()
            binding.hello.text = "Hello, " + FirebaseAuth.getInstance().currentUser?.displayName
            setBottomNav()
            binding.logOut.setOnClickListener {
                setLogOut()
            }//all permissions already granted or just granted

        }.onDeclined { e ->
            if (e.hasDenied()) {

                AlertDialog.Builder(requireContext())
                    .setMessage("Please accept our permissions")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain();
                    } //ask again
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss();
                    }
                    .show();
            }

            if (e.hasForeverDenied()) {

                // you need to open setting manually if you really need it
                e.goToSettings();
            }
        }
    }

    private fun setLogOut() {
        val dialog = Dialog(binding.root.context)
        val view = LogOutBinding.inflate(layoutInflater)
        view.buttonNegative.text = "Cancel"
        view.buttonPositive.text = "Log out"
        view.textViewTitle.text = "Do you want to log out?"
        view.textViewMessage.text = "Are you sure to log out?"
        view.buttonPositive.setOnClickListener {
            val reference =
                FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid!!)
            val hashMap = HashMap<String, Any>()
            val hashMap2 = HashMap<String, Any>()
            hashMap["status"] = false
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val formatted = LocalDateTime.now().format(formatter)
            val now = Calendar.getInstance()
            val timeZone = if (now[Calendar.AM_PM] === Calendar.AM) {
                "AM"
            } else {
                "PM"
            }
            hashMap2["statusTime"] = formatted + " $timeZone"
            reference.updateChildren(hashMap)
            reference.updateChildren(hashMap2)
            FirebaseAuth.getInstance().signOut()
            dialog.cancel()
            findNavController().popBackStack()
            findNavController().navigate(R.id.firstFragment)
        }
        view.buttonNegative.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(view.root)
        dialog.show()
    }

    private fun setViewPager() {
        viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            listOf(PeopleFragment(), GroupsFragment()),
            lifecycle
        )
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomBar.itemActiveIndex = position
            }

        })
        binding.viewpager.adapter = viewPagerAdapter
    }

    private fun setBottomNav() {
        val popupMenu = PopupMenu(binding.root.context, null)
        popupMenu.inflate(R.menu.menu_bottom)
        binding.bottomBar.setOnItemSelectedListener {
            Log.d("TAG", "setBottomNav: $it")
            when (it) {
                1 -> binding.viewpager.currentItem = 1
                0 -> binding.viewpager.currentItem = 0
            }
        }
    }

    override fun onStart() {

        super.onStart()
        val reference =
            FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid!!)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = true
        reference.updateChildren(hashMap)
    }

    override fun onStop() {
        super.onStop()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val reference =
                FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid!!)
            val hashMap = HashMap<String, Any>()
            val hashMap2 = HashMap<String, Any>()
            hashMap["status"] = false
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val formatted = LocalDateTime.now().format(formatter)
            val now = Calendar.getInstance()
            val timeZone = if (now[Calendar.AM_PM] === Calendar.AM) {
                "AM"
            } else {
                "PM"
            }
            hashMap2["statusTime"] = "$formatted $timeZone"
            reference.updateChildren(hashMap)
            reference.updateChildren(hashMap2)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val reference =
                FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid!!)
            val hashMap = HashMap<String, Any>()
            val hashMap2 = HashMap<String, Any>()
            hashMap["status"] = false
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val formatted = LocalDateTime.now().format(formatter)
            val now = Calendar.getInstance()
            val timeZone = if (now[Calendar.AM_PM] === Calendar.AM) {
                "AM"
            } else {
                "PM"
            }
            hashMap2["statusTime"] = "$formatted $timeZone"
            reference.updateChildren(hashMap)
            reference.updateChildren(hashMap2)
        }
    }

}
