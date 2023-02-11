package com.example.clientapplication

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clientapplication.Utils.LoadingDialog
import com.example.clientapplication.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*


class LoginActivity : AppCompatActivity() {


    private lateinit var oneTapClient: SignInClient

    private lateinit var signInRequest: BeginSignInRequest

    private val REQ_ONE_TAP: Int = 2

    lateinit var auth: FirebaseAuth

    private val fireStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val currentUserDocRef get() = fireStore.document("users/${auth.currentUser!!.uid}")

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }

    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonSingUp.setOnClickListener {
            startActivity(Intent(this, SingUpActivity::class.java))
        }

        binding.buttonGoogle.setOnClickListener {
            googleAuth()
        }

        binding.buttonLogIn.setOnClickListener {
            logInWithEmailAndPassword()
        }

    }

    override fun onResume() {
        if (auth.currentUser != null)
        {
            finish()
            startActivity(Intent(this ,MainActivity::class.java))
        }
        super.onResume()
    }



    fun logInWithEmailAndPassword()
    {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.editTextEmail.error = "Please enter a valid email"
            binding.editTextEmail.requestFocus()
            return
        }

        if (password.length < 6)
        {
            binding.editTextPassword.error = "Password 6 char required"
            binding.editTextPassword.requestFocus()
            return
        }
        logInWithEmailAndPassword(email ,password)

    }

    private fun logInWithEmailAndPassword(email: String, password: String) {
        loadingDialog.show()
        auth.signInWithEmailAndPassword(email ,password).addOnCompleteListener {
            if(it.isSuccessful)
            {
                emailIsVerify()
            }
            else
            {
                loadingDialog.hide()
                binding.textViewError.text = "${it.exception!!.message}"
                binding.textViewError.visibility = View.VISIBLE
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun emailIsVerify()
    {
        GlobalScope.launch(Dispatchers.Main) {

            val user = auth.currentUser

            val reloadFirebase = async { user!!.reload() }

            reloadFirebase.await().addOnCompleteListener {
                if (it.isSuccessful)
                {
                    if (user!!.isEmailVerified)
                    {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener {
                                task->
                            if (!task.isSuccessful)
                            {
                                binding.textViewError.text = "Something went wrong. Please try again."
                                binding.textViewError.visibility = View.VISIBLE
                                loadingDialog.hide()
                                return@addOnCompleteListener
                            }
                            val token = task.result
                            fireStore.collection("users").document(auth.uid.toString()).update(mapOf("token" to(token)))
                                .addOnCompleteListener {
                                    if (!it.isSuccessful)
                                    {
                                        binding.textViewError.text = "Something went wrong. Please try again."
                                        binding.textViewError.visibility = View.VISIBLE
                                        loadingDialog.hide()
                                    }
                                    else
                                    {
                                        loadingDialog.hide()
                                        startActivity(Intent(this@LoginActivity ,MainActivity::class.java))
                                    }
                                }
                        }

                    }
                    else
                    {
                        loadingDialog.hide()
                        binding.textViewError.text = "Please check your email to verify it"
                        binding.textViewError.visibility = View.VISIBLE
                    }
                }
                else
                {
                    loadingDialog.hide()
                    binding.textViewError.text = "Please check your internet"
                    binding.textViewError.visibility = View.VISIBLE
                }
            }
        }
    }

    fun googleAuth() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Toast.makeText(baseContext, "something went wrong try again", Toast.LENGTH_LONG)
                        .show()
                }
            }
            .addOnFailureListener(this) { e ->
                Toast.makeText(baseContext, e.toString(), Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_ONE_TAP) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val username = credential.displayName
                val password = credential.password

                val photoProfileUri = credential.profilePictureUri

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                auth.signInWithCredential(firebaseCredential).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        currentUserDocRef.get().addOnSuccessListener { document ->
                            if (document.exists()) {
                                currentUserDocRef.update(
                                    mapOf(
                                        "email" to (auth.currentUser?.email ?: "No email to display"),
                                        "name" to username,
                                        "imagePath" to photoProfileUri.toString(),
                                        "uid" to currentUser?.uid
                                    )
                                ).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }
                                    else
                                    {
                                        Toast.makeText(this ,"something went wrong try again" ,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                currentUserDocRef.set(
                                    mapOf(
                                        "email" to (auth.currentUser?.email ?: "No email to display"),
                                        "name" to username,
                                        "imagePath" to photoProfileUri.toString(),
                                        "uid" to currentUser?.uid
                                    )
                                ).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }
                                    else
                                    {
                                        Toast.makeText(this ,"something went wrong try again" ,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        }
                    }
                }
            }catch (ex:Exception){
                Toast.makeText(this ,ex.message ,Toast.LENGTH_SHORT).show()
            }


            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}