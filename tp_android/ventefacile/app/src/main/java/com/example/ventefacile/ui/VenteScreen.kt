package com.example.ventefacile.ui

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ventefacile.viewmodel.MainViewModel
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun VenteScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var nomClient by remember { mutableStateOf("") }
    var telClient by remember { mutableStateOf("") }
    var nomProduit by remember { mutableStateOf("") }
    var prixProduit by remember { mutableStateOf("") }
    var qteProduit by remember { mutableStateOf("") }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            // Permission accordée
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nouvelle Vente", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = nomClient, onValueChange = { nomClient = it }, label = { Text("Nom du Client") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = telClient, onValueChange = { telClient = it }, label = { Text("Téléphone (Optionnel)") }, modifier = Modifier.fillMaxWidth())

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Ajouter des produits", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = nomProduit, onValueChange = { nomProduit = it }, label = { Text("Produit") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = prixProduit, onValueChange = { prixProduit = it }, label = { Text("Prix") }, modifier = Modifier.weight(0.5f))
            OutlinedTextField(value = qteProduit, onValueChange = { qteProduit = it }, label = { Text("Qté") }, modifier = Modifier.weight(0.4f))
        }

        Button(
            onClick = {
                if (nomProduit.isNotBlank() && prixProduit.isNotBlank()) {
                    viewModel.ajouterProduit(nomProduit, prixProduit.toDouble(), qteProduit.toIntOrNull() ?: 1)
                    nomProduit = ""; prixProduit = ""; qteProduit = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) { Text("Ajouter au panier") }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.produitsChoisis.value) { p ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(" • ${p.nom} x${p.quantite} : ${p.prix_unitaire * p.quantite} FCFA", modifier = Modifier.padding(8.dp))
                }
            }
        }

        Text("Total : ${viewModel.totalVente} FCFA", style = MaterialTheme.typography.headlineSmall)

        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        val lat = location?.latitude ?: 0.0
                        val lng = location?.longitude ?: 0.0
                        viewModel.validerVente(nomClient, telClient, lat, lng)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = nomClient.isNotBlank() && viewModel.produitsChoisis.value.isNotEmpty()
            ) {
                Text("Enregistrer la Vente")
            }
        }

        if (viewModel.statusMessage.isNotBlank()) {
            Text(viewModel.statusMessage, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
        }
    }
}