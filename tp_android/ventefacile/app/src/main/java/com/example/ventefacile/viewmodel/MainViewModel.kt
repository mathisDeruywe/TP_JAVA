package com.example.ventefacile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ventefacile.data.FirebaseRepository
import com.example.ventefacile.models.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repository = FirebaseRepository()

    var isLoading by mutableStateOf(false)
    var statusMessage by mutableStateOf("")

    var produitsChoisis = mutableStateOf<List<Produit>>(emptyList())

    val totalVente: Double
        get() = produitsChoisis.value.sumOf { it.prix_unitaire * it.quantite }

    var listeVentes = mutableStateOf<List<Vente>>(emptyList())
        private set

    var currentUser by mutableStateOf(auth.currentUser)
        private set

    fun register(email: String, pass: String, onSuccess: () -> Unit) {
        isLoading = true
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    onSuccess()
                } else {
                    statusMessage = "Erreur d'inscription : ${task.exception?.message}"
                }
            }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        isLoading = true
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    onSuccess()
                } else {
                    statusMessage = "Erreur de connexion : ${task.exception?.message}"
                }
            }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
    }

    fun ajouterProduit(nom: String, prix: Double, qte: Int) {
        val nouveau = Produit(nom = nom, prix_unitaire = prix, quantite = qte)
        produitsChoisis.value = produitsChoisis.value + nouveau
    }

    fun validerVente(nomClient: String, tel: String, lat: Double, lng: Double) {
        val userId = auth.currentUser?.uid ?: return

        val nouvelleVente = Vente(
            commercant_id = userId,
            client = Client(
                nom = nomClient,
                telephone = tel,
                position = PositionGPS(latitude = lat, longitude = lng)
            ),
            produits = produitsChoisis.value,
            total = totalVente,
            date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        )

        viewModelScope.launch {
            isLoading = true
            val result = repository.enregistrerVente(nouvelleVente)
            isLoading = false

            if (result.isSuccess) {
                statusMessage = "Vente enregistrée avec succès !"
                produitsChoisis.value = emptyList() // Réinitialiser le panier
            } else {
                statusMessage = "Échec de l'enregistrement : ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun chargerHistorique() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.ecouterVentes(userId).collect { ventes ->
                listeVentes.value = ventes.sortedByDescending { it.date }
            }
        }
    }

    init {
        if (currentUser != null) {
            chargerHistorique()
        }
    }
}