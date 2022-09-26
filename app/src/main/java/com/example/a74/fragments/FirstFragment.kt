package com.example.a74.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.a74.R
import com.example.a74.databinding.FragmentFirstBinding
import com.example.a74.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

lateinit var auth: FirebaseAuth

class FirstFragment : Fragment() {
    lateinit var googleSignInClient: GoogleSignInClient
    private var RC_SIGN_IN = 1
    lateinit var binding: FragmentFirstBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        val user = auth.currentUser
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        if (user != null) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.bottomFragment)

        } else {
            googleSignInClient.signOut()
        }
        binding = FragmentFirstBinding.inflate(layoutInflater)

        binding.swipeBtn.setOnActiveListener {
            signIn()
        }






        auth = FirebaseAuth.getInstance()
        return binding.root

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCoder: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCoder, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("FFF", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("FFF", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FFF", "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.d("PPP", "firebaseAuthWithGoogle: ${user?.phoneNumber} aaaaaaa")
                    findNavController().popBackStack(R.id.firstFragment, true)
                    findNavController().navigate(R.id.bottomFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FFF", "signInWithCredential:failure", task.exception)
                }
            }
    }

}