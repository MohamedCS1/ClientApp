package com.example.clientapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clientapplication.Utils.LoadingDialog
import com.example.clientapplication.databinding.ActivityStartEventBinding
import com.example.clientapplication.pojo.RequestMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class StartEventActivity : AppCompatActivity() {

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }

    private lateinit var auth: FirebaseAuth

    private val fireStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val currentRequestsRef get() = fireStore.document("usersRequests/${auth.currentUser!!.uid}").collection("requests")

    lateinit var binding:ActivityStartEventBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        binding.buttonSubmit.setOnClickListener {
            if (binding.editTextName.text.isBlank())
            {
                binding.editTextName.error = "Name required"
                binding.editTextName.requestFocus()
                return@setOnClickListener
            }

            if (binding.textViewDate.text.isBlank())
            {
                binding.textViewDate.error = "Date required"
                binding.textViewDate.requestFocus()
                return@setOnClickListener
            }

            if (binding.editTextBrief.text.isBlank())
            {
                binding.editTextBrief.error = "Brief required"
                binding.editTextBrief.requestFocus()
                return@setOnClickListener
            }


            if (binding.editTextNumberOfDays.text.isBlank())
            {
                binding.editTextNumberOfDays.error = "Number of days required"
                binding.editTextNumberOfDays.requestFocus()
                return@setOnClickListener
            }
            sendEmail(RequestMessage(binding.editTextName.text.toString() , auth.currentUser?.email.toString() ,binding.textViewDate.text.toString() ,binding.editTextNumberOfDays.text.toString() ,binding.editTextBrief.text.toString() ,binding.editTextThreeDDesign.text.toString()?:"" ,binding.editTextDWG.text.toString()?:"" ,UUID.randomUUID().toString()))
        }

        binding.textViewDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(this ,object :DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                    binding.textViewDate.text = "${month+1}/$day/$year"
                }
            } ,year ,month ,day)

            datePickerDialog.show()
        }

        binding.buCancel.setOnClickListener {
            finish()
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendEmail(requestMessage: RequestMessage)
    {
        try {
        loadingDialog.show()
            val stringSenderEmail = "E9PlusDirector@gmail.com"
            val stringReceiverEmail = "mezmoh530@gmail.com"
            val stringPasswordSenderEmail = "qsphbshpkvgmnfto"
            val stringHost = "smtp.gmail.com"
            val properties: Properties = System.getProperties()
            properties["mail.smtp.host"] = stringHost
            properties["mail.smtp.port"] = "465"
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.auth"] = "true"

            val session = Session.getInstance(properties ,object :Authenticator(){
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(stringSenderEmail ,stringPasswordSenderEmail)
                }
            })

            val mimeMessage = MimeMessage(session)

            mimeMessage.addRecipient(MimeMessage.RecipientType.TO, InternetAddress(stringReceiverEmail))
            mimeMessage.subject = "E9+ new event submission"
            mimeMessage.setText("Email: ${requestMessage.email} \n Event name: ${requestMessage.eventName}\n Date: ${requestMessage.date} \n Number Of Days: ${requestMessage.numberDays} \n Event Brief: ${requestMessage.brief} \n 3D design link: ${requestMessage.threeDDesignLink} \n DWG link: ${requestMessage.dwgLink}")

            GlobalScope.launch {
                runBlocking {
                    Transport.send(mimeMessage)
                }
                runOnUiThread {
                    Toast.makeText(baseContext ,"The submission was successfully" ,Toast.LENGTH_SHORT).show()
                }
                currentRequestsRef.document().set(requestMessage).addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        loadingDialog.hide()
                        finish()
                    }
                    else
                    {
                        runOnUiThread {
                            Toast.makeText(baseContext ,"Something went wrong. Please try again." ,Toast.LENGTH_SHORT).show()
                        }
                        loadingDialog.hide()
                        finish()
                    }
                }
            }


        }catch (ex:Exception){
            Toast.makeText(this ,"Something went wrong. Please try again." ,Toast.LENGTH_SHORT).show()
            ex.printStackTrace()
        }
    }


}