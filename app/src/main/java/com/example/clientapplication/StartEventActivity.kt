package com.example.clientapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clientapplication.databinding.ActivityStartEventBinding
import com.example.clientapplication.pojo.MailMessage
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class StartEventActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
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

            if (binding.TextViewDate.text == "MM/DD/YYYY")
            {
                binding.TextViewDate.error = "Date required"
                binding.TextViewDate.requestFocus()
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
            sendEmail(MailMessage(binding.editTextName.text.toString() ,
                auth.currentUser?.email.toString() ,"02/15/2023" ,binding.editTextNumberOfDays.text.toString() ,binding.editTextBrief.text.toString() ,binding.editTextThreeDDesign.text.toString()?:"" ,binding.editTextDWG.text.toString()?:""))
        }

    }

    fun sendEmail(mailMessage: MailMessage)
    {
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
            mimeMessage.setText("Email: ${mailMessage.email} \n Event name: ${mailMessage.eventName}\n Date: ${mailMessage.date} \n Number Of Days: ${mailMessage.numberDays} \n Event Brief: ${mailMessage.brief} \n 3D design link: ${mailMessage.threeDDesignLink} \n DWG link: ${mailMessage.dwgLink}")

            val thread = Thread {
                    Transport.send(mimeMessage)
            }
            thread.start()

        try {

        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }
}