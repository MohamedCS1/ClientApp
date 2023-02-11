package com.example.clientapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientapplication.adapters.RequestsAdapter
import com.example.clientapplication.databinding.ActivityPreviousRequestsBinding
import com.example.clientapplication.pojo.RequestMessage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject

class PreviousRequestsActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    private val fireStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val currentRequestsRef get() = fireStore.document("usersRequests/${auth.currentUser!!.uid}").collection("requests")

    lateinit var requestsAdapter: RequestsAdapter

    lateinit var binding: ActivityPreviousRequestsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviousRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestsAdapter = RequestsAdapter()

        binding.recyclerViewRequests.adapter = requestsAdapter

        binding.recyclerViewRequests.layoutManager = LinearLayoutManager(this)

        currentRequestsRef.addSnapshotListener{
            querySnapshot ,error ->
            requestsAdapter.setList(querySnapshot?.toObjects(RequestMessage::class.java) as ArrayList<RequestMessage>)
        }
    }
}