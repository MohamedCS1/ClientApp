package com.example.clientapplication.pojo

import java.io.Serializable

data class User(val uid:String ,val name:String ,val email:String ,val password:String ,val imagePath:String ):Serializable
{
    constructor():this( "" ,"" ,"","","")

}