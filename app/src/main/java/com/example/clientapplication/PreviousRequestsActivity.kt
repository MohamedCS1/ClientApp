package com.example.clientapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clientapplication.databinding.ActivityPreviousRequestsBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PreviousRequestsActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    private val fireStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val currentRequestsRef get() = fireStore.document("usersRequests/${auth.currentUser!!.uid}").collection("requests")

    lateinit var binding: ActivityPreviousRequestsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviousRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentRequestsRef.addSnapshotListener{
            querySnapshot ,error ->
            for (i in querySnapshot!!)
            {

            }
        }
    }
}