package com.example.clientapplication.pojo

import java.io.Serializable
import java.util.*

data class RequestMessage(val eventName:String, val email:String, val date:String, val numberDays:String, val brief:String, val threeDDesignLink:String, val dwgLink:String ,val uid:String):
    Serializable
{
    constructor():this( "" ,"" ,"","","","","","")

}
