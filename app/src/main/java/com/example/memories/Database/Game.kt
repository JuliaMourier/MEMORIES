package com.example.memories.Database
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Game(val date: String? = null, val duration: String? = null,val nbOfTries: String? = null) {

    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}