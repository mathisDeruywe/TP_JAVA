package com.example.ventefacile.models

import com.google.firebase.database.PropertyName

data class PositionGPS(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Client(
    val nom: String = "",
    val telephone: String = "",
    val position: PositionGPS = PositionGPS()
)

data class Produit(
    val id: String = "",
    val nom: String = "",
    val quantite: Int = 0,
    @get:PropertyName("prix_unitaire") @set:PropertyName("prix_unitaire")
    var prix_unitaire: Double = 0.0
)

data class Vente(
    @get:PropertyName("commercant_id") @set:PropertyName("commercant_id")
    var commercant_id: String = "",
    val client: Client = Client(),
    val produits: List<Produit> = emptyList(),
    val total: Double = 0.0,
    val date: String = ""
)