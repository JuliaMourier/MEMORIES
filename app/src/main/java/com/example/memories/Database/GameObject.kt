package com.example.memories.Database

import java.time.Duration
import java.util.*

class GameObject {
    var date : String? = null
    var duration : String? = null
    var nbTry : String? = null
    var id : String? = null

    //Constructor
    fun GameObject(date_: String?, duration_: String?, nbTry_: String?, id_:String?){
        date = date_
        duration = duration_
        nbTry = nbTry_
        id = id_
    }

    //GETTERS
    fun getMDate(): String? {return date}
    fun getMId():String? {return id}
    fun getMDuration(): String? {return duration}
    fun getMNbTry(): String? {return nbTry}

    //SETTERS
    fun setMDate(date_: String?) {date = date_}
    fun setMId(id_: String?) {id = id_}
    fun setMDuration(duration_: String?) {duration = duration_}
    fun setMNbTry(nbTry_: String) {nbTry = nbTry_}



}