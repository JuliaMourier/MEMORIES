package com.example.memories.Database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GamesData : RealmObject() {

    @PrimaryKey
    private var gameData_id = 0 //id for database
    private var gameData_user: String? = null //id of the user : will contain the unique id of the user's phone
    private var gameData_date: String? = null //Date of the game
    private var gameData_nbTry : String? = null //number of try of the game
    private var gameData_duration : String? = null //number of try of the game


    //SETTERS & GETTERS
    fun getGameDataDuration(): String? {
        return gameData_duration
    }

    fun getGameData_nbTry(): String? {
        return gameData_nbTry
    }

    fun getGameData_user(): String? {
        return gameData_user
    }

    fun getGameData_Date(): String? {
        return gameData_date
    }

    fun setGameData_duration(duration: String?){
       this.gameData_duration = duration
    }

    fun setGameData_user(gameData_User: String?) {
        this.gameData_user = gameData_User
    }

    fun setGameData_date(getGameData_Date: String?) {
        this.gameData_date = getGameData_Date
    }

    fun setGameData_nbTry(getGameData_nbTry: String?) {
        this.gameData_nbTry = getGameData_nbTry
    }





}