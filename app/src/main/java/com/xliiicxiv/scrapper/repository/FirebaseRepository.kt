package com.xliiicxiv.scrapper.repository

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.xliiicxiv.scrapper.dataclass.UserDataClass
import com.xliiicxiv.scrapper.string.isExist
import com.xliiicxiv.scrapper.string.isFail
import com.xliiicxiv.scrapper.string.isSuccess
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    val firebaseDatabase = Firebase.database("https://autochecker-6955f-default-rtdb.asia-southeast1.firebasedatabase.app/")

    val userRef = firebaseDatabase.getReference("users")

    suspend fun getUser() : Flow<List<UserDataClass>> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userList = mutableListOf<UserDataClass>()

                    if (snapshot.exists()) {
                        for (snapshotChildren in snapshot.children) {
                            val userData = snapshotChildren.getValue(UserDataClass::class.java)
                            if (userData != null) {
                                userList.add(userData)
                            }
                        }
                    }
                    trySend(userList)
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(emptyList())
                }
            }

            userRef.addValueEventListener(listener)

            awaitClose {
                userRef.removeEventListener(listener)
            }
        }
    }


    suspend fun addUser(userData: UserDataClass) : String {
        return suspendCancellableCoroutine { continuation ->
            val generatedId = userRef.push().key.toString()
            val finalUserData = UserDataClass(
                userId = generatedId,
                userName = userData.userName,
                userPassword = userData.userPassword,
                userRole = userData.userRole
            )

            val detectUsername = userRef.orderByChild("userName").equalTo(userData.userName)
            detectUsername.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (continuation.isActive) continuation.resume(isExist, null)
                    } else {
                        userRef.child(generatedId).setValue(finalUserData)
                            .addOnSuccessListener {
                                if (continuation.isActive) continuation.resume(isSuccess, null)
                            }
                            .addOnFailureListener {
                                if (continuation.isActive) continuation.resume(isFail, null)
                            }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) continuation.resume(isFail, null)
                }
            })
        }
    }

    fun deleteUser(userData: UserDataClass) {
        userRef.child(userData.userId).removeValue()
    }
}