package com.example.clientapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.clientapplication.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        Glide.with(this).load(auth.currentUser?.photoUrl.toString()).into(binding.userPhotoProfile)

        binding.userName.text = auth.currentUser?.displayName


        binding.userEmail.text = auth.currentUser?.email

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        binding.buttonLogOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this ,LoginActivity::class.java))
            finish()
        }
    }
}