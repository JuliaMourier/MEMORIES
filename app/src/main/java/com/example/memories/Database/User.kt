package com.example.memories.Database

import com.google.firebase.database.IgnoreExtraProperties
import java.time.Duration

@IgnoreExtraProperties
data class User(val phoneId: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}