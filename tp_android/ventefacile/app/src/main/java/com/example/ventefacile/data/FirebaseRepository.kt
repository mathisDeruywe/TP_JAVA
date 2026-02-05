package com.example.ventefacile.data

import com.example.ventefacile.models.Vente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.util.Log

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseDatabase
        .getInstance("https://ventefacile-25a28-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference("ventes")

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun enregistrerVente(vente: Vente): Result<Unit> {
        return try {
            val venteRef = db.push()
            venteRef.setValue(vente).await()
            Log.d("FirebaseRepo", "Vente enregistrée avec succès")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Erreur enregistrement: ${e.message}")
            Result.failure(e)
        }
    }

    fun ecouterVentes(userId: String): Flow<List<Vente>> = callbackFlow {
        val query = db.orderByChild("commercant_id").equalTo(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ventes = snapshot.children.mapNotNull { it.getValue(Vente::class.java) }
                trySend(ventes)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseRepo", "Erreur lecture: ${error.message}")
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }
}