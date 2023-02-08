package com.example.clientapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.clientapplication.Utils.LoadingDialog
import com.example.clientapplication.databinding.ActivitySingUpBinding
import com.example.clientapplication.pojo.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SingUpActivity : AppCompatActivity() {
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val  currentUserDocRef get() =  fireStore.document("users/${auth.currentUser!!.uid}")

    private val progressDialog:LoadingDialog by lazy {
        LoadingDialog(this)
    }

    lateinit var binding:ActivitySingUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        binding.buttonSingUp.setOnClickListener {
            signUp()
        }

    }

    fun signUp()
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
        createNewAccount(User("","" ,email ,password ,""))
    }

    fun createNewAccount(user: User)
    {
        progressDialog.show()
        auth.createUserWithEmailAndPassword(user.email ,user.password).addOnCompleteListener(object :
            OnCompleteListener<AuthResult> {
            override fun onComplete(task: Task<AuthResult>) {
                if (task.isSuccessful)
                {
                    progressDialog.hide()
                    sendEmailVerification()
                    currentUserDocRef.set(user)
                    val intentToMainActivity = Intent(this@SingUpActivity ,LoginActivity::class.java)
                    intentToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intentToMainActivity)
                }
                else
                {
                    progressDialog.hide()
                    binding.textViewError.text = task.exception?.message.toString()
                    binding.textViewError.visibility = View.VISIBLE
                }
            }
        })
    }

    fun sendEmailVerification() {
        val user = auth.currentUser
        user!!.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful)
            {
                Toast.makeText(this ,"Email is send" ,Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this , it.exception?.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

}