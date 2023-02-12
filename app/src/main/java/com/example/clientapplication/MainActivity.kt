package com.example.clientapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.example.clientapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val popUpMenu = PopupMenu(this ,binding.buttonMenu)
        popUpMenu.menu.add(Menu.NONE, 0, 0, "My account")
        popUpMenu.menu.add(Menu.NONE, 1, 1, "Start new event")
        popUpMenu.menu.add(Menu.NONE, 2, 2, "Previous requests")
        popUpMenu.menu.add(Menu.NONE, 3, 3, "Contact")
        popUpMenu.menu.add(Menu.NONE, 4, 4, "About us")

        binding.buttonMenu.setOnClickListener {
            popUpMenu.show()
        }

        popUpMenu.setOnMenuItemClickListener(object :PopupMenu.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when(item?.itemId)
                {
                    0->{
                        startActivity(Intent(this@MainActivity ,ProfileActivity::class.java))
                    }
                    1->{
                        startActivity(Intent(this@MainActivity ,StartEventActivity::class.java))
                    }
                    2->{
                        startActivity(Intent(this@MainActivity ,PreviousRequestsActivity::class.java))
                    }
                    3->{
                        startActivity(Intent(this@MainActivity ,ContactUsActivity::class.java))
                    }
                    4->{
                        startActivity(Intent(this@MainActivity ,AboutUsActivity::class.java))
                    }
                }
                return true
            }
        })
    }
}