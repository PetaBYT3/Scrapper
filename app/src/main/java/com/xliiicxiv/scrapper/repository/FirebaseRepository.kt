package com.xliiicxiv.scrapper.repository

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.xliiicxiv.scrapper.dataclass.UserDataClass
import com.xliiicxiv.scrapper.string.LoginResult
import com.xliiicxiv.scrapper.string.UserDataResult
import com.xliiicxiv.scrapper.string.isExist
import com.xliiicxiv.scrapper.string.isFail
import com.xliiicxiv.scrapper.string.isSuccess
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseRepository {

    val firebaseDatabase = Firebase.database("https://autochecker-6955f-default-rtdb.asia-southeast1.firebasedatabase.app/")

    val userRef = firebaseDatabase.getReference("users")

    suspend fun login(
        userName: String,
        userPassword: String,
        androidId: String
    ) : LoginResult {
        return suspendCancellableCoroutine { continuation ->
            val query = userRef.orderByChild("userName").equalTo(userName)
            val userNameListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (continuation.isActive) {
                        if (snapshot.exists()) {
                            val userSnapshot = snapshot.children.first()
                            val userData = userSnapshot.getValue(UserDataClass::class.java)
                            if (userData != null) {
                                if (userData.userPassword == userPassword) {
                                    if (userData.androidId == "" || userData.androidId == androidId) {
                                        val update = mapOf(
                                            "androidId" to androidId
                                        )
                                        userSnapshot.ref.updateChildren(update)
                                        continuation.resume(LoginResult.Success(userData.userId))
                                    } else {
                                        continuation.resume((LoginResult.DifferentAndroidId))
                                    }
                                } else {
                                    continuation.resume((LoginResult.Fail))
                                }
                            } else {
                                continuation.resume((LoginResult.Fail))
                            }
                        } else {
                            continuation.resume((LoginResult.Fail))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume((LoginResult.Fail))
                }
            }

            query.addListenerForSingleValueEvent(userNameListener)
        }
    }

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

    suspend fun getUserById(userId: String) : Flow<UserDataClass?> {
        return callbackFlow {
            val query = userRef.orderByChild("userId").equalTo(userId)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnapshot = snapshot.children.first()
                        val userData = userSnapshot.getValue(UserDataClass::class.java)
                        if (userData != null) {
                            trySend(userData)
                        }
                    } else {
                        trySend(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(null)
                }
            }

            query.addValueEventListener(listener)

            awaitClose {
                query.removeEventListener(listener)
            }
        }
    }

    suspend fun checkUserExistence(userId: String): Flow<Boolean> {
        return callbackFlow {
            val query = userRef.child(userId)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(false)
                }
            }

            query.addValueEventListener(listener)

            awaitClose {
                query.removeEventListener(listener)
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

    fun deleteAndroidId(userData: UserDataClass) {
        val query = userRef.child(userData.userId)

        val update = mapOf(
            "userId" to userData.userId,
            "userName" to userData.userName,
            "userPassword" to userData.userPassword,
            "userRole" to userData.userRole,
            "androidId" to null
        )
        query.updateChildren(update)
    }

    fun deleteUser(userData: UserDataClass) {
        userRef.child(userData.userId).removeValue()
    }
}